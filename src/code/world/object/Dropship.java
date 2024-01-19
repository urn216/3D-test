package code.world.object;

import code.world.Material;
import code.world.Model;
import code.world.RigidBody;

import mki.math.vector.Vector3;

public class Dropship extends RigidBody {

  public Dropship(Vector3 position, Material mat) {
    super(position, Model.generateMesh("MKinc Gunship.obj"));

    this.model.setMat(mat);
    this.model.setRadius(27.01);
  }
}
