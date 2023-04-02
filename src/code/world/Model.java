package code.world;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

import code.math.IOHelp;
import code.math.tri.Tri3D;
import code.math.vector.Vector2;
import code.math.vector.Vector3;

public class Model {

  protected final Vector3[] verts;
  protected final Tri3D[]     faces;
  protected final Vector2[] vertUVs;

  protected Material mat = new Material(new Vector3(255), 0, new Vector3());

  protected double radius = 1;

  public Model(Vector3[] verts, Tri3D[] faces, Vector2[] vertUVs) {
    if (verts == null || faces == null || vertUVs == null) throw new RuntimeException("3D models cannot have null fields");
    this.verts   = verts;
    this.faces   = faces;
    this.vertUVs = vertUVs;
  }

  public static Model generateMesh(String model) {
    List<Vector3> vs = new ArrayList<Vector3>();
    List<Vector2> vts = new ArrayList<Vector2>();
    List<Tri3D> fs = new ArrayList<Tri3D>();
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
        if (vts.isEmpty()) {
          int a = scan.nextInt(), b = scan.nextInt(), c = scan.nextInt();
          fs.add(new Tri3D(
            new Vector3[]{vs.get(a-1), vs.get(b-1), vs.get(c-1)}, 
            new int[]{a, b, c}
          ));
        }
        else {
          int a = scan.nextInt(), A = scan.nextInt(), b = scan.nextInt(), B = scan.nextInt(), c = scan.nextInt(), C = scan.nextInt();
          fs.add(new Tri3D(
            new Vector3[]{vs.get(a-1), vs.get(b-1), vs.get(c-1)}, 
            new Vector2[]{vts.get(A-1), vts.get(B-1), vts.get(C-1)}, 
            new int[]{a, b, c}, 
            new int[]{A, B, C}
          ));
        }
      }
      scan.close();
    }
    System.out.println("Successfully loaded Model");
    return new Model(vs.toArray(new Vector3[vs.size()]), fs.toArray(new Tri3D[fs.size()]), vts.toArray(new Vector2[vts.size()]));
  }

  public double calculateRadius() {
    double biggest = 0;
    for (Vector3 vert : verts) {
      double dist = vert.magnitude();
      if (dist > biggest) biggest = dist;
    }
    System.out.println("RADIUS   : "+ biggest);
    System.out.println("NUM VERTS: "+ verts.length);
    this.radius = biggest;
    return biggest;
  }

  public Tri3D[] getFaces() {return faces;}

  public void setRadius(double radius) {this.radius = radius;}

  public double getRadius() {return radius;}

  public Material getMat() {return mat;}

  public void setMat(Material mat) {this.mat = mat;}

  public String toString() {
    StringBuilder res = new StringBuilder(1000);
    res.append("o " + this.getClass().getSimpleName() + "\n");
    for (Vector3 v : verts) res.append("v " + v.x + " " + v.y + " " + v.z + "\n");
    for (Vector2 vt : vertUVs) res.append("vt " + vt.x + " " + vt.y + "\n");
    res.append("s off\n");
    for (Tri3D f : faces) res.append("f " + f.toString() + "\n");
    return res.toString();
  }
}
