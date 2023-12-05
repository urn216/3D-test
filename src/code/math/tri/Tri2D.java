package code.math.tri;

import mki.math.vector.Vector2;

public class Tri2D {
  private final Vector2[] verts;
  private final Vector2[] vertUVs;

  private final double z;
  
  public Tri2D(Vector2[] verts, Vector2[] vertUVs, double z) {
    this.verts = verts;
    this.vertUVs = vertUVs;
    this.z = z;
  }

  public Vector2[] getVerts() {
    return verts;
  }

  public Vector2[] getVertUVs() {
    return vertUVs;
  }

  public double getZ() {
    return z;
  }
}
