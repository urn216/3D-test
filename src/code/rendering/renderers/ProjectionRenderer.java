package code.rendering.renderers;

import java.util.stream.Stream;

import code.math.tri.Tri3D;
import code.math.vector.Vector3;
import code.rendering.Drawing;
import code.world.Material;
import code.world.RigidBody;

class ProjectionRenderer extends Renderer {

  private static final double nearClippingPlane = 0.1;
  private static final double farClippingPlane  = 100;

  private static final double q = farClippingPlane/(farClippingPlane-nearClippingPlane);

  private static final Vector3 lightDir = new Vector3(1, 1.5, 0).unitize();

  private double f;

  @Override
  public void updateConstants(double fov) {
    super.updateConstants(fov);
    this.f = 1/Math.tan(fov/2);
  }

  @Override
  public void render(Drawing d, Vector3 position, Vector3 dir, Vector3 upDir, RigidBody[] bodies) {
    d.fill(-16777216);
    Stream.of(bodies).parallel().forEach((b) -> {
      Vector3 offset = b.getPosition().subtract(position);
      Stream.of(b.getModel().getFaces()).parallel().forEach((tri) -> renderTri(d, tri, offset, dir, b.getModel().getMat()));
    });
  }

  ///////////                 /////////
  ////////// SEQUENTIAL CODE //////////
  /////////                 ///////////
  //
  // @Override
  // public void render(Drawing d, Vector3 position, Vector3 dir, Vector3 upDir, RigidBody[] bodies) {
  //   d.fill(-16777216);
  //   for (int i = 0; i < bodies.length; i++) {
  //     RigidBody body = bodies[i];
  //     Vector3 offset = body.getPos().subtract(position);
  //     for (int j = 0; j < body.getFaces().length; j++) {
  //       renderTri(d, body.getFaces()[j], offset, dir, body.getMat());
  //     }
  //   }
  // }

  private void renderTri(Drawing d, Tri3D tri, Vector3 offset, Vector3 dir, Material mat) {
    Vector3 toTri = tri.getVerts()[0].add(offset);

    if (toTri.dot(tri.getNormal()) >= -0.01) return;

    Tri3D projectedTri = projectTri(tri, offset, d.getWidth(), d.getHeight(), d.getAspectRatio());
    int colour = mat.getIntenseColour(new Vector3((tri.getNormal().dot(lightDir)+1)/2));
    d.fillTri(projectedTri, colour);
    // d.drawTri(projectedTri, -16777216|~colour);
  }

  private Tri3D projectTri(Tri3D triWorld, Vector3 offset, int width, int height, double aspRat) {
    return new Tri3D(
      new Vector3[] {
        projectVector3(triWorld.getVerts()[0].add(offset), aspRat).add(1, 1, 0).scale(0.5*width-1, 0.5*height-1, 1),
        projectVector3(triWorld.getVerts()[1].add(offset), aspRat).add(1, 1, 0).scale(0.5*width-1, 0.5*height-1, 1),
        projectVector3(triWorld.getVerts()[2].add(offset), aspRat).add(1, 1, 0).scale(0.5*width-1, 0.5*height-1, 1)
      }, 
      triWorld.getVertUVs(), 
      new int[3],
      new int[3]
    );
  }

  private Vector3 projectVector3(Vector3 vecWorld, double aspRat) {
    return new Vector3(
      vecWorld.x*aspRat*f/vecWorld.z, 
      -vecWorld.y*f/vecWorld.z,
      (-nearClippingPlane/vecWorld.z+1)*q
    );
  }
}
