package code.rendering;

import code.math.matrix.Matrix;
import code.math.rays.RaySphere;
import code.math.vector.Vector3;

import code.world.RigidBody;

class RaySphereRenderer extends Renderer {

  @Override
  public void render(int[] dest, int width, int height, Vector3 position, Vector3 dir, Vector3 upDir, double fov, RigidBody[] bodies) {
    double aspRat = 1.0*height/width;

    for (int y = 0; y < height; y++) {
      double percentDown = (-0.5+1.0*y/height);
      Matrix pitchMatrix = Matrix.rotateXLocal(Math.toRadians(percentDown*fov*aspRat), dir);
      Vector3 vDir = pitchMatrix.multiply(dir);
      Vector3 vUpDir = pitchMatrix.multiply(upDir);
      for (int x = 0; x < width; x++) {
        double percentAlong = (-0.5+1.0*x/width);
        Vector3 rayDir = Matrix.rotateLocal(Math.toRadians(percentAlong*fov), vUpDir).multiply(vDir);
        dest[(x)+(y)*width] = RaySphere.getCol(position, rayDir, bodies, 0, 3);
      }
    }
  }
  
}
