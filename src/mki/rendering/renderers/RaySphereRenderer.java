package mki.rendering.renderers;

import java.util.stream.IntStream;

import mki.math.matrix.Quaternion;
import mki.math.vector.Vector3;
import mki.rendering.Drawing;
import mki.rendering.ray.RaySphere;
import mki.world.RigidBody;

class RaySphereRenderer extends Renderer {

  private double offset;
  
  private int numSteps = 0;
  private int numReflections = 2;

  @Override
  public void updateConstants(double fov, int width, int height) {
    super.updateConstants(fov, width, height);
    this.offset = 2*NEAR_CLIPPING_PLANE/(f*width);
  }

  @Override
  public void render(Drawing d, Vector3 cameraPosition, Quaternion cameraRotation, RigidBody[] bodies) {
    int canvasWidth  = d.getWidth ();
    int canvasHeight = d.getHeight();

    IntStream.range(0, canvasHeight).parallel().forEach((y) -> {
      double pitch = -(y-canvasHeight/2) * offset;
      IntStream.range(0, canvasWidth).parallel().forEach((x) -> {
        double yaw =  (x-canvasWidth /2) * offset;
        
        Vector3 pixelDir = new Vector3(yaw, pitch, NEAR_CLIPPING_PLANE).unitize();
        Vector3 rayDir = cameraRotation.rotate(pixelDir);
        d.drawPixel(x, y, RaySphere.getColour(cameraPosition, rayDir, bodies, numSteps, numReflections));
      });
    });
  }
}
