package code.rendering.renderers;

import code.math.matrix.Matrix;
import code.math.ray.RaySphere;
import code.math.vector.Vector3;
import code.rendering.Drawing;
import code.world.RigidBody;

class RaySphereRenderer extends Renderer {

  @Override
  public void render(Drawing d, Vector3 position, Vector3 dir, Vector3 upDir, RigidBody[] bodies) {
    for (int y = 0; y < d.getHeight(); y++) {
      double percentDown = (-0.5+1.0*y/d.getHeight());
      Matrix pitchMatrix = Matrix.rotateXLocal(percentDown*fov*d.getAspectRatio(), dir);
      Vector3 vDir = pitchMatrix.multiply(dir);
      Vector3 vUpDir = pitchMatrix.multiply(upDir);
      for (int x = 0; x < d.getWidth(); x++) {
        double percentAlong = (-0.5+1.0*x/d.getWidth());
        Vector3 rayDir = Matrix.rotateLocal(percentAlong*fov, vUpDir).multiply(vDir);
        d.drawPixel(x, y, RaySphere.getCol(position, rayDir, bodies, 0, 3));
      }
    }
  }
  
}
