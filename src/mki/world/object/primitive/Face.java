package mki.world.object.primitive;

import mki.math.tri.Tri3D;
import mki.math.vector.Vector2;
import mki.math.vector.Vector3;
import mki.world.Material;
import mki.world.Model;
import mki.world.RigidBody;

public class Face extends RigidBody {
  public Face(Vector3 position, double xSize, double ySize, Material mat) {
    super(position, generateFace(xSize, ySize));

    this.model.setRadius(Math.sqrt((xSize*xSize/4)+(ySize*ySize/4)));
    this.model.setMat(mat);
  }

  private static Model generateFace(double xSize, double ySize) {
    xSize/=2;
    ySize/=2;

    Vector3[] verts = {
      new Vector3(-xSize, -ySize, 0),
      new Vector3(-xSize,  ySize, 0),
      new Vector3( xSize,  ySize, 0),
      new Vector3( xSize, -ySize, 0),
    };
    Vector2[] vertUVs = {
      new Vector2(0, 1),
      new Vector2(0, 0),
      new Vector2(1, 0),
      new Vector2(1, 1)
    };
    Tri3D[] tris = {
      new Tri3D(new Vector3[] {verts[0], verts[1], verts[2]}, new Vector2[] {vertUVs[0], vertUVs[1], vertUVs[2]}, new int[]{1,2,3}, new int[]{1,2,3}),
      new Tri3D(new Vector3[] {verts[0], verts[2], verts[3]}, new Vector2[] {vertUVs[0], vertUVs[2], vertUVs[3]}, new int[]{1,3,4}, new int[]{1,3,4}),
    };

    return new Model(verts, tris, vertUVs);
  }
}
