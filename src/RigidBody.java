
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

  public Tri[] getTris();

  public Material getMat();

  public void setMat(Material mat);

}
