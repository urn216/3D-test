package mki.world.object.loaded;

import mki.math.vector.Vector3;
import mki.world.Material;
import mki.world.Model;
import mki.world.RigidBody;

public class Room extends RigidBody {

  public Room(Vector3 position, Material mat) {
    super(position, Model.generateMesh("room.obj"));

    this.model.setMat(mat);
    this.model.setRadius(14.7);
  }
}
