package code.rendering.renderers;

import code.math.tri.Tri3D;

import code.rendering.Drawing;

import code.world.RigidBody;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

import java.util.stream.Stream;

import mki.math.matrix.Quaternion;
import mki.math.vector.Vector2;
import mki.math.vector.Vector3;

class WireframeRenderer extends Renderer {

  private static final Vector3[] clippingPlanes = new Vector3[4];

  private static final int BACKGROUND_COLOUR = -16777216;

  @Override
  public void updateConstants(double fov, int width, int height) {
    super.updateConstants(fov, width, height);
    double sin = Math.sin(fov/2);
    double cos = Math.cos(fov/2);
    clippingPlanes[0] = new Vector3( cos, 0, sin).unitize();
    clippingPlanes[1] = new Vector3(-cos, 0, sin).unitize();
    clippingPlanes[2] = new Vector3(0,  cos, sin*height/width).unitize();
    clippingPlanes[3] = new Vector3(0, -cos, sin*height/width).unitize();
  }

  @Override
  public void render(Drawing d, Vector3 cameraPosition, Quaternion cameraRotation, RigidBody[] bodies) {
    d.fill(BACKGROUND_COLOUR);

    Quaternion worldRotation = cameraRotation.reverse();

    //drawing objects
    Stream.of(bodies).parallel().forEach((b) -> {
      Vector3 offset = b.getPosition().subtract(cameraPosition);
      Vector3 offrot = worldRotation.rotate(offset);

      //clipping object
      double rad  = b.getModel().getRadius();
      boolean partial = false;
      
      double clip = offrot.z - NEAR_CLIPPING_PLANE;
      if (clip < -rad) return;
      if (clip <= rad) partial = true;

      for (int i = 0; i < 4; i++) {
        clip = offrot.dot(clippingPlanes[i]);
        if (clip < -rad) return;
        if (clip <= rad) partial = true;
      }
      
      //drawing tris
      Stream<Tri3D> s = Stream.of(b.getModel().getFaces()).parallel()
      .filter((tri) -> tri.getVerts()[0].add(offset).dot(tri.getNormal()) < -0.00001)
      .map((tri) -> tri.projectVerts(
        worldRotation.rotate(tri.getVerts()[0].add(offset)),
        worldRotation.rotate(tri.getVerts()[1].add(offset)),
        worldRotation.rotate(tri.getVerts()[2].add(offset))
      ));

      if (partial) s = s.<Tri3D>mapMulti(this::clipTri);
      
      s.forEach((tri) -> renderTri(d, tri));
    });
  }

  /**
   * Clips a {@code Tri3D} against all the clipping planes of this {@code ProjectionRenderer}.
   * Each clipping plane will process the input {@code Tri3D}(s) with the following procedure:
   * <ol>
   * <li> If no vertices fall outside the clipping plane, the triangle is accepted as is and passed to the next plane. 
   * 
   * <li> If one vertex, {@code A}, falls outside the plane, we generate two new triangles {{@code B}, {@code AB'}, {@code AC'}}
   * and {{@code C}, {@code AB'}, {@code AC'}} - where {@code AB'} and {@code AC'} are the points within the triangle that 
   * intercept the plane - and pass those two on to the next plane.
   * 
   * <li> If two vertices, {@code B} and {@code C}, fall outside the plane, we generate one new triangle {{@code A}, {@code AB'}, 
   * {@code AC'}} - following the same syntax as the previous step - and pass this new triangle on to the next plane.
   * 
   * <li> If all vertices fall outside the plane the triangle is simply discarded and we move on.
   * 
   * </ol>
   * 
   * In the end, we have a set of triangles to draw to the screen which are guaranteed to not produce any unexpected behaviour 
   * when drawn.
   * 
   * @param tri The initial input tri to clip.
   * @param c The consumer with which to accept any final clipped tris.
   */
  private void clipTri(Tri3D tri, Consumer<Tri3D> c) {
    Tri3D[] iTris = new Tri3D[16];
    Tri3D[] oTris = new Tri3D[16];

    iTris[0] = tri;
    int i = 1, o = 0;
    
    for (int p = 0; p < clippingPlanes.length; p++) {
      Vector3 plane = clippingPlanes[p];
      Tri3D[] out = oTris;
      o = againstPlane(
        i, o, iTris, (v) -> v.dot(plane) < 0, (a, b) -> out[b] = a, 
        (a, b) -> -plane.dot(a)/plane.dot(b)
      );

      Tri3D[] temp = iTris;
      iTris = oTris;
      oTris = temp;
      i = o;
      o = 0;
    }

    againstPlane(
      i, o, iTris, (v) -> v.z - NEAR_CLIPPING_PLANE < 0, (a, b) -> c.accept(a),
      (a, b) -> (NEAR_CLIPPING_PLANE-a.z)/b.z
    );
  }

  private int againstPlane(int i, int o, Tri3D[] iTris, Predicate<Vector3> planeCheck, BiConsumer<Tri3D, Integer> c, BiFunction<Vector3, Vector3, Double> primer) {
    for (int t = 0; t < i; t++) {
      Vector3[] verts = iTris[t].getVerts();
      int inBoundsVerts = 3;
      int A = 0;
      int B = 0;

      for (int v = 0; v < 3; v++) {
        if (planeCheck.test(verts[v])) {
          inBoundsVerts--;
          if (inBoundsVerts >= 2) A = v;
          else B = v;
        }
      }

      switch (inBoundsVerts) {
        case 3:
          c.accept(iTris[t], o);
          o++;
          continue;
        case 2:
          clip2Verts(iTris[t], verts, A, o, c, primer);
          break;
        case 1:
          clip1Vert(iTris[t], verts, A, B, o, c, primer);
          break;
        default:
          break;
      }
      o+=inBoundsVerts;
    }
    return o;
  }

  private void clip2Verts(Tri3D tri, Vector3[] verts, int A, int o, BiConsumer<Tri3D, Integer> c, BiFunction<Vector3, Vector3, Double> primer) {
    int B = (A+1)%3;
    int C = (A+2)%3;

    Vector3 vBsA = verts[B].subtract(verts[A]);
    Vector3 vCsA = verts[C].subtract(verts[A]);
    double tAB = primer.apply(verts[A], vBsA);
    double tAC = primer.apply(verts[A], vCsA);

    Vector2[] texels = tri.getVertUVs();

    Vector3 vABPrime = verts[A].add(vBsA.scale(tAB));
    Vector3 vACPrime = verts[A].add(vCsA.scale(tAC));
    Vector2 uvABPrime = texels[A].add(texels[B].subtract(texels[A]).scale(tAB));
    Vector2 uvACPrime = texels[A].add(texels[C].subtract(texels[A]).scale(tAC));

    c.accept(tri.projectVerts(verts[B], vABPrime, verts[C], texels[B], uvABPrime, texels[C]), o);
    c.accept(tri.projectVerts(verts[C], vABPrime, vACPrime, texels[C], uvABPrime, uvACPrime), o+1);
  }

  private void clip1Vert(Tri3D tri, Vector3[] verts, int A, int B, int o, BiConsumer<Tri3D, Integer> c, BiFunction<Vector3, Vector3, Double> primer) {
    int C = (A+1)%3;
    C = C == B ? (A+2)%3 : C;

    Vector3 vCsA = verts[C].subtract(verts[A]);
    Vector3 vCsB = verts[C].subtract(verts[B]);
    double tAC = primer.apply(verts[A], vCsA);
    double tBC = primer.apply(verts[B], vCsB);

    Vector2[] texels = tri.getVertUVs();

    Vector3 vACPrime = verts[A].add(vCsA.scale(tAC));
    Vector3 vBCPrime = verts[B].add(vCsB.scale(tBC));
    Vector2 uvACPrime = texels[A].add(texels[C].subtract(texels[A]).scale(tAC));
    Vector2 uvBCPrime = texels[B].add(texels[C].subtract(texels[B]).scale(tBC));

    c.accept(tri.projectVerts(verts[C], vACPrime, vBCPrime, texels[C], uvACPrime, uvBCPrime), o);
  }

  /**
   * Converts a {@code Tri3D} in world-space coordinates to 2-dimensional screen-space coordinates so that it may be drawn to a
   * {@code Drawing} with the correct perspective as to imply depth. This 2-dimensional tri is then drawn.
   * 
   * @param d The {@code Drawing} to render the tri to
   * @param tri The {@code Tri3D} to render
   * @param mat The {@code Material} of the {@code RigidBody} the input tri belongs to. 
   * (This contains texture and surface information for drawing.)
   */
  private void renderTri(Drawing d, Tri3D tri) {
    int width  = d.getWidth ()-1;
    int height = d.getHeight()-1;

    double aspRat = d.getAspectRatio();

    Vector3[] verts  = tri.getVerts();
    Vector2[] vertUVs  = tri.getVertUVs();

    Tri3D triFlattened = tri.projectVerts(
      projectVector3(verts[0], aspRat).add(1, 1, 0).scale(0.5*width, 0.5*height, 1),
      projectVector3(verts[1], aspRat).add(1, 1, 0).scale(0.5*width, 0.5*height, 1),
      projectVector3(verts[2], aspRat).add(1, 1, 0).scale(0.5*width, 0.5*height, 1),
      vertUVs[0].scale(1/verts[0].z), 
      vertUVs[1].scale(1/verts[1].z), 
      vertUVs[2].scale(1/verts[2].z)
    );

    d.drawTri(triFlattened, ~0);
  }

  /**
   * Projects a {@code Vector3} from world-space coordinates to screen-space coordinates.
   * 
   * @param vecWorld The input {@code Vector3}.
   * @param aspRat The aspect ratio of the screen to project to.
   * 
   * @return the input {@code Vector3} in {@code x}-{@code y} screen coordinates with depth stored in the output {@code z}-coordinate.
   */
  private Vector3 projectVector3(Vector3 vecWorld, double aspRat) {
    return new Vector3(
      vecWorld.x*f/vecWorld.z, 
      -vecWorld.y*f/(vecWorld.z*aspRat),
      // (-NEAR_CLIPPING_PLANE/vecWorld.z+1)*q
      1/vecWorld.z
    );
  }
}
