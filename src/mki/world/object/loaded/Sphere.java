package mki.world.object.loaded;

import mki.math.vector.Vector3;
import mki.world.Material;
import mki.world.Model;
import mki.world.RigidBody;

public class Sphere extends RigidBody {

  public Sphere(Vector3 position, double radius, Material mat) {
    super(position, Model.generateMesh("sphere.obj", radius));

    this.model.setRadius(radius);
    this.model.setMat(mat);
  }
}
