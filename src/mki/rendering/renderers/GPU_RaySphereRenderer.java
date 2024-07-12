package mki.rendering.renderers;

import java.util.Arrays;

import mki.math.matrix.Quaternion;
import mki.math.vector.Vector3;
import mki.math.vector.Vector4;
import mki.rendering.Drawing;
// import mki.rendering.gpumath.MathHelp;
import mki.rendering.ray.GPU_RaySphere;
import mki.world.RigidBody;

class GPU_RaySphereRenderer extends Renderer {
  
  private static final int RAY_DENSITY = 128;

  protected double offset;

  private int frame = 0;

  private Vector3[] previousFrame = null;
  
  @Override
  public void updateConstants(double fov, int width, int height) {
    super.updateConstants(fov, width, height);
    this.offset = (float)(2*NEAR_CLIPPING_PLANE/(f*width));
  }

  @Override
  public void initialise(Drawing d) {
    frame = 0;
    previousFrame = new Vector3[d.getContents().length];
    Arrays.fill(previousFrame, new Vector3());
  }

  @Override
  public void render(Drawing d, Vector3 cameraPosition, Vector4 cameraRotation, RigidBody[] bodies) {
    int canvasWidth   = d.getWidth();
    int hCanvasWidth  =   canvasWidth/2;
    int hCanvasHeight = d.getHeight()/2;
    this.frame++;
    float weight = 1f/frame;

    for (int i = 0; i < d.getContents().length; i++) {
      double pitch = -(i/canvasWidth-hCanvasHeight) * offset;
      double yaw   =  (i%canvasWidth-hCanvasWidth ) * offset;

      Vector3 pixelDir = new Vector3(yaw, pitch, NEAR_CLIPPING_PLANE).normal();
      Vector3 rayDir = Quaternion.rotate(cameraRotation, pixelDir);
      Vector3 colour = new Vector3();
      for (int j = 0; j < RAY_DENSITY; j++) 
        colour = colour.add(GPU_RaySphere.getColour(cameraPosition, rayDir, bodies));
      // out[i] = 0xFF000000|
      //   (TornadoMath.min(255,(int)(colour.getX()*255/RAY_DENSITY))<<16)|
      //   (TornadoMath.min(255,(int)(colour.getY()*255/RAY_DENSITY))<< 8)|
      //   (TornadoMath.min(255,(int)(colour.getZ()*255/RAY_DENSITY))    );

      previousFrame[i] = colour.scale(weight).add(previousFrame[i].scale(1-weight));

      d.getContents()[i] = 0xFF000000|
        (Math.min(255,(int)(previousFrame[i].x*255/RAY_DENSITY))<<16)|
        (Math.min(255,(int)(previousFrame[i].y*255/RAY_DENSITY))<< 8)|
        (Math.min(255,(int)(previousFrame[i].z*255/RAY_DENSITY))    );
    }
  }  
}
