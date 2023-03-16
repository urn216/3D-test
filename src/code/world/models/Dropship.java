package code.world.models;

import code.math.vector.Vector3;
import code.world.Material;
import code.world.Model;

public class Dropship extends Model {

  private static final double radius = new Map(new Vector3(), null).calculateRadius();

  public Dropship(Vector3 position, Material mat) {
    super(generateMesh("MKinc Gunship.obj"));
    this.position = position;
    this.mat = mat;
  }

  public double getRad() {return radius;}
}
