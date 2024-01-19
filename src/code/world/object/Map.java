package code.world.object;

import code.world.Material;
import code.world.Model;
import code.world.RigidBody;

import mki.math.vector.Vector3;

public class Map extends RigidBody {

  public Map(Vector3 position, double scale, Material mat) {
    super(position, Model.generateMesh("map.obj", scale));

    this.model.setMat(mat);
    this.model.calculateRadius();
  }
}