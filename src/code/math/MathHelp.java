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

  /**
   * Based on Bresenham's Line Algorithm.
   * <p>
   * Performs an action at each {@code Integer} coordinate of a line segment.
   * <p>
   * Utilises only bitwise operators and addition/subtraction. Very fast.
   * 
   * @param x1 the first x coordinate (does not have to be < x2)
   * @param y1 the first y coordinate (does not have to be < y2)
   * @param x2 the second x coordinate
   * @param y2 the second y coordinate
   * @param c the action to perform at each {@code x} and {@code y} coordinate along the line
   */
  public static void line2DToInt(int x1, int y1, int x2, int y2, BiConsumer<Integer, Integer> c) {
    int dx = MathHelp.abs(x2-x1);
    int sx = (((x2-x1)>>31)<<1)+1; //normalises dx
    int dy = -MathHelp.abs(y2-y1);
    int sy = (((y2-y1)>>31)<<1)+1; //normalises dy

    int e = dx+dy; //average error
    
    while (true) {
      c.accept(x1, y1);
      if (x1==x2 && y1==y2) break;
      int e2 = 2*e; //double error, rather than seeing if e>0.5*limit, so no division
      if (e2>=dy) { //too much y error, do an x step
        if (x1 == x2) break; //if we've done all the x steps we need, we're done
        e+=dy; //lower the error by dy
        x1+=sx; //shifts one step in the correct direction
      }
      if (e2<=dx) { //too much x error, do a y step
        if (y1 == y2) break; //if we've done all the y steps we need, we're done
        e+=dx; //increase the error by dx
        y1+=sy; //shifts one step in the correct direction
      }
    }
  }

  /**
   * Based on Bresenham's Line Algorithm.
   * <p>
   * Performs an action at each {@code Integer} coordinate of a line segment.
   * <p>
   * Utilises only bitwise operators and addition/subtraction. Very fast.
   * 
   * @param x1 the first x coordinate (does not have to be < x2)
   * @param y1 the first y coordinate (does not have to be < y2)
   * @param x2 the second x coordinate
   * @param y2 the second y coordinate
   * @param c the action to perform at each {@code x} and {@code y} coordinate along the line
   */
  public static void line3DToInt(int x1, int y1, int z1, int x2, int y2, int z2, TriConsumer<Integer, Integer, Integer> c) {
    int dz = MathHelp.abs(z2-z1);
    int sz = (((z2-z1)>>31)<<1)+1; //normalises dz

    if (dz == 0) {
      line2DToInt(x1, y1, x2, y2, (x, y) -> c.accept(x, y, z2));
      return;
    }

    int dx = MathHelp.abs(x2-x1);
    int sx = (((x2-x1)>>31)<<1)+1; //normalises dx
    int dy = MathHelp.abs(y2-y1);
    int sy = (((y2-y1)>>31)<<1)+1; //normalises dy

    int e1 = 2*dx-dz;
    int e2 = 2*dy-dz;
    
    while (true) {
      c.accept(x1, y1, z1);
      if (y1==y2 && x1==x2) break;
      if (e1>=0) { //too much x error, do a y step 
        if (y1 == y2) break;
        y1+=sy;
        e1-=2*dz;
      }
      if (e2>=0) {//too much y error, do an x step
        if (x1 == x2) break;
        x1+=sx;
        e2-=2*dz;
      }
      e1 += 2*dy;
      e2 += 2*dx;
      z1 += sz;
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

  public static <T> void swap(T o1, T o2) {
    T temp = o1;
    o1 = o2;
    o2 = temp;
  }
}
