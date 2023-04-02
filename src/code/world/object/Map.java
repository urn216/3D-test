package code.world.object;

import code.math.vector.Vector3;
import code.world.Material;
import code.world.Model;
import code.world.RigidBody;

public class Map extends RigidBody {

  public Map(Vector3 position, Material mat) {
    super(position, Model.generateMesh("map.obj"));

    this.model.setMat(mat);
    this.model.calculateRadius();
  }
}