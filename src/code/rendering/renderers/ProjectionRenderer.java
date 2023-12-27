package code.rendering.renderers;

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

    for (int i = 0; i < clippingPlanes.length; i++) {
      System.out.println(clippingPlanes[i]);
    }
  }

  @Override
  public void render(Drawing d, Vector3 cameraPosition, Quaternion cameraRotation, RigidBody[] bodies) {
    d.fill(-16777216);

    Quaternion worldRotation = cameraRotation.reverse();

    Stream.of(bodies).parallel().forEach((b) -> {
      Vector3 offset = b.getPosition().subtract(cameraPosition);
      Vector3 rotoff = worldRotation.rotate(offset);
      if (
        rotoff.z - nearClippingPlane  > b.getModel().getRadius() &&
        rotoff.dot(clippingPlanes[0]) > b.getModel().getRadius() &&
        rotoff.dot(clippingPlanes[1]) > b.getModel().getRadius() &&
        rotoff.dot(clippingPlanes[2]) > b.getModel().getRadius() &&
        rotoff.dot(clippingPlanes[3]) > b.getModel().getRadius()
      )
        Stream.of(b.getModel().getFaces()).parallel().forEach((tri) -> renderTri(d, tri, offset, worldRotation, b.getModel().getMat()));
    });
  }

  private void renderTri(Drawing d, Tri3D tri, Vector3 offset, Quaternion worldRotation, Material mat) {
    Vector3 toTri = tri.getVerts()[0].add(offset);

    if (toTri.dot(tri.getNormal()) >= -0.01) return;

    Tri3D projectedTri = projectTri(tri, offset, worldRotation, d.getWidth(), d.getHeight(), d.getAspectRatio());
    int colour = mat.getIntenseColour(lightCol.scale(MathHelp.intensity(Math.max(tri.getNormal().dot(lightDir), 0), lightDistSquared)));
    d.fillTri(projectedTri, colour);
    // d.drawTri(projectedTri, -16777216|~colour);
  }

  private Tri3D projectTri(Tri3D triWorld, Vector3 offset, Quaternion worldRotation, int width, int height, double aspRat) {
    return new Tri3D(
      new Vector3[] {
        projectVector3(worldRotation.rotate(triWorld.getVerts()[0].add(offset)), aspRat).add(1, 1, 0).scale(0.5*width-1, 0.5*height-1, 1),
        projectVector3(worldRotation.rotate(triWorld.getVerts()[1].add(offset)), aspRat).add(1, 1, 0).scale(0.5*width-1, 0.5*height-1, 1),
        projectVector3(worldRotation.rotate(triWorld.getVerts()[2].add(offset)), aspRat).add(1, 1, 0).scale(0.5*width-1, 0.5*height-1, 1)
      }, 
      triWorld.getVertUVs(), 
      new int[3],
      new int[3]
    );
  }

  private Vector3 projectVector3(Vector3 vecWorld, double aspRat) {
    return new Vector3(
      vecWorld.x*f/vecWorld.z, 
      -vecWorld.y*f/(vecWorld.z*aspRat),
      (-nearClippingPlane/vecWorld.z+1)*q
    );
  }
}
