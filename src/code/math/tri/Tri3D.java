package code.math.tri;

import mki.math.vector.Vector2;
import mki.math.vector.Vector3;

public class Tri3D {

  private final Vector3[] verts;
  private final Vector3[] edges;
  private final Vector2[] vertUVs;

  private final int[] vertexIndeces;
  private final int[] vertexTextureIndeces;
  
  private Vector3 normal;

  public Tri3D(Vector3[] verts, int[] vertexIndeces) {
    this(verts, new Vector2[3], vertexIndeces, new int[3]);
  }

  public Tri3D(Vector3[] verts, Vector2[] vertUVs, int[] vertexIndeces, int[] vertexTextureIndeces) {
    if (verts.length != 3 || vertexIndeces.length != 3 || vertUVs.length != 3 || vertexTextureIndeces.length != 3) {
      throw new RuntimeException("Triangles have 3 points!");
    }

    this.verts = verts;
    this.vertexIndeces = vertexIndeces;

    this.edges = new Vector3[3];

    this.edges[0] = verts[1].subtract(verts[0]);
    this.edges[1] = verts[2].subtract(verts[0]);
    this.edges[2] = this.edges[0].cross(this.edges[1]);

    this.normal = edges[2].unitize();

    this.vertUVs = vertUVs;
    this.vertexTextureIndeces = vertexTextureIndeces;
  }

  /**
   * @return the verts
   */
  public Vector3[] getVerts() {
  	return verts;
  }

  /**
   * @return the vertUVs
   */
  public Vector2[] getVertUVs() {
  	return vertUVs;
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
  public Vector3 getNormal() {
  	return normal;
  }

  public int[] getVertexIndeces() {
    return vertexIndeces;
  }

  public int[] getVertexTextureIndeces() {
    return vertexTextureIndeces;
  }

  public Vector2 getUVCoords(double u, double v) {
    return vertUVs[0] == null ? new Vector2() : vertUVs[1].scale(u).add(vertUVs[2].scale(v)).add(vertUVs[0].scale(1-u-v));
  }

  public void setVerts(Vector3 v0, Vector3 v1, Vector3 v2) {
    this.verts[0] = v0;
    this.verts[1] = v1;
    this.verts[2] = v2;

    this.edges[0] = v1.subtract(v0);
    this.edges[1] = v2.subtract(v0);
    this.edges[2] = this.edges[0].cross(this.edges[1]);

    this.normal = edges[2].unitize();
  }

  public String toString() {
    if (vertUVs[0] == null) {
      return vertexIndeces[0]+" "+vertexIndeces[1]+" "+vertexIndeces[2];
    }
    return vertexIndeces[0]+"/"+vertexTextureIndeces[0]+" "+vertexIndeces[1]+"/"+vertexTextureIndeces[1]+" "+vertexIndeces[2]+"/"+vertexTextureIndeces[2];
  }
}
