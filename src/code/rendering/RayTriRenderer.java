package code.rendering;

import code.math.matrix.Matrix;
import code.math.rays.RayTri;
import code.math.vector.Vector3;

import code.world.RigidBody;

class RayTriRenderer extends Renderer {
  // MULTITHREADING:
  // WARNING; UNFINISHED. LOOKS LIKE SHIT
  // private static final int NUM_THREADS = 2;
  // LookThread[] screenChunks = new LookThread[NUM_THREADS];

  public RayTriRenderer() {
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
  
  @Override
  public void render(int[] dest, int width, int height, Vector3 position, Vector3 dir, Vector3 upDir, double fov, RigidBody[] bodies) {

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

    double aspRat = 1.0*height/width;

    for (int y = 0; y < height; y++) {
      double percentDown = (-0.5+y/height);
      Matrix pitchMatrix = Matrix.rotateXLocal(Math.toRadians(percentDown*fov*aspRat), dir);
      Vector3 vDir = pitchMatrix.multiply(dir);
      Vector3 vUpDir = pitchMatrix.multiply(upDir);
      for (int x = 0; x < width; x++) {
        double percentAlong = (-0.5+x/width);
        Vector3 rayDir = Matrix.rotateLocal(Math.toRadians(percentAlong*fov), vUpDir).multiply(vDir);
        dest[(x)+(y)*width] = RayTri.getCol(position, rayDir, bodies, 0, 1);
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
