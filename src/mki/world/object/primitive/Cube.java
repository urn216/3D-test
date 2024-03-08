package mki.world.object.primitive;

import mki.math.tri.Tri3D;

import mki.math.vector.Vector2;
import mki.math.vector.Vector3;
import mki.world.Material;
import mki.world.Model;
import mki.world.RigidBody;

public class Cube extends RigidBody {

  public Cube(Vector3 position, double width, Material mat) {
    super(position, generateMesh(width));

    this.model.setRadius(width/(2*Math.sqrt(3)/3));
    this.model.setMat(mat);
  }

  public static Model generateMesh(double width) {
    double sRad = width/2;

    Vector3[] verts = {
      new Vector3(-sRad, -sRad, -sRad),
      new Vector3(-sRad,  sRad, -sRad),
      new Vector3( sRad,  sRad, -sRad),
      new Vector3( sRad, -sRad, -sRad),
      new Vector3(-sRad, -sRad,  sRad),
      new Vector3(-sRad,  sRad,  sRad),
      new Vector3( sRad,  sRad,  sRad),
      new Vector3( sRad, -sRad,  sRad)
    };
    Vector2[] vertUVs = {
      new Vector2(0, 1),
      new Vector2(0, 0),
      new Vector2(1, 0),
      new Vector2(1, 1)
    };
    Tri3D[] tris = {
      new Tri3D(new Vector3[] {verts[0], verts[1], verts[2]}, new Vector2[] {vertUVs[0], vertUVs[1], vertUVs[2]}, new int[]{1,2,3}, new int[]{0,0,0}),
      new Tri3D(new Vector3[] {verts[0], verts[2], verts[3]}, new Vector2[] {vertUVs[0], vertUVs[2], vertUVs[3]}, new int[]{1,3,4}, new int[]{0,0,0}),
      new Tri3D(new Vector3[] {verts[3], verts[2], verts[6]}, new Vector2[] {vertUVs[0], vertUVs[1], vertUVs[2]}, new int[]{4,3,7}, new int[]{0,0,0}),
      new Tri3D(new Vector3[] {verts[3], verts[6], verts[7]}, new Vector2[] {vertUVs[0], vertUVs[2], vertUVs[3]}, new int[]{4,7,8}, new int[]{0,0,0}),
      new Tri3D(new Vector3[] {verts[7], verts[6], verts[5]}, new Vector2[] {vertUVs[0], vertUVs[1], vertUVs[2]}, new int[]{8,7,6}, new int[]{0,0,0}),
      new Tri3D(new Vector3[] {verts[7], verts[5], verts[4]}, new Vector2[] {vertUVs[0], vertUVs[2], vertUVs[3]}, new int[]{8,6,5}, new int[]{0,0,0}),
      new Tri3D(new Vector3[] {verts[4], verts[5], verts[1]}, new Vector2[] {vertUVs[0], vertUVs[1], vertUVs[2]}, new int[]{5,6,2}, new int[]{0,0,0}),
      new Tri3D(new Vector3[] {verts[4], verts[1], verts[0]}, new Vector2[] {vertUVs[0], vertUVs[2], vertUVs[3]}, new int[]{5,2,1}, new int[]{0,0,0}),
      new Tri3D(new Vector3[] {verts[1], verts[5], verts[6]}, new Vector2[] {vertUVs[0], vertUVs[1], vertUVs[2]}, new int[]{2,6,7}, new int[]{0,0,0}),
      new Tri3D(new Vector3[] {verts[1], verts[6], verts[2]}, new Vector2[] {vertUVs[0], vertUVs[2], vertUVs[3]}, new int[]{2,7,3}, new int[]{0,0,0}),
      new Tri3D(new Vector3[] {verts[4], verts[0], verts[3]}, new Vector2[] {vertUVs[0], vertUVs[1], vertUVs[2]}, new int[]{5,1,4}, new int[]{0,0,0}),
      new Tri3D(new Vector3[] {verts[4], verts[3], verts[7]}, new Vector2[] {vertUVs[0], vertUVs[2], vertUVs[3]}, new int[]{5,4,8}, new int[]{0,0,0})
    };

    return new Model(verts, tris, vertUVs);
  }
}
