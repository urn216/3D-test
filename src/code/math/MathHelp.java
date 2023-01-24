package code.math;
/**
* class helping do file stuff
*/
public class MathHelp {
  public static double clamp(double val, double l, double u) {return Math.min(Math.max(val, l), u);}

  public static double intensity(double source, double distSquare) {return source/(1+sphereSurfaceArea(distSquare));}

  public static double sphereSurfaceArea(double rSquare) {return Math.PI*4.0*rSquare;}

  public static Vector2 sphereUVPoint(Vector3 sNormal) {return new Vector2(0.5 + (Math.atan2(-sNormal.x, sNormal.z))/(2*Math.PI), 0.5 - (Math.asin(sNormal.y))/Math.PI);}

  public static Vector2 sphereUVPointInv(Vector3 sNormal) {return new Vector2(0.5 + (Math.atan2(sNormal.x, sNormal.z))/(2*Math.PI), 0.5 - (Math.asin(sNormal.y))/Math.PI);}
}
