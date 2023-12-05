package code.world;

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

  public RigidBody(Vector3 position, Model model) {
    this.position = position;

    this.model = model;
  }

  public Vector3 getPosition() {return position;}

  public void setPosition(Vector3 pos) {position = pos;}

  public void move(double x, double y, double z) {position = position.add(new Vector3(x, y, z));}

  public Model getModel() {return model;}

}
