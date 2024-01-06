package code.world.object;

import mki.math.vector.Vector3;

import code.world.Material;
import code.world.Model;
import code.world.RigidBody;

public class MapBlock extends RigidBody {

  public MapBlock(Vector3 position, double scale, Material mat) {
    super(position, Model.generateMesh("mapBlock.obj", scale));

    this.model.setMat(mat);
    this.model.calculateRadius();
  }
}