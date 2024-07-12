package mki.rendering.renderers;

import mki.math.matrix.Quaternion;
import mki.math.vector.Vector3;
import mki.rendering.Drawing;
import mki.rendering.ray.GPU_FastSphere;
import mki.world.Material;
import mki.world.RigidBody;
import uk.ac.manchester.tornado.api.ImmutableTaskGraph;
import uk.ac.manchester.tornado.api.TaskGraph;
import uk.ac.manchester.tornado.api.TornadoExecutionPlan;
import uk.ac.manchester.tornado.api.annotations.Parallel;
import uk.ac.manchester.tornado.api.enums.DataTransferMode;
import uk.ac.manchester.tornado.api.math.TornadoMath;
import uk.ac.manchester.tornado.api.types.arrays.IntArray;
import uk.ac.manchester.tornado.api.types.collections.VectorFloat4;
import uk.ac.manchester.tornado.api.types.vectors.Float3;
import uk.ac.manchester.tornado.api.types.vectors.Float4;

class GPU_FastSphereRenderer extends GPU_RaySphereRenderer {

  @Override
  public void initialise(Drawing d) {

    destroy();

    int[] out = d.getContents();

    IntArray in = d.getDims();

    loadedBodies = new VectorFloat4(RigidBody.getNumberOfBodies()*4);

    TaskGraph taskGraph = new TaskGraph("GPU_Ray_Sphere")
            .transferToDevice(DataTransferMode.FIRST_EXECUTION, out, in)
            .transferToDevice(DataTransferMode.EVERY_EXECUTION, camera, loadedBodies)
            .task("Render", GPU_FastSphereRenderer::rendergpu, out, in, camera, loadedBodies)
            .transferToHost(DataTransferMode.EVERY_EXECUTION, out);
            
    ImmutableTaskGraph immutableTaskGraph = taskGraph.snapshot();
    executor = new TornadoExecutionPlan(immutableTaskGraph);
  }

  @Override
  public void render(Drawing d, Vector3 cameraPosition, Quaternion cameraRotation, RigidBody[] bodies) {

    for (int i = 0; i < loadedBodies.getLength()/4; i++) {
      if (bodies[i] == null) continue;
      Vector3 pos = bodies[i].getPosition();
      Material m = bodies[i].getModel().getMat();
      int col = m.getBaseColour();
      
      loadedBodies.set(i*4, new Float4((float)pos.x, (float)pos.y, (float)pos.z, (float)bodies[i].getModel().getRadius()));
      loadedBodies.set(i*4+1, bodies[i].getRotation());
      loadedBodies.set(i*4+2, new Float4(((col>>16)&255)/255f, ((col>>8)&255)/255f, ((col)&255)/255f, m.getReflectivity()));
      loadedBodies.set(i*4+3, new Float4((float)m.getEmissivity().x, (float)m.getEmissivity().y, (float)m.getEmissivity().z, m.isEmissive() ? 1 : 0));
    }

    camera.set(0, new Float4((float)cameraPosition.x, (float)cameraPosition.y, (float)cameraPosition.z, 0));
    camera.set(1, new Float4((float)cameraRotation.q.x, (float)cameraRotation.q.y, (float)cameraRotation.q.z, (float)cameraRotation.q.w));

    executor.execute();                                            //Run on GPU
    // rendergpu(d.getContents(), d.getDims(), camera, loadedBodies); //Run on CPU
  }

  private static void rendergpu(int[] out, IntArray in, VectorFloat4 cam, VectorFloat4 bodies) {
    int canvasWidth   = in.get(0);
    int hCanvasWidth  = canvasWidth/2;
    int hCanvasHeight = in.get(1)  /2;

    Float3 cameraPosition = cam.get(0).asFloat3();
    Float4 cameraRotation = cam.get(1);
    
    float offset   = cam.get(2).get(2);
    float nearClip = cam.get(2).get(0);

    for (@Parallel int i = 0; i < out.length; i++) {
      float pitch = -(i/canvasWidth-hCanvasHeight) * offset;
      float yaw   =  (i%canvasWidth-hCanvasWidth ) * offset;

      Float3 pixelDir = Float3.normalise(new Float3(yaw, pitch, nearClip));
      Float3 rayDir = mki.rendering.gpumath.Quaternion.rotate(cameraRotation, pixelDir);
      Float3 colour = GPU_FastSphere.getColour(cameraPosition, rayDir, bodies);
      out[i] = 0xFF000000|
        (TornadoMath.min(255,(int)(colour.getX()*255))<<16)|
        (TornadoMath.min(255,(int)(colour.getY()*255))<< 8)|
        (TornadoMath.min(255,(int)(colour.getZ()*255))    );
    }
  }
  
}
