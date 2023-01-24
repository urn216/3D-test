package code.world;
import code.core.Core;
import code.math.Matrix;
import code.math.RayTri;
import code.math.Vector2;
import code.math.Vector3;

/**
* A camera allows a player to see what's happening in the game.
*/
public class Camera3D
{
  private static final Vector2 DEFAULT_SCREEN_SIZE = Core.DEFAULT_SCREEN_SIZE;
  // MULTITHREADING:
  // WARNING; UNFINISHED. LOOKS LIKE SHIT
  // private static final int NUM_THREADS = 2;
  // LookThread[] screenChunks = new LookThread[NUM_THREADS];

  private double defaultZoom;
  private double zoom;
  private double fieldOfView = 80;
  private Vector3 position;
  private Vector3 target;
  private Vector3 dir;
  private Vector3 rightDir;
  private Vector3 upDir;
  private double pitch;
  private int screenSizeX;
  private int screenSizeY;

  private int sparsity = 1;

  private double aspRat = DEFAULT_SCREEN_SIZE.y/DEFAULT_SCREEN_SIZE.x;

  int[] colourArray = new int[(int)(DEFAULT_SCREEN_SIZE.x*DEFAULT_SCREEN_SIZE.y)];
  int[] backgroundArray = new int[(int)(DEFAULT_SCREEN_SIZE.x*DEFAULT_SCREEN_SIZE.y)];

  /**
  * @Camera
  *
  * Constructs a camera with a position vector, a default zoom level, and the current resolution of the game window.
  */
  public Camera3D(Vector3 position, double zoom, int sX, int sY)
  {
    this.position = position.copy();
    this.dir = new Vector3(0, 0, 1);
    this.rightDir = new Vector3(1, 0, 0);
    this.upDir = new Vector3(0, 1, 0);
    this.pitch = 0;
    this.defaultZoom = zoom;
    this.screenSizeX = sX;
    this.screenSizeY = sY;
    this.zoom = sY/DEFAULT_SCREEN_SIZE.y*zoom;

    // MULTITHREADING:
    // WARNING; UNFINISHED. LOOKS LIKE SHIT
    // int yOff = 0;
    // int xOff = 0;
    // int width = (int)DEFAULT_SCREEN_SIZE.x/2;
    // int height = (int)DEFAULT_SCREEN_SIZE.y;
    // for (int i = 0; i < NUM_THREADS; i++) {
    //   screenChunks[i] = new LookThread(width, height, xOff, yOff);
    //   screenChunks[i].give(fieldOfView, zoom, aspRat, dir, upDir, position, new RigidBody[0]);;
    //   screenChunks[i].start();
    //   xOff += width;
    //   if (xOff>=DEFAULT_SCREEN_SIZE.x-1) {
    //     xOff=0;
    //     yOff+=height;
    //   }
    // }
  }

  public Vector3 getPos() {return position;}

  public Vector3 getDir() {return dir;}

  public Vector3 getTarget() {return target;}

  public double getZoom() {return zoom;}

  public double getDZoom() {return (screenSizeY/DEFAULT_SCREEN_SIZE.y)*defaultZoom;}

  public Vector2 getSize() {return new Vector2(screenSizeX/(zoom*2), screenSizeY/(zoom*2));}

  public void setPos(Vector3 position) {this.position = position;}

  public void setSparse(int i) {this.sparsity = i;}

  public void setTarget(Vector3 t){target = t;}

  public void setZoom(double z) {this.zoom = z;}

  public void setScreenSize(int sX, int sY) {
    this.screenSizeX = sX;
    this.screenSizeY = sY;
    // this.zoom = (sY/DEFAULT_SCREEN_SIZE.y)*defaultZoom;
  }

  public void move(double x, double y, double z) {
    position = position.add(dir.multiply(z)).add(rightDir.multiply(x)).add(upDir.multiply(y));
  }

  public void rotateX(double ang) {
    if (pitch+ang > 85) ang = 85-pitch;
    if (pitch+ang < -85) ang = -85-pitch;
    pitch+=ang;
    ang = Math.toRadians(ang);
    Matrix pitchMatrix = Matrix.rotateXLocal(ang, dir);
    dir = pitchMatrix.multiply(dir);
    rightDir = pitchMatrix.multiply(rightDir);
    upDir = pitchMatrix.multiply(upDir);
  }

  public void rotateY(double ang) {
    ang = Math.toRadians(ang);
    Matrix yawMatrix = Matrix.rotateY(ang);
    dir = yawMatrix.multiply(dir);
    rightDir = yawMatrix.multiply(rightDir);
    upDir = yawMatrix.multiply(upDir);
  }

  public double conX() {
    return position.x*zoom-screenSizeX/2;
  }

  public double conY() {
    return position.y*zoom-screenSizeY/2;
  }

  public int[] background() {
    for (int y = 0; y < DEFAULT_SCREEN_SIZE.y; y++) {
      for (int x = 0; x < DEFAULT_SCREEN_SIZE.x; x++) {
        backgroundArray[x+y*(int)DEFAULT_SCREEN_SIZE.x] = ((255 << 24) | ((int)(255*(0.6-(y/DEFAULT_SCREEN_SIZE.y)/2)) << 16) | ((int)(255*(1-(y/DEFAULT_SCREEN_SIZE.y)/1.5f)) << 8) | (int)(255*(1-(y/DEFAULT_SCREEN_SIZE.y)/2)));
        // colourArray[x+y*(int)DEFAULT_SCREEN_SIZE.x] = (255 << 24);
      }
    }
    return backgroundArray;
  }

  public void look(RigidBody[] bodies) {

    // MULTITHREADING:
    // WARNING; UNFINISHED. LOOKS LIKE SHIT
    // int i = 0;
    // int j = 0;
    // for (LookThread l : screenChunks) {
    //   l.give(fieldOfView, zoom, aspRat, dir, upDir, position, bodies);
    //   int[] localCol = l.get();
    //   int width = l.getWidth();
    //   int height = l.getHeight();
    //   for (int y = 0; y < height; y++) {
    //     for (int x = 0; x < width; x++) {
    //       colourArray[(x+i*width)+(y+j*height)*(int)DEFAULT_SCREEN_SIZE.x] = localCol[x+y*width];
    //     }
    //   }
    //   i++;
    //   if (i >= 16) {
    //     i = 0;
    //     j++;
    //   }
    // }

    //SINGLE THREAD:
    for (int y = 0; y < DEFAULT_SCREEN_SIZE.y; y+=sparsity) {
      // System.out.println(y);
      double percentDown = (-0.5+y/DEFAULT_SCREEN_SIZE.y);
      Matrix pitchMatrix = Matrix.rotateXLocal(Math.toRadians(percentDown*(fieldOfView/zoom)*aspRat), dir);
      Vector3 vDir = pitchMatrix.multiply(dir);
      Vector3 vUpDir = pitchMatrix.multiply(upDir);
      for (int x = 0; x < DEFAULT_SCREEN_SIZE.x; x+=sparsity) {
        // Vector3 dir = new Vector3(fieldOfView/zoom, (-y/DEFAULT_SCREEN_SIZE.y+0.5)*(fieldOfView), (x/DEFAULT_SCREEN_SIZE.x-0.5)*(fieldOfView)*aspRat).unitize();
        double percentAlong = (-0.5+x/DEFAULT_SCREEN_SIZE.x);
        // Vector2 ang = angle.add(new Vector2(yAxis*(fieldOfView/zoom)*aspRat, xAxis*(fieldOfView/zoom))).fixAngle();
        // Vector3 dir = Vector3.fromAngle(ang).unitize();
        Vector3 rayDir = Matrix.rotateLocal(Math.toRadians(percentAlong*(fieldOfView/zoom)), vUpDir).multiply(vDir);
        // Vector3 vDir = new Quaternion(xAxis*(fieldOfView/zoom), yAxis*(fieldOfView/zoom)*aspRat, 0).rotate(dir);
        // int col = RaySphere.getCol(position, rayDir, bodies, 1, 3);
        int col = RayTri.getCol(position, rayDir, bodies, 0, 1);
        for (int i = 0; i < sparsity; i++) {
          for (int j = 0; j < sparsity; j++) {colourArray[(x+j)+(y+i)*(int)DEFAULT_SCREEN_SIZE.x] = col;}
        }
      }
    }

    //DEPRECATED:
    // for (int y = 0; y < DEFAULT_SCREEN_SIZE.y; y+=sparsity) {
    //   for (int x = 0; x < DEFAULT_SCREEN_SIZE.x; x+=sparsity) {
    //     Vector2 ang = angle.add(new Vector2((-(fieldOfView/(2*zoom))*aspRat)+(x/DEFAULT_SCREEN_SIZE.x)*(fieldOfView/zoom)*aspRat, (fieldOfView/(2*zoom))-(y/DEFAULT_SCREEN_SIZE.y)*(fieldOfView/zoom))).fixAngle();
    //     Vector3 dir = Vector3.fromAngle(ang).unitize();
    //     colourArray[x+y*(int)DEFAULT_SCREEN_SIZE.x] = Ray.getCol(position, dir, bodies, 1);
    //   }
    // }
  }

  public int[] draw(RigidBody[] bodies) {
    // if (sparsity > 1) {background();}
    look(bodies);
    return colourArray;
  }
}

// MULTITHREADING:
// WARNING; UNFINISHED. LOOKS LIKE SHIT
// class LookThread extends Thread {
//   private final int width;
//   private final int height;
//   private final int xOff;
//   private final int yOff;
//   private int sparsity = 10;
//
//   private int[] colourArray;
//
//   double fieldOfView; double zoom; double aspRat; Vector3 dir; Vector3 upDir; Vector3 position; RigidBody[] bodies;
//
//   public LookThread(int width, int height, int xOff, int yOff) {
//     this.width = width;
//     this.height = height;
//     this.xOff = xOff;
//     this.yOff = yOff;
//     colourArray = new int[width*height];
//   }
//
//   public void give(double fieldOfView, double zoom, double aspRat, Vector3 dir, Vector3 upDir, Vector3 position, RigidBody[] bodies) {
//     this.fieldOfView = fieldOfView; this.zoom = zoom; this.aspRat = aspRat; this.dir = dir; this.upDir = upDir; this.position = position; this.bodies = bodies;
//   }
//
//   public int[] get() {
//     return colourArray;
//   }
//
//   public int getWidth() {return width;}
//
//   public int getHeight() {return height;}
//
//   public void run() {
//     while(true) {
//       for (int y = yOff; y < height+yOff; y+=sparsity) {
//         double percentDown = (-0.5+y/Core.DEFAULT_SCREEN_SIZE.y);
//         Matrix pitchMatrix = Matrix.rotateXLocal(Math.toRadians(percentDown*(fieldOfView/zoom)*aspRat), dir);
//         Vector3 vDir = pitchMatrix.multiply(dir);
//         Vector3 vUpDir = pitchMatrix.multiply(upDir);
//         for (int x = xOff; x < width+xOff; x+=sparsity) {
//           // Vector3 dir = new Vector3(fieldOfView/zoom, (-y/DEFAULT_SCREEN_SIZE.y+0.5)*(fieldOfView), (x/DEFAULT_SCREEN_SIZE.x-0.5)*(fieldOfView)*aspRat).unitize();
//           double percentAlong = (-0.5+x/Core.DEFAULT_SCREEN_SIZE.x);
//           // Vector2 ang = angle.add(new Vector2(yAxis*(fieldOfView/zoom)*aspRat, xAxis*(fieldOfView/zoom))).fixAngle();
//           // Vector3 dir = Vector3.fromAngle(ang).unitize();
//           Vector3 rayDir = Matrix.rotateLocal(Math.toRadians(percentAlong*(fieldOfView/zoom)), vUpDir).multiply(vDir);
//           // Vector3 vDir = new Quaternion(xAxis*(fieldOfView/zoom), yAxis*(fieldOfView/zoom)*aspRat, 0).rotate(dir);
//           int col = RaySphere.getCol(position, rayDir, bodies, 1, 3);
//           for (int i = 0; i < sparsity; i++) {
//             for (int j = 0; j < sparsity; j++) {colourArray[(x+j-xOff)+(y+i-yOff)*width] = col;}
//           }
//         }
//       }
//     }
//   }
// }
