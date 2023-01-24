package code.core.scene.models;

import code.core.scene.Material;
import code.core.scene.Model;
import code.math.Vector3;

public class LowPoly extends Model {

  private static final double radius = 2.96;

  public LowPoly(Vector3 position, Material mat) {
    this.position = position;
    this.mat = mat;
    generateMesh("lowPoly.obj");
  }

  public double getRad() {return radius;}
}
