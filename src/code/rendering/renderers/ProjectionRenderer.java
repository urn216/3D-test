package code.rendering.renderers;

import java.util.function.Consumer;
import java.util.stream.Stream;

import mki.math.MathHelp;
import mki.math.matrix.Quaternion;
import mki.math.vector.Vector3;

import code.math.tri.Tri3D;
import code.rendering.Drawing;
import code.world.Material;
import code.world.RigidBody;

class ProjectionRenderer extends Renderer {

  private static final double nearClippingPlane = 0.1;
  private static final double farClippingPlane  = 100;
  private static final Vector3[] clippingPlanes = new Vector3[4];

  private static final double q = farClippingPlane/(farClippingPlane-nearClippingPlane);

  private static final double lightDistSquared = new Vector3(4000, 10000, 1000).magsquare();
  private static final Vector3 lightDir = new Vector3(0.4, 1, 0.1).unitize();
  private static final Vector3 lightCol = new Vector3(Integer.MAX_VALUE);
  private static final Vector3 glowLCol  = new Vector3(25, 25, 50);

  private double f;

  @Override
  public void updateConstants(double fov, int width, int height) {
    super.updateConstants(fov, width, height);
    this.f = 1/Math.tan(fov/2);
    double sin = Math.sin(fov/2);
    double cos = Math.cos(fov/2);
    clippingPlanes[0] = new Vector3( cos, 0, sin).unitize();
    clippingPlanes[1] = new Vector3(-cos, 0, sin).unitize();
    clippingPlanes[2] = new Vector3(0,  cos, sin*height/width).unitize();
    clippingPlanes[3] = new Vector3(0, -cos, sin*height/width).unitize();
  }

  @Override
  public void render(Drawing d, Vector3 cameraPosition, Quaternion cameraRotation, RigidBody[] bodies) {
    d.fill(-16777216);

    Quaternion worldRotation = cameraRotation.reverse();

    Vector3 lightDir = worldRotation.rotate(ProjectionRenderer.lightDir);

    //drawing objects
    Stream.of(bodies).parallel().forEach((b) -> {
      Vector3 offset = b.getPosition().subtract(cameraPosition);
      Vector3 offrot = worldRotation.rotate(offset);

      //clipping object
      double rad  = b.getModel().getRadius();
      boolean partial = false;
      
      double clip = offrot.z - nearClippingPlane;
      if (clip < -rad) return;
      if (clip <= rad) partial = true;

      for (int i = 0; i < 4; i++) {
        clip = offrot.dot(clippingPlanes[i]);
        if (clip < -rad) return;
        if (clip <= rad) partial = true;
      }
      
      //drawing tris
      Stream<Tri3D> s = Stream.of(b.getModel().getFaces()).parallel()
      .filter((tri) -> tri.getVerts()[0].add(offset).dot(tri.getNormal()) < -0.01)
      .map((tri) -> new Tri3D(new Vector3[]{
          worldRotation.rotate(tri.getVerts()[0].add(offset)),
          worldRotation.rotate(tri.getVerts()[1].add(offset)),
          worldRotation.rotate(tri.getVerts()[2].add(offset))
        }, tri.getVertUVs(), tri.getVertexIndeces(), tri.getVertexTextureIndeces()));

      if (partial) s = s.<Tri3D>mapMulti(this::clipTri);
      
      s.forEach((tri) -> renderTri(d, tri, lightDir, b.getModel().getMat()));
    });
  }

  private void clipTri(Tri3D tri, Consumer<Tri3D> c) {
    for (int i = 0; i < 3; i++) {
      Vector3 vertex = tri.getVerts()[i];

      double clip = vertex.z - nearClippingPlane;
      if (clip < 0) return;

      for (int j = 0; j < 4; j++) {
        clip = vertex.dot(clippingPlanes[j]);
        if (clip < 0) return;
      }
    }
    c.accept(tri); //AAAAAAHHHH
  }

  private void renderTri(Drawing d, Tri3D tri, Vector3 lightDir, Material mat) {
    int width  = d.getWidth ();
    int height = d.getHeight();

    double aspRat = d.getAspectRatio();

    Vector3[] verts = tri.getVerts();
    Vector3 normal = tri.getNormal();
    Vector3 vert0  = verts[0].scale(-1).unitize();
    double distSquared = verts[0].magsquare();

    tri.setVerts(
      projectVector3(verts[0], aspRat).add(1, 1, 0).scale(0.5*width-1, 0.5*height-1, 1),
      projectVector3(verts[1], aspRat).add(1, 1, 0).scale(0.5*width-1, 0.5*height-1, 1),
      projectVector3(verts[2], aspRat).add(1, 1, 0).scale(0.5*width-1, 0.5*height-1, 1)
    );

    int colour = mat.getIntenseColour(
      lightCol.scale(MathHelp.intensity(Math.max(normal.dot(lightDir), 0), lightDistSquared)).add(
      glowLCol.scale(MathHelp.intensity(Math.max(normal.dot(vert0   ), 0),      distSquared))
    ));
    d.fillTri(tri, colour);
    // d.drawTri(tri, -16777216|~colour);
  }

  private Vector3 projectVector3(Vector3 vecWorld, double aspRat) {
    return new Vector3(
      vecWorld.x*f/vecWorld.z, 
      -vecWorld.y*f/(vecWorld.z*aspRat),
      (-nearClippingPlane/vecWorld.z+1)*q
    );
  }
}
