package code.math.vector;

/**
* 3D Vector
*/
public class Vector3I extends Vector3 {
  public final int x;
  public final int y;
  public final int z;

  public Vector3I(int x, int y, int z) {
    super(x, y, z);

    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Vector3I(Vector3I old) {
    super(old);

    this.x = old.x;
    this.y = old.y;
    this.z = old.z;
  }

  public Vector3I() {
    super();
    x = y = z = 0;
  }

  public Vector3I(int v) {
    super(v);
    x = y = z = v;
  }

  public Vector3I subtract(Vector3I other) {
    return new Vector3I(this.x-other.x, this.y-other.y, this.z-other.z);
  }

  public Vector3I subtract(int other) {
    return new Vector3I(this.x-other, this.y-other, this.z-other);
  }

  public Vector3I add(Vector3I other) {
    return new Vector3I(this.x+other.x, this.y+other.y, this.z+other.z);
  }

  public Vector3I add(int other) {
    return new Vector3I(this.x+other, this.y+other, this.z+other);
  }

  public Vector3I scale(Vector3I other) {
    return new Vector3I(this.x*other.x, this.y*other.y, this.z*other.z);
  }

  public Vector3I scale(int other) {
    return new Vector3I(this.x*other, this.y*other, this.z*other);
  }

  public Vector3I scale(int x, int y, int z) {
    return new Vector3I(this.x*x, this.y*y, this.z*z);
  }

  public Vector3I copy() {
    return new Vector3I(this);
  }
}
