package mki.world.object.loaded;

import mki.math.vector.Vector3;
import mki.world.Material;
import mki.world.Model;
import mki.world.RigidBody;

public class LowPoly extends RigidBody {

  public LowPoly(Vector3 position, Material mat) {
    super(position, Model.generateMesh("lowPoly.obj"));

    this.model.setMat(mat);
    this.model.setRadius(2.96);
  }
}
