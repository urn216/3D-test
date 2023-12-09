package code.rendering.renderers;

import mki.math.matrix.Quaternion;
import mki.math.vector.Vector3;
import code.math.ray.RaySphere;
import code.rendering.Drawing;
import code.world.RigidBody;

class RaySphereRenderer extends Renderer {

  @Override
  public void render(Drawing d, Vector3 cameraPosition, Quaternion cameraRotation, RigidBody[] bodies) {
    // for (int y = 0; y < d.getHeight(); y++) {
    //   double percentDown = (-0.5+(double)y/d.getHeight());
    //   Matrix pitchMatrix = Matrix.rotateXLocal(percentDown*fov*d.getAspectRatio(), dir);
    //   Vector3 vDir = pitchMatrix.multiply(dir);
    //   for (int x = 0; x < d.getWidth(); x++) {
    //     double percentAlong = (-0.5+(double)x/d.getWidth());
    //     Vector3 rayDir = Matrix.rotateLocal(percentAlong*fov, upDir).multiply(vDir);
    //     d.drawPixel(x, y, RaySphere.getCol(position, rayDir, bodies, 0, 3));
    //   }
    // }
    for (int y = 0; y < d.getHeight(); y++) {
      double pitch = (-0.5+(double)y/d.getHeight())*fov*d.getAspectRatio();
      for (int x = 0; x < d.getWidth(); x++) {
        double yaw = (-0.5+(double)x/d.getWidth())*fov;
        Quaternion pixelRotation = Quaternion.fromPitchYawRoll(pitch, yaw, 0);
        Vector3 rayDir = cameraRotation.rotate(pixelRotation.rotate(new Vector3(0, 0, 1)));
        d.drawPixel(x, y, RaySphere.getCol(cameraPosition, rayDir, bodies, 0, 3));
      }
    }
  }
  
}
