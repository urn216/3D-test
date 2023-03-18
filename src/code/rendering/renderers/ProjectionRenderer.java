package code.rendering.renderers;

import code.math.tri.Tri2D;
import code.math.tri.Tri3D;
import code.math.vector.Vector2;
import code.math.vector.Vector3;
import code.rendering.Drawing;
import code.world.Material;
import code.world.RigidBody;

class ProjectionRenderer extends Renderer {

  private static final double nearClippingPlane = 0.1;
  private static final double farClippingPlane  = 1000;

  private static final double q = farClippingPlane/(farClippingPlane-nearClippingPlane);

  private double f;

  @Override
  public void updateConstants(double fov) {
    super.updateConstants(fov);
    this.f = 1/Math.tan(fov/2);
  }

  @Override
  public void render(Drawing d, Vector3 position, Vector3 dir, Vector3 upDir, RigidBody[] bodies) {
    d.fill(-16777216);
    for (int i = 0; i < bodies.length; i++) {
      RigidBody body = bodies[i];
      Vector3 offset = body.getPos().subtract(position);
      for (int j = 0; j < body.getFaces().length; j++) {
        renderTri(d, body.getFaces()[j], offset, dir, body.getMat());
      }
    }
  }

  private void renderTri(Drawing d, Tri3D tri, Vector3 offset, Vector3 dir, Material mat) {
    Vector3 toTri = tri.getVerts()[0].add(offset);

    if (tri.getNormal().dot(dir) > 0 || toTri.dot(tri.getNormal()) >= 0) return;

    Tri2D projectedTri = projectTri(tri, offset, d.getWidth(), d.getHeight(), d.getAspectRatio());
    d.fillTri(projectedTri, mat.getAbsColour());
  }

  private Tri2D projectTri(Tri3D triWorld, Vector3 offset, int width, int height, double aspRat) {
    return new Tri2D(
      new Vector2[] {
        projectVector3(triWorld.getVerts()[0].add(offset), aspRat).add(1).scale(0.5*(width-1), 0.5*(height-1)),
        projectVector3(triWorld.getVerts()[1].add(offset), aspRat).add(1).scale(0.5*(width-1), 0.5*(height-1)),
        projectVector3(triWorld.getVerts()[2].add(offset), aspRat).add(1).scale(0.5*(width-1), 0.5*(height-1))
      }, 
      triWorld.getVertUVs(), 
      0
    );
  }

  private Vector2 projectVector3(Vector3 vecWorld, double aspRat) {
    return new Vector2(
      vecWorld.x*aspRat*f/vecWorld.z, 
      -vecWorld.y*f/vecWorld.z
    );
  }
}
