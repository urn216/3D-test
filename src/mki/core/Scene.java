package mki.core;

import mki.math.tri.Tri3D;

import mki.math.vector.Vector2;
import mki.math.vector.Vector3;
import mki.math.vector.Vector3I;
import mki.world.Material;
import mki.world.Model;
import mki.world.RigidBody;
import mki.world.object.loaded.*;
import mki.world.object.primitive.Cube;
import mki.world.object.primitive.Quad;

abstract class Scene {

  public static RigidBody[] empty() {
    return new RigidBody[] {new RigidBody(new Vector3(), new Model(new Vector3[0], new Tri3D[0], new Vector2[0])) {}};
  }

  public static RigidBody[] spheres() {
    return new RigidBody[] {
      new Sphere(new Vector3(-1, -1, 2.5), 0.1, new Material(~0, 0f, new Vector3(0, 10, 20), "decal/degrad.png")),
      new Sphere(new Vector3(4000, 10000, 1000), 800, new Material(~0, 0f, new Vector3(Integer.MAX_VALUE))),
      new Sphere(new Vector3(0, 0, 10), 1, new Material(new java.awt.Color(255, 140, 1), 0.9f)),
      new Sphere(new Vector3(-3, 0, 8), 1, new Material(new java.awt.Color(1, 140, 255), 0.5f, new Vector3(), "env/cobblestone.png", "env/cobblestone_normal.png")),
      new Sphere(new Vector3(-4, -3, 50), 20, new Material(new java.awt.Color(1, 255, 114), 0.99f)),
      new Sphere(new Vector3(4, 3, 13), 1, new Material(new java.awt.Color(1, 255, 1), 0.5f)),
      new Sphere(new Vector3(0, -100, 0), 95, new Material(new java.awt.Color(100, 1, 255), 0.3f, new Vector3(), "env/test.png", "env/test_normal.png")),
      new Sphere(new Vector3(10, -5  , 15), 2  , new Material(new java.awt.Color(255, 200, 50), 0f  , new Vector3(), "env/shapesbw.png", "env/shapes_normal.png")),
      new Sphere(new Vector3(10, -2.6, 15), 1.5, new Material(new java.awt.Color(255, 200, 50), 0.4f  , new Vector3(), "env/shapesbw.png", "env/shapes_normal.png")),
      new Sphere(new Vector3(10, -0.6, 15), 1  , new Material(new java.awt.Color(255, 200, 50), 0.7f, new Vector3(), "env/shapesbw.png", "env/shapes_normal.png")),
      new Sphere(new Vector3(6, -2, 10), 0.6  , new Material(~0, 1f)),
      new Sphere(new Vector3(7, -2.4, 11), 0.6  , new Material(0xFF1F1F1F, 0.9f)),
      new LowPoly(new Vector3(0.5, -1.2, 4.3), new Material(~0, 0.4f, new Vector3(), "decal/degrad.png"))
    };
  };

  public static RigidBody[] gunship() {
    return new RigidBody[] {
      new Sphere(new Vector3(-1, -1, 2.5), 0.1, new Material(~0, 0f, new Vector3(0, 5, 10))),
      new Sphere(new Vector3(4000, 10000, 1000), 800, new Material(~0, 0f, new Vector3(Integer.MAX_VALUE))),
      new Dropship(new Vector3(0, 0, 40), new Material(~0, 0f, new Vector3(), "env/test.png", "env/test_normal.png"))
    };
  };

  public static RigidBody[] roomBright() {
    return new RigidBody[] {
      new Sphere(new Vector3(-1, -1, 2.5), 0.1, new Material(~0, 0f, new Vector3(1000000000))),
      new Room(new Vector3(0, 0, 0), new Material(~0, 0.2f))
    };
  };

  public static RigidBody[] heightMap() {
    return new RigidBody[] {
      new Sphere(new Vector3(-1, -1, 2.5), 0.1, new Material(~0, 0f, new Vector3(0, 5, 10))),
      new Sphere(new Vector3(4000, 10000, 1000), 800, new Material(~0, 0f, new Vector3(Integer.MAX_VALUE))),
      new Map(new Vector3(0, -28, 0), 1, new Material(new java.awt.Color(150, 150, 150), 0f, new Vector3(), "env/terrain.png")),
      // new RigidBody(new Vector3(), Model.generateMesh("chunk.obj")) {
      //   {model.setMat(new Material(new Vector3I(150, 150, 150), 0f, new Vector3(), "env/terrain_map.png"));model.calculateRadius();}
      // }
      // new Cube(new Vector3(), 1, new Material(new Vector3I(150, 150, 150), 0f, new Vector3(), "env/terrain.png"))
    };
  };

  public static RigidBody[] voxelMap() {
    return new RigidBody[] {
      new Sphere(new Vector3(-1, -1, 2.5), 0.1, new Material(~0, 0f, new Vector3(0, 5, 10))),
      new Sphere(new Vector3(4000, 10000, 1000), 800, new Material(~0, 0f, new Vector3(Integer.MAX_VALUE))),
      new MapBlock(new Vector3(0, -28, 0), 1, new Material(new java.awt.Color(150, 150, 150), 0f, new Vector3(), "env/terrain.png")),
      // new Cube(new Vector3(), 1, new Material(new Vector3I(150, 150, 150), 0f, new Vector3(), "env/terrain.png"))
    };
  };

  public static RigidBody[] threeSpheres() {
    return new RigidBody[] {
      new Sphere(new Vector3(0, 0, 5), 1, new Material(new java.awt.Color(255, 1, 128))),
      new Sphere(new Vector3(0, 0, 2), 0.3, new Material(new java.awt.Color(1, 128, 255))),
      new Sphere(new Vector3(1, 0, 3), 0.6, new Material(new java.awt.Color(128, 255, 1))),
      new Sphere(new Vector3(4000, 10000, 1000), 800, new Material(~0, 0f, new Vector3(Integer.MAX_VALUE))),
      // new LowPoly(new Vector3(0.5, -1.2, 4.3), new Material(new Vector3(255, 255, 255), 0.4f, new Vector3(1)))
    };
  };

  public static RigidBody[] aspRatDebug() {
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

  public static RigidBody[] roomReflection() {
    Vector3I white  = new Vector3I(255, 255, 255);
    int whiteBase   = ~0;
    Vector3I purple = new Vector3I(255, 120, 255);
    int purpleBase  = new java.awt.Color(purple.x, purple.y, purple.z).getRGB();
    Vector3I gold   = new Vector3I(255, 200,  50);
    int goldBase    = new java.awt.Color(gold  .x, gold  .y, gold  .z).getRGB();
    Vector3I green  = new Vector3I(120, 255, 120);
    int greenBase   = new java.awt.Color(green .x, green .y, green .z).getRGB();

    return new RigidBody[] {
      new Sphere(new Vector3( 9  ,  0  ,  0), 0.1, new Material(greenBase , 0f  , green.scale(1.5))),
      new Sphere(new Vector3( 0  ,  2.9,  6), 0.1, new Material(whiteBase , 0f  , white )),
      new Sphere(new Vector3( 0  ,  2.9, -6), 0.1, new Material(whiteBase , 0f  , white )),
      new Sphere(new Vector3( 7  ,  2  ,  5), 0.1, new Material(purpleBase, 0f  , purple)),
      new Sphere(new Vector3(-8.9,  2.4,  0), 0.1, new Material(purpleBase, 0f  , purple)),
      new Sphere(new Vector3( 0  ,  0  ,  0), 1  , new Material(goldBase  , 0.6f, new Vector3(), "env/gold_block.png", "env/gold_block_normal.png")),
      new Room  (new Vector3( 0  ,  0  ,  0),      new Material(whiteBase , 0f  , new Vector3()))
    };
  };

  public static RigidBody[] cubes() {
    Material bricks = new Material(~0, 0.04f, new Vector3(), "env/mossy_stone_bricks.png", "env/mossy_stone_bricks_normal.png");
    Material gold   = new Material(~0, 0.6f , new Vector3(25.5, 20.0, 5.0), "env/gold_block.png", "env/gold_block_normal.png");
    Material grass  = new Material(~0, 0f   , new Vector3(), "env/grass_block_top.png", "env/grass_block_top_normal.png");
    return new RigidBody[] {
      new Cube(new Vector3( 0, -0.6, 5),  1,           gold  ),
      new Quad(new Vector3( 0, -1  , 2),  3, 1,  1, 1, bricks),
      new Quad(new Vector3( 0, -1  , 8),  3, 1,  1, 1, bricks),
      new Quad(new Vector3(-3, -1  , 5),  1, 1,  3, 1, bricks),
      new Quad(new Vector3( 3, -1  , 5),  1, 1,  3, 1, bricks),
      new Quad(new Vector3( 0, -2  , 5), 15, 1, 15, 1, grass ),
      new Sphere(new Vector3(4000, 10000, 1000), 800, new Material(~0, 0f, new Vector3(Integer.MAX_VALUE))),
    };
  };

  public static RigidBody[] cubesNM() {
    Material gold   = new Material(~0, 0.8f, new Vector3(), "env/gold_block.png", "env/gold_block_normal.png");
    Material grass  = new Material(~0, 0f, new Vector3(), "env/cobblestone.png", "env/cobblestone_normal.png");
    return new RigidBody[] {
      new Sphere(new Vector3(3, -0.6, 5), 0.2, new Material(~0, 0f, new Vector3(100))),
      new Cube(new Vector3( 0, -0.6, 5),  1,           gold  ),
      new Quad(new Vector3( 0, -2  , 5), 15, 1, 15, 1, grass ),
    };
  };
}
