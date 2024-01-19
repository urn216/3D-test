package code.world.object;

import code.world.Material;
import code.world.Model;
import code.world.RigidBody;

import mki.math.vector.Vector3;

public class LowPoly extends RigidBody {

  public LowPoly(Vector3 position, Material mat) {
    super(position, Model.generateMesh("lowPoly.obj"));

    this.model.setMat(mat);
    this.model.setRadius(2.96);
  }
}
