package code.core.scene.models;
import code.math.Vector2;
import code.math.Vector3;

public class Tri {

  private Vector3[] verts = new Vector3[3];
  private Vector3[] edges = new Vector3[3];
  private Vector2[] vertUVs = new Vector2[3];
  private Vector3 normal;

  public Tri(Vector3 a, Vector3 b, Vector3 c) {
    verts[0] = a; verts[1] = b; verts[2] = c;
    edges[0] = b.subtract(a);
    edges[1] = c.subtract(a);
    edges[2] = edges[0].cross(edges[1]).unitize();
    normal = edges[0].cross(edges[1]);
  }

  public Tri(Vector3 a, Vector3 b, Vector3 c, Vector2 A, Vector2 B, Vector2 C) {
    this(a, b, c);
    vertUVs[0] = A; vertUVs[1] = B; vertUVs[2] = C;
  }

  /**
   * @return the verts
   */
  public Vector3[] getVerts() {
  	return verts;
  }

  public void setVerts(Vector3[] verts) {
    this.verts = verts;
  }

  /**
   * @return the vertUVs
   */
  public Vector2[] getVertUVs() {
  	return vertUVs;
  }

  public void setVertUVs(Vector2[] vertUVs) {
    this.vertUVs = vertUVs;
  }

  /**
   * @return the edges
   */
  public Vector3[] getEdges() {
  	return edges;
  }

  /**
   * @return the normal
   */
  public Vector3 getNorm() {
  	return normal;
  }

  public Vector2 getUVCoords(double u, double v) {
    return vertUVs[1].multiply(u).add(vertUVs[2].multiply(v)).add(vertUVs[0].multiply(1-u-v));
  }
}
