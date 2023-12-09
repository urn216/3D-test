package code.rendering.renderers;

import mki.math.matrix.Quaternion;
import mki.math.vector.Vector3;

import code.rendering.Drawing;
import code.world.RigidBody;

public abstract class Renderer {

  protected double fov;

  public static Renderer rayTri() {return new RayTriRenderer();}
  public static Renderer raySphere() {return new RaySphereRenderer();}
  public static Renderer projection() {return new ProjectionRenderer();}

  public void updateConstants(double fov) {
    this.fov = fov;
  }

  public abstract void render(Drawing d, Vector3 cameraPosition, Quaternion cameraRotation, RigidBody[] bodies);
}
