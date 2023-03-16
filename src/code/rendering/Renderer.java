package code.rendering;

import code.math.vector.Vector3;

import code.world.RigidBody;

public abstract class Renderer {
  public static Renderer rayTri() {return new RayTriRenderer();}
  public static Renderer raySphere() {return new RaySphereRenderer();}

  public abstract void render(int[] dest, int width, int height, Vector3 position, Vector3 dir, Vector3 upDir, double fov, RigidBody[] bodies);
}
