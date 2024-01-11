package code.core;

import mki.math.vector.Vector2;
import mki.math.vector.Vector3;
import mki.math.vector.Vector3I;
import code.math.tri.Tri3D;
import code.world.Material;
import code.world.Model;
import code.world.RigidBody;
import code.world.object.Cube;
import code.world.object.Dropship;
import code.world.object.LowPoly;
import code.world.object.Map;
import code.world.object.MapBlock;
import code.world.object.Room;
import code.world.object.Sphere;

public abstract class Scene {

  public static RigidBody[] empty() {
    return new RigidBody[] {new RigidBody(new Vector3(), new Model(new Vector3[0], new Tri3D[0], new Vector2[0])) {}};
  }

  public static RigidBody[] s1() {
    return new RigidBody[] {
      new Sphere(new Vector3(-1, -1, 2.5), 0.1, new Material(new Vector3I(255, 255, 255), 0f, new Vector3(0, 5, 10), "decal/degrad.png")),
      new Sphere(new Vector3(4000, 10000, 1000), 80, new Material(new Vector3I(255, 255, 255), 0f, new Vector3(Integer.MAX_VALUE))),
      new Sphere(new Vector3(0, 0, 10), 1, new Material(new Vector3I(255, 140, 1), 0.9f, new Vector3())),
      new Sphere(new Vector3(-3, 0, 8), 1, new Material(new Vector3I(1, 140, 255), 0.5f, new Vector3(), "BG/debug.png")),
      new Sphere(new Vector3(-4, -3, 50), 20, new Material(new Vector3I(1, 255, 114), 0.99f, new Vector3())),
      new Sphere(new Vector3(4, 3, 13), 1, new Material(new Vector3I(1, 255, 1), 0.5f, new Vector3())),
      new Sphere(new Vector3(0, -100, 0), 95, new Material(new Vector3I(100, 1, 255), 0.3f, new Vector3(), "env/test.png")),
      new Sphere(new Vector3(10, -5, 15), 2, new Material(new Vector3I(255, 200, 50), 0f, new Vector3(), "env/shapesbw.png")),
      new Sphere(new Vector3(10, -2.6, 15), 1.5, new Material(new Vector3I(255, 200, 50), 0f, new Vector3(), "env/shapesbw.png")),
      new Sphere(new Vector3(10, -0.6, 15), 1, new Material(new Vector3I(255, 200, 50), 0.7f, new Vector3(), "env/shapesbw.png")),
      new LowPoly(new Vector3(0.5, -1.2, 4.3), new Material(new Vector3I(255, 255, 255), 0.4f, new Vector3(), "decal/degrad.png"))
    };
  };

  public static RigidBody[] s2() {
    return new RigidBody[] {
      new Sphere(new Vector3(-1, -1, 2.5), 0.1, new Material(new Vector3I(255, 255, 255), 0f, new Vector3(0, 5, 10))),
      new Sphere(new Vector3(4000, 10000, 1000), 80, new Material(new Vector3I(255, 255, 255), 0f, new Vector3(Integer.MAX_VALUE))),
      new Dropship(new Vector3(0, 0, 40), new Material(new Vector3I(255, 255, 255), 0f, new Vector3(), "env/test.png"))
    };
  };

  public static RigidBody[] s3() {
    return new RigidBody[] {
      new Sphere(new Vector3(-1, -1, 2.5), 0.1, new Material(new Vector3I(255, 255, 255), 0f, new Vector3(1000000000))),
      new Room(new Vector3(0, 0, 0), new Material(new Vector3I(255, 255, 255), 0.2f, new Vector3()))
    };
  };

  public static RigidBody[] s4() {
    return new RigidBody[] {
      new Sphere(new Vector3(-1, -1, 2.5), 0.1, new Material(new Vector3I(255, 255, 255), 0f, new Vector3(0, 5, 10))),
      new Sphere(new Vector3(4000, 10000, 1000), 80, new Material(new Vector3I(255, 255, 255), 0f, new Vector3(Integer.MAX_VALUE))),
      new Map(new Vector3(0, -28, 0), 1, new Material(new Vector3I(150, 150, 150), 0f, new Vector3()))
    };
  };

  public static RigidBody[] s4_1() {
    return new RigidBody[] {
      new Sphere(new Vector3(-1, -1, 2.5), 0.1, new Material(new Vector3I(255, 255, 255), 0f, new Vector3(0, 5, 10))),
      new Sphere(new Vector3(4000, 10000, 1000), 80, new Material(new Vector3I(255, 255, 255), 0f, new Vector3(Integer.MAX_VALUE))),
      new MapBlock(new Vector3(0, -28, 0), 1, new Material(new Vector3I(150, 150, 150), 0f, new Vector3()))
    };
  };

  public static RigidBody[] s5() {
    return new RigidBody[] {
      new Sphere(new Vector3(0, 0, 5), 1, new Material(new Vector3I(255, 1, 128), 0f, new Vector3())),
      new Sphere(new Vector3(0, 0, 2), 0.3, new Material(new Vector3I(1, 128, 255), 0f, new Vector3())),
      new Sphere(new Vector3(1, 0, 3), 0.6, new Material(new Vector3I(128, 255, 1), 0f, new Vector3())),
      new Sphere(new Vector3(4000, 10000, 1000), 80, new Material(new Vector3I(255, 255, 255), 0f, new Vector3(Integer.MAX_VALUE))),
      // new LowPoly(new Vector3(0.5, -1.2, 4.3), new Material(new Vector3(255, 255, 255), 0.4f, new Vector3(1)))
    };
  };

  public static RigidBody[] s6() {
    Vector3[] vs = {
      new Vector3(-8, -4.5, 8),
      new Vector3(-8,  4.5, 8),
      new Vector3( 8,  4.5, 8),
      new Vector3( 8, -4.5, 8),
    };
    Tri3D[] ts = {
      new Tri3D(new Vector3[] {vs[0], vs[1], vs[2]}, new Vector2[] {new Vector2(0, 1), new Vector2(0, 0), new Vector2(1, 0)}, new int[]{1,2,3}, new int[]{0,0,0}),
      new Tri3D(new Vector3[] {vs[0], vs[2], vs[3]}, new Vector2[] {new Vector2(0, 1), new Vector2(1, 0), new Vector2(1, 1)}, new int[]{1,3,4}, new int[]{0,0,0})
    };
    return new RigidBody[] {
      new RigidBody(new Vector3(), new Model(vs, ts, new Vector2[]{})) {}
    };
  };

  public static RigidBody[] s7() {
    return new RigidBody[] {
      new Cube(new Vector3(0, 0, 5), 1, new Material(new Vector3I(255, 1, 128), 0f, new Vector3(), "decal/degrad.png")),
      new Cube(new Vector3(0, 0, 2), 0.3, new Material(new Vector3I(1, 128, 255), 0f, new Vector3(), "decal/degrad.png")),
      new Sphere(new Vector3(4000, 10000, 1000), 80, new Material(new Vector3I(255, 255, 255), 0f, new Vector3(Integer.MAX_VALUE))),
      // new LowPoly(new Vector3(0.5, -1.2, 4.3), new Material(new Vector3(255, 255, 255), 0.4f, new Vector3(1)))
    };
  };
}
