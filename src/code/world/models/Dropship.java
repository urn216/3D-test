package code.world.models;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

import code.math.IOHelp;
import code.math.Vector3;
import code.world.Material;
import code.world.RigidBody;

public class Dropship implements RigidBody {

  private Vector3 position;
  private Tri[] tris;
  private Material mat;
  Vector3[] verts;

  private static final double radius = 27.1;

  public Dropship(Vector3 position, Material mat) {
    this.position = position;
    this.mat = mat;
    generateMesh();
  }

  public void generateMesh() {
    List<Vector3> vs = new ArrayList<Vector3>();
    List<Tri> fs = new ArrayList<Tri>();
    String filename = "MKinc Gunship.obj";
    List<String> allLines = IOHelp.readAllLines(filename, false);
    for (String line : allLines) {
      Scanner scan = new Scanner(line);
      String type;
      if (scan.hasNext()) {
        type = scan.next();
      }
      else {type = "gap";}
      if (type.equals("v")) {vs.add(new Vector3(scan.nextDouble(), scan.nextDouble(), scan.nextDouble()));}
      else if (type.equals("f")) {fs.add(new Tri(vs.get(scan.nextInt()-1), vs.get(scan.nextInt()-1), vs.get(scan.nextInt()-1)));}
      scan.close();
    }
    verts = vs.toArray(new Vector3[vs.size()]);
    tris = fs.toArray(new Tri[fs.size()]);
    // double biggest = 0;
    // for (Vector3 vert : verts) {
    //   double dist = vert.magnitude();
    //   if (dist > biggest) biggest = dist;
    // }
    // System.out.println("THIS: "+ biggest);
  }

  public Vector3 getPos() {return position;}

  public void setPos(Vector3 pos) {position = pos;}

  public void move(double x, double y, double z) {position = position.add(new Vector3(x, y, z));}

  public double getRad() {return radius;}

  public Tri[] getTris() {return tris;}

  public Material getMat() {return mat;}

  public void setMat(Material mat) {this.mat = mat;}
}
