package code.math.matrix;

import code.math.vector.Vector3;
import code.math.vector.Vector4;

/**
* 4D Vector
*/
public class Quaternion
{
  public double q;
  public Vector3 v;

  public Quaternion(double w, double x, double y, double z) {
    this.q = w;
    this.v = new Vector3(x, y, z);
  }

  public Quaternion(double q, Vector3 v) {
    this.q = q;
    this.v = v;
  }

  public Quaternion(Vector4 v) {
    this.q = v.w;
    this.v = new Vector3(v.x, v.y, v.z);
  }

  public Quaternion(double pitch, double yaw, double roll) {
    pitch = Math.toRadians(pitch);
    yaw = Math.toRadians(yaw);
    roll = Math.toRadians(roll);
    double cp = Math.cos(pitch*0.5);
    double sp = Math.sin(pitch*0.5);
    double cy = Math.cos(yaw*0.5);
    double sy = Math.sin(yaw*0.5);
    double cr = Math.cos(roll*0.5);
    double sr = Math.sin(roll*0.5);

    this.q = cr*cp*cy + sr*sp*sy;
    double x = sr*cp*cy - cr*sp*sy;
    double y = cr*sp*cy + sr*cp*sy;
    double z = cr*cp*sy - sr*sp*cy;
    this.v = new Vector3(x, y, z);
  }

  public Vector3 rotate(Vector3 dir) {
    Vector3 t = v.scale(2).cross(dir);
    return dir.add(t.scale(q)).add(v.cross(t));
  }
}
