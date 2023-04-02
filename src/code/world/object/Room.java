package code.world.object;

import code.math.vector.Vector3;
import code.world.Material;
import code.world.Model;
import code.world.RigidBody;

public class Room extends RigidBody {

  public Room(Vector3 position, Material mat) {
    super(position, Model.generateMesh("room.obj"));

    this.model.setMat(mat);
    this.model.setRadius(14.7);
  }
}
