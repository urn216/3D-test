package code.rendering.renderers;

import mki.math.matrix.Quaternion;
import mki.math.vector.Vector3;

import code.rendering.Drawing;
import code.world.RigidBody;

public abstract class Renderer {
  
  protected static final double NEAR_CLIPPING_PLANE = 0.1;
  protected static final double FAR_CLIPPING_PLANE  = 100;

  protected double fov;
  protected double f;

  public static Renderer rayTri() {return new RayTriRenderer();}
  public static Renderer raySphere() {return new RaySphereRenderer();}
  public static Renderer rasterizer() {return new RasterRenderer();}

  private static boolean normalMap = false;

  public static boolean usesNormalMap() {
    return normalMap;
  }

  public static void setNormalMap(boolean normalMap) {
    Renderer.normalMap = normalMap;
  }

  public void updateConstants(double fov, int width, int height) {
    this.fov = fov;
    this.f = 1/Math.tan(fov/2);
  }

  public abstract void render(Drawing d, Vector3 cameraPosition, Quaternion cameraRotation, RigidBody[] bodies);
}
