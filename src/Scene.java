abstract class Scene {
  public static RigidBody[] s1() {
    RigidBody[] s = {
      new Sphere(new Vector3(-1, -1, 2.5), 0.1, new Material(new Vector3(255, 255, 255), 0f, new Vector3(0, 5, 10))),
      new Sphere(new Vector3(2000, 10000, 1000), 80, new Material(new Vector3(255, 255, 255), 0f, new Vector3(2000000000))),
      new Sphere(new Vector3(0, 0, 10), 1, new Material(new Vector3(255, 140, 1), 0.9f, new Vector3())),
      new Sphere(new Vector3(-3, 0, 8), 1, new Material(new Vector3(1, 140, 255), 0.5f, new Vector3(), "BG/debug.png")),
      new Sphere(new Vector3(-4, -3, 50), 20, new Material(new Vector3(1, 255, 114), 0.99f, new Vector3())),
      new Sphere(new Vector3(4, 3, 13), 1, new Material(new Vector3(1, 255, 1), 0.5f, new Vector3())),
      new Sphere(new Vector3(0, -100, 0), 95, new Material(new Vector3(100, 1, 255), 0.3f, new Vector3(), "env/test.png")),
      new Sphere(new Vector3(10, -5, 15), 2, new Material(new Vector3(255, 200, 50), 0f, new Vector3(), "env/shapesbw.png")),
      new Sphere(new Vector3(10, -2.6, 15), 1.5, new Material(new Vector3(255, 200, 50), 0f, new Vector3(), "env/shapesbw.png")),
      new Sphere(new Vector3(10, -0.6, 15), 1, new Material(new Vector3(255, 200, 50), 0.7f, new Vector3(), "env/shapesbw.png")),
      new LowPoly(new Vector3(0.5, -1.2, 4.3), new Material(new Vector3(255, 255, 255), 0.4f, new Vector3(), "decal/degrad.png"))
    };
    return s;
  };

  public static RigidBody[] s2() {
    RigidBody[] s = {
      new Sphere(new Vector3(-1, -1, 2.5), 0.1, new Material(new Vector3(255, 255, 255), 0f, new Vector3(0, 5, 10))),
      new Sphere(new Vector3(2000, 10000, 1000), 80, new Material(new Vector3(255, 255, 255), 0f, new Vector3(2000000000))),
      new Dropship(new Vector3(0, 0, 40), new Material(new Vector3(255, 255, 255), 0.4f, new Vector3()))
    };
    return s;
  };

  public static RigidBody[] s3() {
    RigidBody[] s = {
      new Sphere(new Vector3(-1, -1, 2.5), 0.1, new Material(new Vector3(255, 255, 255), 0f, new Vector3(1000000000))),
      new Room(new Vector3(0, 0, 0), new Material(new Vector3(255, 255, 255), 0.2f, new Vector3()))
    };
    return s;
  };
}
