package mki.rendering.renderers;

import mki.math.matrix.Quaternion;
import mki.math.vector.Vector3;
import mki.math.vector.Vector4;
import mki.rendering.Drawing;
import mki.rendering.ray.GPU_FastSphere;
import mki.world.RigidBody;

class GPU_FastSphereRenderer extends GPU_RaySphereRenderer {
  
  @Override
  public void render(Drawing d, Vector3 cameraPosition, Vector4 cameraRotation, RigidBody[] bodies) {
    int canvasWidth   = d.getWidth();
    int hCanvasWidth  =   canvasWidth/2;
    int hCanvasHeight = d.getHeight()/2;

    for (int i = 0; i < d.getContents().length; i++) {
      double pitch = -(i/canvasWidth-hCanvasHeight) * offset;
      double yaw   =  (i%canvasWidth-hCanvasWidth ) * offset;

      Vector3 pixelDir = new Vector3(yaw, pitch, NEAR_CLIPPING_PLANE).normal();
      Vector3 rayDir = Quaternion.rotate(cameraRotation, pixelDir);
      Vector3 colour = GPU_FastSphere.getColour(cameraPosition, rayDir, bodies);
      d.getContents()[i] = 0xFF000000|
        (Math.min(255,(int)(colour.x*255))<<16)|
        (Math.min(255,(int)(colour.y*255))<< 8)|
        (Math.min(255,(int)(colour.z*255))    );
    }
  }  
}
