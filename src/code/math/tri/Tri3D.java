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
  private Vector3 pU;
  private Vector3 pV;

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
    this.pV = (normal.y > 0.99 || normal.y < -0.99 ? Vector3.UNIT_X : Vector3.UNIT_Y).cross(normal).unitize();
    this.pU = normal.cross(pV).unitize();

    this.vertUVs = vertUVs;
    this.vertexTextureIndeces = vertexTextureIndeces;
  }

  private Tri3D(Vector3[] verts, Vector2[] vertUVs, int[] vertexIndeces, int[] vertexTextureIndeces, Vector3 normal, Vector3 pU, Vector3 pV) {
    this.verts = verts;
    this.vertexIndeces = vertexIndeces;

    this.edges = new Vector3[3];

    this.edges[0] = verts[1].subtract(verts[0]);
    this.edges[1] = verts[2].subtract(verts[0]);
    this.edges[2] = this.edges[0].cross(this.edges[1]);

    this.normal = normal;
    this.pU = pU;
    this.pV = pV;

    this.vertUVs = vertUVs;
    this.vertexTextureIndeces = vertexTextureIndeces;
  }

  public Tri3D projectVerts(Vector3 v0, Vector3 v1, Vector3 v2) {
    return new Tri3D(new Vector3[]{v0, v1, v2}, this.vertUVs.clone(), this.vertexIndeces, this.vertexTextureIndeces, this.normal, this.pU, this.pV);
  }

  public Tri3D projectVerts(Vector3 v0, Vector3 v1, Vector3 v2, Vector2 uv0, Vector2 uv1, Vector2 uv2) {
    return new Tri3D(new Vector3[]{v0, v1, v2}, new Vector2[]{uv0, uv1, uv2}, this.vertexIndeces, this.vertexTextureIndeces, this.normal, this.pU, this.pV);
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

  public Vector3 getpU() {
    return pU;
  }

  public Vector3 getpV() {
    return pV;
  }

  public Vector3 getDisplacedNormal(Vector3 displacement) {
    return normal.add(pU.scale(displacement.x)).add(pV.scale(displacement.y)).subtract(normal.scale(displacement.z)).unitize();
  }

  public int[] getVertexIndeces() {
    return vertexIndeces;
  }

  public int[] getVertexTextureIndeces() {
    return vertexTextureIndeces;
  }

  public Vector2 getUVCoords(double u, double v) {
    return vertUVs[1].scale(u).add(vertUVs[2].scale(v)).add(vertUVs[0].scale(1-u-v));
  }

  public void setVerts(Vector3 v0, Vector3 v1, Vector3 v2) {
    this.verts[0] = v0;
    this.verts[1] = v1;
    this.verts[2] = v2;

    this.edges[0] = v1.subtract(v0);
    this.edges[1] = v2.subtract(v0);
    this.edges[2] = this.edges[0].cross(this.edges[1]);

    this.normal = edges[2].unitize();
    this.pV = (normal.y > 0.99 || normal.y < -0.99 ? Vector3.UNIT_X : Vector3.UNIT_Y).cross(normal).unitize();
    this.pU = normal.cross(pV).unitize();
  }

  public void setVertUVs(Vector2 v0, Vector2 v1, Vector2 v2) {
    this.vertUVs[0] = v0;
    this.vertUVs[1] = v1;
    this.vertUVs[2] = v2;
  }

  public String toString() {
    if (vertexTextureIndeces[0] == 0) {
      return vertexIndeces[0]+" "+vertexIndeces[1]+" "+vertexIndeces[2];
    }
    return vertexIndeces[0]+"/"+vertexTextureIndeces[0]+" "+vertexIndeces[1]+"/"+vertexTextureIndeces[1]+" "+vertexIndeces[2]+"/"+vertexTextureIndeces[2];
  }
}
