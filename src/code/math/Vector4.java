package code.math;

/**
* 4D Vector
*/
public class Vector4
{
  public double w;
  public double x;
  public double y;
  public double z;

  public Vector4(double w, double x, double y, double z) {
    this.w = w;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  private Vector4(Vector4 old) {
    this.w = old.w;
    this.x = old.x;
    this.y = old.y;
    this.z = old.z;
  }

  public Vector4() {w = x = y = z = 0;}

  public Vector4(double v) {w = x = y = z = v;}

  public static Vector4 abs(Vector4 input) {
    return new Vector4(Math.abs(input.w), Math.abs(input.x), Math.abs(input.y), Math.abs(input.z));
  }

  public String toString() {
    return "(" + w + ", " + x + ", " + y + ", " + z + ")";
  }

  public double magnitude() {
    return Math.sqrt((w*w)+(x*x)+(y*y)+(z*z));
  }

  public double magsquare() {
    return (w*w)+(x*x)+(y*y)+(z*z);
  }

  public Vector4 unitize() {
    double mag = magnitude();
    if (mag == 0) {mag = 1;}
    return new Vector4(w/mag, x/mag, y/mag, z/mag);
  }

  public Vector4 subtract(Vector4 other) {
    return new Vector4(this.w-other.w, this.x-other.x, this.y-other.y, this.z-other.z);
  }

  public Vector4 subtract(double other) {
    return new Vector4(this.w-other, this.x-other, this.y-other, this.z-other);
  }

  public Vector4 add(Vector4 other) {
    return new Vector4(this.w+other.w, this.x+other.x, this.y+other.y, this.z+other.z);
  }

  public Vector4 add(double other) {
    return new Vector4(this.w+other, this.x+other, this.y+other, this.z+other);
  }

  public Vector4 multiply(Vector4 other) {
    return new Vector4(this.w*other.w, this.x*other.x, this.y*other.y, this.z*other.z);
  }

  public Vector4 multiply(double other) {
    return new Vector4(this.w*other, this.x*other, this.y*other, this.z*other);
  }

  public double dot(Vector4 other) {
    return this.w*other.w + this.x*other.x + this.y*other.y + this.z*other.z;
  }

  public Vector4 copy() {
    return new Vector4(this);
  }
}
