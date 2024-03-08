package mki.world.object.loaded;

import mki.math.vector.Vector3;
import mki.world.Material;
import mki.world.Model;
import mki.world.RigidBody;

public class MapBlock extends RigidBody {

  public MapBlock(Vector3 position, double scale, Material mat) {
    super(position, Model.generateMesh("mapBlock.obj", scale));

    this.model.setMat(mat);
    this.model.calculateRadius();
  }
}