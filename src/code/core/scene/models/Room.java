package code.core.scene.models;

import code.core.scene.Material;
import code.core.scene.Model;
import code.math.Vector3;

public class Room extends Model {

  private static final double radius = 14.7;

  public Room(Vector3 position, Material mat) {
    this.position = position;
    this.mat = mat;
    generateMesh("room.obj");
  }

  public double getRad() {return radius;}
}
