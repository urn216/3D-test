package mki.world.object.primitive;

import mki.math.vector.Vector3;
import mki.world.Material;
import mki.world.Model;
import mki.world.RigidBody;

public class Prism extends RigidBody {

  public Prism(Vector3 position, int edges, double surfaceRad, double height, Material mat) {
    super(position, generateMesh(edges, surfaceRad, height));

    this.model.setRadius(Math.sqrt((height*height/4)+(surfaceRad*surfaceRad)));
    this.model.setMat(mat);
  }

  private static Model generateMesh(int edges, double surfaceRad, double height) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'generateMesh'");
  }
  
}
