package mki.rendering.renderers;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
// import java.util.stream.IntStream;
import java.util.stream.Stream;

import mki.math.MathHelp;

import mki.math.matrix.Quaternion;

import mki.math.tri.Tri3D;

import mki.math.vector.Vector2;
import mki.math.vector.Vector3;
import mki.math.vector.Vector4;
// import mki.math.vector.Vector3I;
import mki.rendering.Constants;
import mki.rendering.Drawing;
import mki.world.Material;
import mki.world.RigidBody;

class RasterRenderer extends Renderer {

  private double offset;

  private static final Vector3[] clippingPlanes = new Vector3[4];

  private static double[] buffer;

  // private static final double EDGE_DETECTION_THRESHOLD = 0.02;

  // private static final double q = FAR_CLIPPING_PLANE/(FAR_CLIPPING_PLANE-NEAR_CLIPPING_PLANE);

  private static final double lightDistSquared = new Vector3(4000, 10000, 1000).magsquare();
  private static final Vector3 lightDir = new Vector3(0.4, 1, 0.1).normal();
  private static final Vector3 lightCol = new Vector3(Integer.MAX_VALUE);

  private static final int BACKGROUND_COLOUR = -16777216;
  private static int FOG_COLOUR = -16777216 | (120 << 16) | (200 << 8) | (255);
  private static double FOG_MIN_DIST = 1000;
  private static double FOG_STRENGTH = 0.16;

  // private static final Material white = new Material(new Vector3I(150), 0, new Vector3());
  // private static final Material red = new Material(new Vector3I(150, 0, 0), 0, new Vector3());

  @Override
  public void updateConstants(double fov, int width, int height) {
    super.updateConstants(fov, width, height);
    this.offset = 2*NEAR_CLIPPING_PLANE/(f*width);
    double sin = Math.sin(fov/2);
    double cos = Math.cos(fov/2);
    clippingPlanes[0] = new Vector3( cos, 0, sin).normal();
    clippingPlanes[1] = new Vector3(-cos, 0, sin).normal();
    clippingPlanes[2] = new Vector3(0,  cos, sin*height/width).normal();
    clippingPlanes[3] = new Vector3(0, -cos, sin*height/width).normal();

    buffer = new double[width*height];
  }

  @Override
  public void render(Drawing d, Vector3 cameraPosition, Vector4 cameraRotation, RigidBody[] bodies) {
    d.fill(BACKGROUND_COLOUR);
    if (Constants.usesFog()) generateFog(d, cameraPosition, cameraRotation);
    // d.fill(~0);

    Vector4 worldRotation = Quaternion.reverse(cameraRotation);

    Vector3[] lights = Stream.of(bodies).parallel().filter((b) -> b != null && b.getModel().getMat().isEmissive()).mapMulti((b, c) -> {
      c.accept(b.getPosition().subtract(cameraPosition));
      c.accept(b.getModel().getMat().getEmissivity());
    }).toArray((i) -> new Vector3[i]);

    //drawing objects
    Stream.of(bodies).parallel().forEach((b) -> {
      if (b == null) return;
      
      Vector3 offset = b.getPosition().subtract(cameraPosition);
      Vector3 offrot = Quaternion.rotate(worldRotation, offset);

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
        Quaternion.rotate(worldRotation, tri.getVerts()[0].add(offset)),
        Quaternion.rotate(worldRotation, tri.getVerts()[1].add(offset)),
        Quaternion.rotate(worldRotation, tri.getVerts()[2].add(offset))
      ));

      if (partial) s = s.<Tri3D>mapMulti(this::clipTri);
      
      s.forEach((tri) -> renderTri(d, tri, b.getModel().getMat(), cameraRotation, lights));

      // Alternative draw and clip combo to visualise clipping
      // if (partial) s.forEach((tri) -> clipTri(tri, (t) -> renderTri(d, t, tri==t ? white : red, cameraRotation, lights)));
    });

    // EDGE DETECTION

    // double[] currentDepths = d.getDepths();

    // int w = d.getWidth ();
    // int h = d.getHeight();
    
    // IntStream.range(0, currentDepths.length).parallel().forEach((i) -> {
    //   int x = i%w;
    //   int y = i/w;
    //   if (
    //     (x !=  0  && Math.abs(currentDepths[x-1 + ( y )*w] - currentDepths[i]) > EDGE_DETECTION_THRESHOLD) || 
    //     (x != w-1 && Math.abs(currentDepths[x+1 + ( y )*w] - currentDepths[i]) > EDGE_DETECTION_THRESHOLD) || 
    //     (y !=  0  && Math.abs(currentDepths[ x  + (y-1)*w] - currentDepths[i]) > EDGE_DETECTION_THRESHOLD) || 
    //     (y != h-1 && Math.abs(currentDepths[ x  + (y+1)*w] - currentDepths[i]) > EDGE_DETECTION_THRESHOLD)
    //   ) d.drawPixel(i, -16777216);
    // });
  }

  private void generateFog(Drawing d, Vector3 cameraPosition, Vector4 cameraRotation) {
    int canvasWidth  = d.getWidth ();
    int canvasHeight = d.getHeight();

    for (int y = 0; y < canvasHeight; y++) {
      double pitch = -(y-canvasHeight/2) * offset;
      for (int x = 0; x < canvasWidth; x++) {
        double yaw =  (x-canvasWidth /2) * offset;
        
        Vector3 pixelDir = new Vector3(yaw, pitch, NEAR_CLIPPING_PLANE).normal();
        Vector3 rayDir = Quaternion.rotate(cameraRotation, pixelDir);

        if (rayDir.y <= 0) {
          d.drawPixel(x, y, 0, BACKGROUND_COLOUR);
          buffer[x + y*canvasWidth] = 0;
        }
        else {
          double depth = rayDir.y/Math.max(rayDir.y, 5000-cameraPosition.y);
          double percent = Math.min(1, Math.pow(FOG_MIN_DIST*depth, FOG_STRENGTH));
  
          d.drawPixel(x, y, 0, Material.blendColours(BACKGROUND_COLOUR, percent, FOG_COLOUR));
          buffer[x + y*canvasWidth] = depth;
        }
      }
    }
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
  private void renderTri(Drawing d, Tri3D tri, Material mat, Vector4 cameraRotation, Vector3... lights) {
    int width  = d.getWidth ();
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

    //COLOUR

    BiFunction<Vector3, Vector2, Integer> lighting = Constants.usesDynamicRasterLighting() ?
    (v, p)->{ // dynamic lighting (full noise!)
      double z = v.z;
      Vector3 pixelWorldLocation = Quaternion.rotate(cameraRotation, new Vector3((v.x*2.0/width-1)/(z*f), -((v.y*2.0/height-1)*aspRat)/(z*f), 1.0/v.z));
      Vector3 normal = Constants.getTriNormal().apply(tri, mat, p.x, p.y);
      Vector3 intensity = mat.getEmissivity();

      for (int i = 0; i < lights.length; i+=2) {
        Vector3 lightOffset = lights[i].subtract(pixelWorldLocation);
        intensity = intensity.add(lights[i+1].scale(MathHelp.intensity(Math.max(normal.dot(lightOffset.normal()), 0), lightOffset.magsquare())));
      }

      // fudged reflections for now
      return mat.getReflection(BACKGROUND_COLOUR, intensity, p.x, p.y);
    }: !Constants.usesFog() ?
    (v, p)->{ // directional sky light (no dynamic lighting)
      return mat.getIntenseColour(
        lightCol.scale(MathHelp.intensity((Constants.getTriNormal().apply(tri, mat, p.x, p.y).dot(lightDir)+1)/2, lightDistSquared)), 
        p.x, 
        p.y
      );
    }:
    (v, p)->{ // Fog Test
      int base = mat.getIntenseColour(
        lightCol.scale(MathHelp.intensity((Constants.getTriNormal().apply(tri, mat, p.x, p.y).dot(lightDir)+1)/2, lightDistSquared)), 
        p.x, 
        p.y
      );

      double percent = Math.min(1, Math.pow(FOG_MIN_DIST*Math.max(v.z, buffer[(int)v.x + (int)v.y * d.getWidth()]), FOG_STRENGTH));

      return Material.blendColours(base, percent, FOG_COLOUR);
    };

    d.fillTri(triFlattened, lighting);

    // Catching bug for drawing out of bounds pixels (I think it's a desync between moving tris and drawing them simultaneously)
    // try {
    //   d.fillTri(triFlattened, lighting);
    // } catch (ArrayIndexOutOfBoundsException e) {
    //   System.out.println("Bad Triangle:\n  " + triFlattened.getVerts()[0] + "\n  " + triFlattened.getVerts()[1] + "\n  " + triFlattened.getVerts()[2]);
    //   System.out.println("Original:\n  " + tri.getVerts()[0] + "\n  " + tri.getVerts()[1] + "\n  " + tri.getVerts()[2]);
    //   System.out.println("Material used was " + (mat == white ? "white" : mat == red ? "red" : "the default"));
    // }

    // Draw tri outlines
    // d.drawTri(tri, -16777216|~mat.getIntenseColour(
    //   globalIllumination
    //   // .add(glowLCol.scale(MathHelp.intensity(Math.max(normal.dot(vert0), 0),distSquared)))
    // ));
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
