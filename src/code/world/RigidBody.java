package code.world;

import code.math.tri.Tri3D;
import code.math.vector.Vector3;

/**
* Write a description of class WorldObject here.
*
* @author (your name)
* @version (a version number or a date)
*/
public interface RigidBody
{

  public Vector3 getPos();

  public void setPos(Vector3 pos);

  public void move(double x, double y, double z);

  public double getRadius();

  public Tri3D[] getFaces();

  public Material getMat();

  public void setMat(Material mat);

}
