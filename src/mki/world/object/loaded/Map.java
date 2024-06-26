package mki.world.object.loaded;

import mki.math.vector.Vector3;
import mki.world.Material;
import mki.world.Model;
import mki.world.RigidBody;

public class Map extends RigidBody {

  public Map(Vector3 position, double scale, Material mat) {
    super(position, Model.generateMesh("map.obj", scale));

    this.model.setMat(mat);
    this.model.calculateRadius();
  }
}