package code.world.models;

import code.math.vector.Vector3;
import code.world.Material;
import code.world.Model;

public class LowPoly extends Model {

  private static final double radius = 2.96;

  public LowPoly(Vector3 position, Material mat) {
    super(generateMesh("lowPoly.obj"));
    this.position = position;
    this.mat = mat;
  }

  public double getRad() {return radius;}
}
