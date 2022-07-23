
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
    tris[0] = new Tri(verts[0], verts[1], verts[2], new Vector2(0, 1), new Vector2(0, 0), new Vector2(1, 0));
    tris[1] = new Tri(verts[0], verts[2], verts[3], new Vector2(0, 1), new Vector2(1, 0), new Vector2(1, 1));
    tris[2] = new Tri(verts[3], verts[2], verts[6], new Vector2(0, 1), new Vector2(0, 0), new Vector2(1, 0));
    tris[3] = new Tri(verts[3], verts[6], verts[7], new Vector2(0, 1), new Vector2(1, 0), new Vector2(1, 1));
    tris[4] = new Tri(verts[7], verts[6], verts[5], new Vector2(0, 1), new Vector2(0, 0), new Vector2(1, 0));
    tris[5] = new Tri(verts[7], verts[5], verts[4], new Vector2(0, 1), new Vector2(1, 0), new Vector2(1, 1));
    tris[6] = new Tri(verts[4], verts[5], verts[1], new Vector2(0, 1), new Vector2(0, 0), new Vector2(1, 0));
    tris[7] = new Tri(verts[4], verts[1], verts[0], new Vector2(0, 1), new Vector2(1, 0), new Vector2(1, 1));
    tris[8] = new Tri(verts[1], verts[5], verts[6], new Vector2(0, 1), new Vector2(0, 0), new Vector2(1, 0));
    tris[9] = new Tri(verts[1], verts[6], verts[2], new Vector2(0, 1), new Vector2(1, 0), new Vector2(1, 1));
    tris[10] = new Tri(verts[4], verts[0], verts[3], new Vector2(0, 1), new Vector2(0, 0), new Vector2(1, 0));
    tris[11] = new Tri(verts[4], verts[3], verts[7], new Vector2(0, 1), new Vector2(1, 0), new Vector2(1, 1));
  }

  public Vector3 getPos() {return position;}

  public void setPos(Vector3 pos) {position = pos;}

  public void move(double x, double y, double z) {position = position.add(new Vector3(x, y, z));}

  public double getRad() {return radius;}

  public void setPos(double r) {radius = r;}

  public Tri[] getTris() {return tris;}

  public Material getMat() {return mat;}

  public void setMat(Material mat) {this.mat = mat;}
}
