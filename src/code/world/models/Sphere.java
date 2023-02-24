package code.world.models;

import code.math.Vector2;
import code.math.Vector3;
import code.world.Material;
import code.world.RigidBody;
import code.world.Tri;

public class Sphere implements RigidBody {

  Vector3 position;
  double radius;
  Tri[] tris = new Tri[12];
  Material mat;
  Vector3[] verts = new Vector3[8];

  public Sphere(Vector3 position, double radius, Material mat) {
    this.position = position;
    this.radius = radius;
    this.mat = mat;
    double sRad = radius*Math.sqrt(3)/3;
    verts[0] = new Vector3(-sRad, -sRad, -sRad);
    verts[1] = new Vector3(-sRad, sRad, -sRad);
    verts[2] = new Vector3(sRad, sRad, -sRad);
    verts[3] = new Vector3(sRad, -sRad, -sRad);
    verts[4] = new Vector3(-sRad, -sRad, sRad);
    verts[5] = new Vector3(-sRad, sRad, sRad);
    verts[6] = new Vector3(sRad, sRad, sRad);
    verts[7] = new Vector3(sRad, -sRad, sRad);
    generateMesh();
  }

  public void generateMesh() {
    tris[0]  = new Tri(new Vector3[] {verts[0], verts[1], verts[2]}, new Vector2[] {new Vector2(0, 1), new Vector2(0, 0), new Vector2(1, 0)}, new int[]{1,2,3}, new int[]{0,0,0});
    tris[1]  = new Tri(new Vector3[] {verts[0], verts[2], verts[3]}, new Vector2[] {new Vector2(0, 1), new Vector2(1, 0), new Vector2(1, 1)}, new int[]{1,3,4}, new int[]{0,0,0});
    tris[2]  = new Tri(new Vector3[] {verts[3], verts[2], verts[6]}, new Vector2[] {new Vector2(0, 1), new Vector2(0, 0), new Vector2(1, 0)}, new int[]{4,3,7}, new int[]{0,0,0});
    tris[3]  = new Tri(new Vector3[] {verts[3], verts[6], verts[7]}, new Vector2[] {new Vector2(0, 1), new Vector2(1, 0), new Vector2(1, 1)}, new int[]{4,7,8}, new int[]{0,0,0});
    tris[4]  = new Tri(new Vector3[] {verts[7], verts[6], verts[5]}, new Vector2[] {new Vector2(0, 1), new Vector2(0, 0), new Vector2(1, 0)}, new int[]{8,7,6}, new int[]{0,0,0});
    tris[5]  = new Tri(new Vector3[] {verts[7], verts[5], verts[4]}, new Vector2[] {new Vector2(0, 1), new Vector2(1, 0), new Vector2(1, 1)}, new int[]{8,6,5}, new int[]{0,0,0});
    tris[6]  = new Tri(new Vector3[] {verts[4], verts[5], verts[1]}, new Vector2[] {new Vector2(0, 1), new Vector2(0, 0), new Vector2(1, 0)}, new int[]{5,6,2}, new int[]{0,0,0});
    tris[7]  = new Tri(new Vector3[] {verts[4], verts[1], verts[0]}, new Vector2[] {new Vector2(0, 1), new Vector2(1, 0), new Vector2(1, 1)}, new int[]{5,2,1}, new int[]{0,0,0});
    tris[8]  = new Tri(new Vector3[] {verts[1], verts[5], verts[6]}, new Vector2[] {new Vector2(0, 1), new Vector2(0, 0), new Vector2(1, 0)}, new int[]{2,6,7}, new int[]{0,0,0});
    tris[9]  = new Tri(new Vector3[] {verts[1], verts[6], verts[2]}, new Vector2[] {new Vector2(0, 1), new Vector2(1, 0), new Vector2(1, 1)}, new int[]{2,7,3}, new int[]{0,0,0});
    tris[10] = new Tri(new Vector3[] {verts[4], verts[0], verts[3]}, new Vector2[] {new Vector2(0, 1), new Vector2(0, 0), new Vector2(1, 0)}, new int[]{5,1,4}, new int[]{0,0,0});
    tris[11] = new Tri(new Vector3[] {verts[4], verts[3], verts[7]}, new Vector2[] {new Vector2(0, 1), new Vector2(1, 0), new Vector2(1, 1)}, new int[]{5,4,8}, new int[]{0,0,0});
  }

  public Vector3 getPos() {return position;}

  public void setPos(Vector3 pos) {position = pos;}

  public void move(double x, double y, double z) {position = position.add(new Vector3(x, y, z));}

  public double getRad() {return radius;}

  public void setPos(double r) {radius = r;}

  public Tri[] getFaces() {return tris;}

  public Material getMat() {return mat;}

  public void setMat(Material mat) {this.mat = mat;}
}