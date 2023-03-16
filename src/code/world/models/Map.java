package code.world.models;

import code.math.vector.Vector3;
import code.world.Material;
import code.world.Model;

public class Map extends Model {

  private static final double radius = new Map(new Vector3(), null).calculateRadius();

  public Map(Vector3 position, Material mat) {
    super(generateMesh("map.obj"));
    this.position = position;
    this.mat = mat;
  }

  public double getRad() {return radius;}
}