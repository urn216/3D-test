package mki.world;

import mki.math.MathHelp;
import mki.math.matrix.Quaternion;
import mki.math.vector.Vector3;

/**
* Write a description of class RigidBody here.
*
* @author (your name)
* @version (a version number or a date)
*/
public abstract class RigidBody {

  private static RigidBody[] ACTIVE_BODIES = new RigidBody[10];
  private static int size = 0;

  protected Vector3 position;

  protected Model model;

  private double pitch;
  private double yaw;
  private double roll;

  private Quaternion rotation;

  public RigidBody(Vector3 position, Model model) {
    this.position = position;
    this.rotation = Quaternion.fromPitchYawRoll(Math.toRadians(this.pitch), Math.toRadians(this.yaw), Math.toRadians(this.roll));
    this.model = model;

    addBody(this);
  }

  public Vector3 getPosition() {
    return position;
  }

  public double getPitch() {
    return pitch;
  }

  public double getYaw() {
    return yaw;
  }

  public double getRoll() {
    return roll;
  }

  public Quaternion getRotation() {
    return rotation;
  }

  public void offsetPosition(Vector3 position) {
    this.position = this.position.add(position);
  }

  public void setPosition(Vector3 position) {
    this.position = position;
  }

  public void resetRotation() {
    this.pitch = this.yaw = this.roll = 0;

    this.rotation = Quaternion.fromAxisAngle(0, new Vector3());
  }

  public void offsetPitch(double theta) {
    setPitch(this.pitch+theta);
  }

  public void setPitch(double theta) {
    this.pitch = fixPitch(theta);

    updateQ();
  }

  protected static double fixPitch(double theta) {
    return MathHelp.clamp(theta, -90, 90);
  }

  public void offsetYaw(double theta) {
    setYaw(this.yaw+theta);
  }

  public void setYaw(double theta) {
    this.yaw = fixYaw(theta);

    updateQ();
  }

  protected static double fixYaw(double theta) {
    return (360 + theta) % 360;
  }

  public void offsetRoll(double theta) {
    setRoll(this.roll+theta);
  }

  public void setRoll(double theta) {
    this.roll = fixRoll(theta);

    updateQ();
  }

  protected static double fixRoll(double theta) {
    return (360 + theta) % 360;
  }

  public void setRotation(double pitch, double yaw, double roll) {
    this.pitch = fixPitch(pitch);
    this.yaw   = fixYaw  (yaw  );
    this.roll  = fixRoll (roll );

    updateQ();
  }

  private void updateQ() {
    this.rotation = Quaternion.fromPitchYawRoll(Math.toRadians(this.pitch), Math.toRadians(this.yaw), Math.toRadians(this.roll));

    this.model.setRotation(this.rotation);
  }

  public Model getModel() {return model;}

  static final RigidBody[] getActiveBodies() {
    return ACTIVE_BODIES;
  }

  private static final void addBody(RigidBody b) {
    if (size >= ACTIVE_BODIES.length) {
      RigidBody[] a = new RigidBody[ACTIVE_BODIES.length+10];
      System.arraycopy(ACTIVE_BODIES, 0, a, 0, ACTIVE_BODIES.length);
      ACTIVE_BODIES = a;
    }
    ACTIVE_BODIES[size++] = b;
  }

  public static final boolean removeBody(RigidBody b) {
    for (int i = 0; i < size; i++) {
      if (!b.equals(ACTIVE_BODIES[i])) continue;

      removeIndex(i);
      return true;
    }
    return false;
  }

  public static final void removeIndex(int i) {
    final int newSize;
    if ((newSize = size - 1) > i)
      System.arraycopy(ACTIVE_BODIES, i + 1, ACTIVE_BODIES, i, newSize - i);
    ACTIVE_BODIES[size = newSize] = null;
  }

  public static final void clearBodies() {
    ACTIVE_BODIES = new RigidBody[10];
    size = 0;
  }

}
