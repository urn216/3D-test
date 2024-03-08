package mki.world.object.loaded;

import mki.math.vector.Vector3;
import mki.world.Material;
import mki.world.Model;
import mki.world.RigidBody;

public class Dropship extends RigidBody {

  public Dropship(Vector3 position, Material mat) {
    super(position, Model.generateMesh("MKinc Gunship.obj"));

    this.model.setMat(mat);
    this.model.setRadius(27.01);
  }
}
