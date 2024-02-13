package code.rendering.renderers;

import code.rendering.Drawing;

import code.world.RigidBody;

import mki.math.matrix.Quaternion;
import mki.math.vector.Vector3;

public abstract class Renderer {
  
  protected static final double NEAR_CLIPPING_PLANE = 0.1;
  protected static final double FAR_CLIPPING_PLANE  = 100;

  protected double fov;
  protected double f;

  public static Renderer rayTri() {return new RayTriRenderer();}
  public static Renderer raySphere() {return new RaySphereRenderer();}
  public static Renderer rasterizer() {return new RasterRenderer();}
  public static Renderer motionSensor() {return new MotionRenderer();}
  public static Renderer wireframe() {return new WireframeRenderer();}

  public void updateConstants(double fov, int width, int height) {
    this.fov = fov;
    this.f = 1/Math.tan(fov/2);
  }

  public void initialise(Drawing d) {}

  public abstract void render(Drawing d, Vector3 cameraPosition, Quaternion cameraRotation, RigidBody[] bodies);
}
