package code.world.object;

import code.world.Material;
import code.world.Model;
import code.world.RigidBody;

import mki.math.vector.Vector3;

public class Sphere extends RigidBody {

  public Sphere(Vector3 position, double radius, Material mat) {
    super(position, Model.generateMesh("sphere.obj", radius));

    this.model.setRadius(radius);
    this.model.setMat(mat);
  }
}
