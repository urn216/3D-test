package mki.world;

import mki.math.matrix.Quaternion;
import mki.math.vector.Vector3;

/**
* Write a description of class RigidBody here.
*
* @author (your name)
* @version (a version number or a date)
*/
public abstract class RigidBody {

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
  }

  public Vector3 getPosition() {
    return position;
  }

  public Quaternion getRotation() {
    return rotation;
  }

  public void setPosition(Vector3 position) {
    this.position = position;
  }

  public void offsetPosition(Vector3 position) {
    this.position = this.position.add(position);
  }

  public void offsetPitch(double theta) {
    setPitch(this.pitch+theta);
  }

  public void setPitch(double theta) {
    this.pitch = (360 + theta) % 360;

    updateQ();
  }

  public void offsetYaw(double theta) {
    setYaw(this.yaw+theta);
  }

  public void setYaw(double theta) {
    this.yaw = (360 + theta) % 360;

    updateQ();
  }

  public void offsetRoll(double theta) {
    setRoll(this.roll+theta);
  }

  public void setRoll(double theta) {
    this.roll = (360 + theta) % 360;

    updateQ();
  }

  private void updateQ() {
    this.rotation = Quaternion.fromPitchYawRoll(Math.toRadians(this.pitch), Math.toRadians(this.yaw), Math.toRadians(this.roll));

    this.model.setRotation(this.rotation);
  }

  public Model getModel() {return model;}

}
