package mki.world;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

import mki.io.FileIO;

import mki.math.matrix.Quaternion;

import mki.math.tri.Tri3D;

import mki.math.vector.Vector2;
import mki.math.vector.Vector3;
import mki.math.vector.Vector4;

public class Model {

  private static final Material DEFAULT_MATERIAL = new Material();

  protected final Vector3[] verts;
  protected final Vector2[] vertUVs;

  protected Tri3D[] faces;

  protected Material mat = Model.DEFAULT_MATERIAL;

  protected double radius = 1;

  public Model(Vector3[] verts, Tri3D[] faces, Vector2[] vertUVs) {
    if (verts == null || faces == null || vertUVs == null) throw new RuntimeException("3D models cannot have null fields");
    this.verts   = verts;
    this.faces   = faces;
    this.vertUVs = vertUVs;
  }

  public static Model generateMesh(String model) {
    return generateMesh(model, 1);
  }

  public static Model generateMesh(String model, double scale) {
    List<Vector3> vs = new ArrayList<Vector3>();
    List<Vector2> vts = new ArrayList<Vector2>();
    List<Tri3D> fs = new ArrayList<Tri3D>();
    String filename = "../data/" + model;
    List<String> allLines = FileIO.readAllLines(filename, false);
    for (String line : allLines) {
      Scanner scan = new Scanner(line);
      scan.useDelimiter("[/ ]+");
      String type;
      if (scan.hasNext()) {
        type = scan.next();
      }
      else {type = "gap";}
      if (type.equals("v")) {vs.add(new Vector3(scan.nextDouble()*scale, scan.nextDouble()*scale, scan.nextDouble()*scale));}
      else if (type.equals("vt")) {vts.add(new Vector2(scan.nextDouble(), 1-scan.nextDouble()));}
      else if (type.equals("f")) {
        if (vts.isEmpty()) {
          int a = scan.nextInt(), b = scan.nextInt(), c = scan.nextInt();
          fs.add(new Tri3D(
            new Vector3[]{vs.get(a-1), vs.get(b-1), vs.get(c-1)}, 
            new Vector2[]{new Vector2(), new Vector2(), new Vector2()}, 
            new int[]{a, b, c}, 
            new int[]{0, 0, 0}
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
    for (int i = 0; i < verts.length; i++) {
      double dist = verts[i].magsquare();
      if (dist > biggest) biggest = dist;
    }
    // System.out.println("RADIUS   : "+ biggest);
    // System.out.println("NUM VERTS: "+ verts.length);
    this.radius = Math.sqrt(biggest);
    return this.radius;
  }

  public Tri3D[] getFaces() {
    return faces;
  }

  public Vector3[] getVerts() {
    return verts;
  }

  public Vector2[] getVertUVs() {
    return vertUVs;
  }

  public void setFaces(Tri3D[] faces) {
    this.faces = faces;
  }

  public void setRadius(double radius) {this.radius = radius;}

  public double getRadius() {return radius;}

  public Material getMat() {return mat;}

  public void setMat(Material mat) {this.mat = mat;}

  public void setRotation(Vector4 rotation) {
    Vector3[] rotatedVs = new Vector3[verts.length];
    for (int i = 0; i < rotatedVs.length; i++) {
      rotatedVs[i] = Quaternion.rotate(rotation, verts[i]);
    }
    for (int i = 0; i < faces.length; i++) {
      int[] indices = faces[i].getVertexIndeces();
      faces[i].setVerts(rotatedVs[indices[0]-1], rotatedVs[indices[1]-1], rotatedVs[indices[2]-1]);
    }
  }

  public String toString() {
    String matTitle = mat.toString();
    StringBuilder res = new StringBuilder(1000);
    res.append("mtllib "+matTitle+".mtl\nusemtl "+matTitle+"\n");
    res.append("o " + this.getClass().getSimpleName() + "\n");
    for (Vector3 v : verts) res.append("v " + v.x + " " + v.y + " " + v.z + "\n");
    for (Vector2 vt : vertUVs) res.append("vt " + vt.x + " " + vt.y + "\n");
    res.append("s off\n");
    for (Tri3D f : faces) res.append("f " + f.toString() + "\n");
    return res.toString();
  }
}
