package code.world;

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

  public double getRad();

  public Tri[] getFaces();

  public Material getMat();

  public void setMat(Material mat);

}
