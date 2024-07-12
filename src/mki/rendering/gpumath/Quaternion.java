package mki.rendering.gpumath;

import mki.math.vector.Vector3;
import uk.ac.manchester.tornado.api.types.vectors.Float3;
import uk.ac.manchester.tornado.api.types.vectors.Float4;

/**
 * 4D Vector for rotating 3D Vectors
 * 
 * @author William Kilty
 */
public abstract class Quaternion {

  /**
   * Generates a {@code Quaternion} to rotate about a given axis by {@code theta} radians.
   * 
   * @param theta angle in radians to ratate by
   * @param axis axix in which to rotate about
   * 
   * @return the {@code Quaternion} represented by a rotation via the given axis-angle values.
   */
  public static Float4 fromAxisAngle(double theta, Float4 axis) {
    float imScale = (float)Math.sin(theta/2);
    return new Float4(axis.getX()*imScale, axis.getY()*imScale, axis.getZ()*imScale, (float)Math.cos(theta/2));
  }

  /**
   * Generates a {@code Quaternion} from a set of {@code pitch}, {@code yaw}, and {@code roll} values as radians.
   * 
   * @param pitch a pitch angle in radians (rotation about global x-axis)
   * @param yaw a yaw angle in radians (rotation about global y-axis)
   * @param roll a roll angle in radians (rotation about global z-axis)
   * 
   * @return the {@code Quaternion} represented by a rotation by the given Euler angles.
   */
  public static Float4 fromPitchYawRoll(double pitch, double yaw, double roll) {
    float cp = (float)Math.cos(pitch*0.5);
    float sp = (float)Math.sin(pitch*0.5);
    float cy = (float)Math.cos(yaw*0.5);
    float sy = (float)Math.sin(yaw*0.5);
    float cr = (float)Math.cos(roll*0.5);
    float sr = (float)Math.sin(roll*0.5);

    return new Float4(
      sp*cy*cr - cp*sy*sr,
      cp*sy*cr + sp*cy*sr,
      cp*cy*sr - sp*sy*cr,
      cp*cy*cr + sp*sy*sr
    );
  }

  /**
   * Generates a {@code Quaternion} to rotate a given {@code Vector3} to face the same direction as another given {@code Vector3}.
   * 
   * @param startDir the unit direction to rotate from.
   * @param finalDir the unit direction to rotate towards.
   * 
   * @return the {@code Quaternion} represented by a rotation between the two given vectors.
   */
  public static Float4 toLookAt(Float4 startDir, Float4 finalDir) {
    Float4 translateDir = Float4.sub(finalDir, startDir);
    translateDir = Float4.scaleByInverse(translateDir, Float4.length(translateDir));
    
    Float4 val = new Float4(translateDir.getZ(), 0, -translateDir.getX(), translateDir.getY()+1);
    
    return Float4.scaleByInverse(val, Float4.length(val));
  }

  /**
   * Inverts this {@code Quaternion}. Switches between 'active' and 'passive' rotations.
   * 
   * @return the reversed form of this {@code Quaternion}.
   */
  public static Float4 reverse(Float4 q) {
    return new Float4(-q.getX(), -q.getY(), -q.getZ(), q.getW());
  }

  /**
   * Rotates a given {@code Vector3} by the rotation as defined by this {@code Quaternion}
   * 
   * @param v the {@code Vector3} to rotate
   * 
   * @return the rotated {@code Vector3}.
   */
  public static Float3 rotate(Float4 q, Float3 v) {
    float w = q.getX()*v.getX()+q.getY()*v.getY()+q.getZ()*v.getZ();
    float x = q.getW()*v.getX()+q.getY()*v.getZ()-q.getZ()*v.getY();
    float y = q.getW()*v.getY()-q.getX()*v.getZ()+q.getZ()*v.getX();
    float z = q.getW()*v.getZ()+q.getX()*v.getY()-q.getY()*v.getX();

    return new Float3(
      w*q.getX() + x*q.getW() - y*q.getZ() + z*q.getY(), 
      w*q.getY() + x*q.getZ() + y*q.getW() - z*q.getX(), 
      w*q.getZ() - x*q.getY() + y*q.getX() + z*q.getW()
    );
  }

  /**
   * Rotates a given {@code Vector3} by the rotation as defined by this {@code Quaternion}
   * 
   * @param v the {@code Vector3} to rotate
   * 
   * @return the rotated {@code Vector3}.
   */
  public static Vector3 rotate(Float4 q, Vector3 v) {
    double w = q.getX()*v.x+q.getY()*v.y+q.getZ()*v.z;
    double x = q.getW()*v.x+q.getY()*v.z-q.getZ()*v.y;
    double y = q.getW()*v.y-q.getX()*v.z+q.getZ()*v.x;
    double z = q.getW()*v.z+q.getX()*v.y-q.getY()*v.x;

    return new Vector3(
      w*q.getX() + x*q.getW() - y*q.getZ() + z*q.getY(), 
      w*q.getY() + x*q.getZ() + y*q.getW() - z*q.getX(), 
      w*q.getZ() - x*q.getY() + y*q.getX() + z*q.getW()
    );
  }
}
