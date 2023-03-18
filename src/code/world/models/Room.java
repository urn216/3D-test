package code.world.models;

import code.math.vector.Vector3;
import code.world.Material;
import code.world.Model;

public class Room extends Model {

  private static final double radius = 14.7;

  public Room(Vector3 position, Material mat) {
    super(generateMesh("room.obj"));
    this.position = position;
    this.mat = mat;
  }

  public double getRadius() {return radius;}
}
