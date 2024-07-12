package mki.rendering.renderers;

import mki.math.matrix.Quaternion;
import mki.math.vector.Vector3;
import mki.rendering.Drawing;
// import mki.rendering.gpumath.MathHelp;
import mki.rendering.ray.GPU_RaySphere;
import mki.world.Material;
import mki.world.RigidBody;
import uk.ac.manchester.tornado.api.ImmutableTaskGraph;
import uk.ac.manchester.tornado.api.TaskGraph;
import uk.ac.manchester.tornado.api.TornadoExecutionPlan;
import uk.ac.manchester.tornado.api.annotations.Parallel;
import uk.ac.manchester.tornado.api.enums.DataTransferMode;
import uk.ac.manchester.tornado.api.exceptions.TornadoExecutionPlanException;
import uk.ac.manchester.tornado.api.math.TornadoMath;
import uk.ac.manchester.tornado.api.types.arrays.FloatArray;
import uk.ac.manchester.tornado.api.types.arrays.IntArray;
import uk.ac.manchester.tornado.api.types.collections.VectorFloat3;
import uk.ac.manchester.tornado.api.types.collections.VectorFloat4;
import uk.ac.manchester.tornado.api.types.vectors.Float3;
import uk.ac.manchester.tornado.api.types.vectors.Float4;

class GPU_RaySphereRenderer extends Renderer {

  protected float offset;
  
  protected final VectorFloat4 camera = new VectorFloat4(3);

  protected VectorFloat4 loadedBodies = new VectorFloat4(RigidBody.getNumberOfBodies()*4);
  
  protected TornadoExecutionPlan executor = null;
  
  @Override
  public void updateConstants(double fov, int width, int height) {
    super.updateConstants(fov, width, height);
    this.offset = (float)(2*NEAR_CLIPPING_PLANE/(f*width));

    camera.set(FloatArray.fromElements(
      0, 0, 0, 0,
      0, 0, 0, 1,
      (float)NEAR_CLIPPING_PLANE, (float)FAR_CLIPPING_PLANE, offset, 0
    ));
  }

  @Override
  public void initialise(Drawing d) {

    destroy();

    int[] out = d.getContents();

    IntArray in = d.getDims();

    in.set(2, 0);

    loadedBodies = new VectorFloat4(RigidBody.getNumberOfBodies()*4);

    VectorFloat3 previousFrame = new VectorFloat3(out.length);

    TaskGraph taskGraph = new TaskGraph("GPU_Ray_Sphere")
            .transferToDevice(DataTransferMode.FIRST_EXECUTION, out, previousFrame)
            .transferToDevice(DataTransferMode.EVERY_EXECUTION, camera, loadedBodies, in)
            .task("Render", GPU_RaySphereRenderer::rendergpu, out, in, previousFrame, camera, loadedBodies)
            .transferToHost(DataTransferMode.EVERY_EXECUTION, out);
            
    ImmutableTaskGraph immutableTaskGraph = taskGraph.snapshot();
    executor = new TornadoExecutionPlan(immutableTaskGraph);
  }

  @Override
  public void destroy() {
    if (executor == null) return;
    try {
      executor.close();
      executor = null;
    } catch (TornadoExecutionPlanException e) {
      e.printStackTrace();
    }
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
      loadedBodies.set(i*4+3, new Float4((float)Math.min(m.getEmissivity().x, 255), (float)Math.min(m.getEmissivity().y, 255), (float)Math.min(m.getEmissivity().z, 255), m.isEmissive() ? 1 : 0));
    }

    camera.set(0, new Float4((float)cameraPosition  .x, (float)cameraPosition  .y, (float)cameraPosition  .z, 0));
    camera.set(1, new Float4((float)cameraRotation.q.x, (float)cameraRotation.q.y, (float)cameraRotation.q.z, (float)cameraRotation.q.w));

    d.getDims().set(2, d.getDims().get(2) + 1);

    executor.execute();                                            //Run on GPU
    // rendergpu(d.getContents(), d.getDims(), camera, loadedBodies); //Run on CPU
  }

  // private static void renderRand(int[] out, IntArray in, VectorFloat4 cam, VectorFloat4 bodies) {
  //   for (@Parallel int i = 0; i < out.length; i++) out[i] = 0xFF000000 | ((int)(255*MathHelp.pseudoRandom(i)));
  // }

  private static final int RAY_DENSITY = 512;

  private static void rendergpu(int[] out, IntArray in, VectorFloat3 prev, VectorFloat4 cam, VectorFloat4 bodies) {
    int canvasWidth   = in.get(0);
    int hCanvasWidth  = canvasWidth/2;
    int hCanvasHeight = in.get(1)  /2;
    int frame = in.get(2);
    float weight = 1f/frame;

    Float3 cameraPosition = cam.get(0).asFloat3();
    Float4 cameraRotation = cam.get(1);
    
    float offset   = cam.get(2).get(2);
    float nearClip = cam.get(2).get(0);

    for (@Parallel int i = 0; i < out.length; i++) {
      float pitch = -(i/canvasWidth-hCanvasHeight) * offset;
      float yaw   =  (i%canvasWidth-hCanvasWidth ) * offset;

      Float3 pixelDir = Float3.normalise(new Float3(yaw, pitch, nearClip));
      Float3 rayDir = mki.rendering.gpumath.Quaternion.rotate(cameraRotation, pixelDir);
      Float3 colour = new Float3();
      for (int j = 0; j < RAY_DENSITY; j++) 
        colour = Float3.add(colour, GPU_RaySphere.getColour(cameraPosition, rayDir, bodies, j+frame*RAY_DENSITY));
      // out[i] = 0xFF000000|
      //   (TornadoMath.min(255,(int)(colour.getX()*255/RAY_DENSITY))<<16)|
      //   (TornadoMath.min(255,(int)(colour.getY()*255/RAY_DENSITY))<< 8)|
      //   (TornadoMath.min(255,(int)(colour.getZ()*255/RAY_DENSITY))    );

      prev.set(i, Float3.add(Float3.mult(colour, weight), Float3.mult(prev.get(i), 1-weight)));

      out[i] = 0xFF000000|
        (TornadoMath.min(255,(int)(prev.get(i).getX()*255/RAY_DENSITY))<<16)|
        (TornadoMath.min(255,(int)(prev.get(i).getY()*255/RAY_DENSITY))<< 8)|
        (TornadoMath.min(255,(int)(prev.get(i).getZ()*255/RAY_DENSITY))    );
    }
  }
  
}
