package mki.world.object.primitive;

import mki.math.tri.Tri3D;

import mki.math.vector.Vector2;
import mki.math.vector.Vector3;
import mki.world.Material;
import mki.world.Model;
import mki.world.RigidBody;

public class Quad extends RigidBody {

  public Quad(Vector3 position, double xSize, double ySize, double zSize, double textureSize, Material mat) {
    super(position, generateQuad(xSize, ySize, zSize, textureSize));

    this.model.setRadius(Math.sqrt((xSize*xSize/4)+(ySize*ySize/4)+(zSize*zSize/4)));
    this.model.setMat(mat);
  }
  
  public static Model generateQuad(double xSize, double ySize, double zSize, double textureSize) {
    double x = xSize/textureSize;
    double y = ySize/textureSize;
    double z = zSize/textureSize;
    xSize /= 2;
    ySize /= 2;
    zSize /= 2;
    Vector3[] verts = {
      new Vector3(-xSize, -ySize, -zSize), //0
      new Vector3(-xSize,  ySize, -zSize), //1
      new Vector3( xSize,  ySize, -zSize), //2
      new Vector3( xSize, -ySize, -zSize), //3
      new Vector3(-xSize, -ySize,  zSize), //4
      new Vector3(-xSize,  ySize,  zSize), //5
      new Vector3( xSize,  ySize,  zSize), //6
      new Vector3( xSize, -ySize,  zSize)  //7
    };
    Vector2[] vertUVs = {
      new Vector2(0, y),
      new Vector2(0, 0),
      new Vector2(x, 0),
      new Vector2(x, y),
      new Vector2(z, 0),
      new Vector2(z, y),
      new Vector2(0, z),
      new Vector2(x, z)
    };
    Tri3D[] tris = {
      new Tri3D(new Vector3[] {verts[0], verts[1], verts[2]}, new Vector2[] {vertUVs[0], vertUVs[1], vertUVs[2]}, new int[]{1,2,3}, new int[]{0,0,0}), //front
      new Tri3D(new Vector3[] {verts[0], verts[2], verts[3]}, new Vector2[] {vertUVs[0], vertUVs[2], vertUVs[3]}, new int[]{1,3,4}, new int[]{0,0,0}), //-----
      new Tri3D(new Vector3[] {verts[3], verts[2], verts[6]}, new Vector2[] {vertUVs[0], vertUVs[1], vertUVs[4]}, new int[]{4,3,7}, new int[]{0,0,0}), //right
      new Tri3D(new Vector3[] {verts[3], verts[6], verts[7]}, new Vector2[] {vertUVs[0], vertUVs[4], vertUVs[5]}, new int[]{4,7,8}, new int[]{0,0,0}), //-----
      new Tri3D(new Vector3[] {verts[7], verts[6], verts[5]}, new Vector2[] {vertUVs[0], vertUVs[1], vertUVs[2]}, new int[]{8,7,6}, new int[]{0,0,0}), //back
      new Tri3D(new Vector3[] {verts[7], verts[5], verts[4]}, new Vector2[] {vertUVs[0], vertUVs[2], vertUVs[3]}, new int[]{8,6,5}, new int[]{0,0,0}), //----
      new Tri3D(new Vector3[] {verts[4], verts[5], verts[1]}, new Vector2[] {vertUVs[0], vertUVs[1], vertUVs[4]}, new int[]{5,6,2}, new int[]{0,0,0}), //left
      new Tri3D(new Vector3[] {verts[4], verts[1], verts[0]}, new Vector2[] {vertUVs[0], vertUVs[4], vertUVs[5]}, new int[]{5,2,1}, new int[]{0,0,0}), //----
      new Tri3D(new Vector3[] {verts[1], verts[5], verts[6]}, new Vector2[] {vertUVs[6], vertUVs[1], vertUVs[2]}, new int[]{2,6,7}, new int[]{0,0,0}), //up
      new Tri3D(new Vector3[] {verts[1], verts[6], verts[2]}, new Vector2[] {vertUVs[6], vertUVs[2], vertUVs[7]}, new int[]{2,7,3}, new int[]{0,0,0}), //--
      new Tri3D(new Vector3[] {verts[4], verts[0], verts[3]}, new Vector2[] {vertUVs[6], vertUVs[1], vertUVs[2]}, new int[]{5,1,4}, new int[]{0,0,0}), //down
      new Tri3D(new Vector3[] {verts[4], verts[3], verts[7]}, new Vector2[] {vertUVs[6], vertUVs[2], vertUVs[7]}, new int[]{5,4,8}, new int[]{0,0,0})  //----
    };

    return new Model(verts, tris, vertUVs);
  }
}
