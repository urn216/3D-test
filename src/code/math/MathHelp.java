package code.math;

import java.util.function.BiConsumer;

import code.math.vector.Vector2;
import code.math.vector.Vector3;

/**
* class helping do file stuff
*/
public class MathHelp {
  public static double clamp(double val, double l, double u) {return Math.min(Math.max(val, l), u);}

  public static int abs(int val) {
    int mask = val>>31;
    return (val^mask)-mask;
  }

  public static void performAlongIntLine(int x1, int y1, int x2, int y2, BiConsumer<Integer, Integer> c) {
    int dx = MathHelp.abs(x2-x1);
    int sx = (((x2-x1)>>31)<<1)+1; //normalises dx
    int dy = -MathHelp.abs(y2-y1);
    int sy = (((y2-y1)>>31)<<1)+1; //normalises dy

    int e = dx+dy;
    
    while (true) {
      c.accept(x1, y1);
      if (x1==x2 && y1==y2) break;
      int e2 = 2*e;
      if (e2>=dy) {
        if (x1 == x2) break;
        e+=dy; //lower the error by dy
        x1+=sx; //shifts one step in the right direction
      }
      if (e2<=dx) {
        if (y1 == y2) break;
        e+=dx; //increase the error by dx
        y1+=sy; //shifts one step in the right direction
      }
    }
  }

  public static double intensity(double source, double distSquare) {return source/(1+sphereSurfaceArea(distSquare));}

  public static double sphereSurfaceArea(double rSquare) {return Math.PI*4.0*rSquare;}

  public static Vector2 sphereUVPoint(Vector3 sNormal) {return new Vector2(0.5 + (Math.atan2(-sNormal.x, sNormal.z))/(2*Math.PI), 0.5 - (Math.asin(sNormal.y))/Math.PI);}

  public static Vector2 sphereUVPointInv(Vector3 sNormal) {return new Vector2(0.5 + (Math.atan2(sNormal.x, sNormal.z))/(2*Math.PI), 0.5 - (Math.asin(sNormal.y))/Math.PI);}

  public static <T> void swap(T[] arr, int i, int j) {
    T temp = arr[i];
    arr[i] = arr[j];
    arr[j] = temp;
  }
}
