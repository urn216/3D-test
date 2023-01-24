package code.core.scene;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

import code.core.scene.models.Tri;
import code.math.IOHelp;
import code.math.Vector2;
import code.math.Vector3;

public abstract class Model implements RigidBody {

  protected Vector3 position;
  protected Tri[] tris;
  protected Material mat;
  Vector3[] verts;

  public void generateMesh(String model) {
    List<Vector3> vs = new ArrayList<Vector3>();
    List<Vector2> vts = new ArrayList<Vector2>();
    List<Tri> fs = new ArrayList<Tri>();
    String filename = "data/" + model;
    List<String> allLines = IOHelp.readAllLines(filename, false);
    for (String line : allLines) {
      Scanner scan = new Scanner(line);
      scan.useDelimiter("[/ ]+");
      String type;
      if (scan.hasNext()) {
        type = scan.next();
      }
      else {type = "gap";}
      if (type.equals("v")) {vs.add(new Vector3(scan.nextDouble(), scan.nextDouble(), scan.nextDouble()));}
      else if (type.equals("vt")) {vts.add(new Vector2(scan.nextDouble(), scan.nextDouble()));}
      else if (type.equals("f")) {
        int a = scan.nextInt()-1, A = scan.nextInt()-1, b = scan.nextInt()-1, B = scan.nextInt()-1, c = scan.nextInt()-1, C = scan.nextInt()-1;
        fs.add(new Tri(vs.get(a), vs.get(b), vs.get(c), vts.get(A), vts.get(B), vts.get(C)));
      }
      scan.close();
    }
    verts = vs.toArray(new Vector3[vs.size()]);
    tris = fs.toArray(new Tri[fs.size()]);
  }

  public double calculateRadius() {
    double biggest = 0;
    for (Vector3 vert : verts) {
      double dist = vert.magnitude();
      if (dist > biggest) biggest = dist;
    }
    System.out.println("RADIUS   : "+ biggest);
    System.out.println("NUM VERTS: "+ verts.length);
    return biggest;
  }

  public Vector3 getPos() {return position;}

  public void setPos(Vector3 pos) {position = pos;}

  public void move(double x, double y, double z) {position = position.add(new Vector3(x, y, z));}

  public double getRad() {return 1;}

  public Tri[] getTris() {return tris;}

  public Material getMat() {return mat;}

  public void setMat(Material mat) {this.mat = mat;}
}
