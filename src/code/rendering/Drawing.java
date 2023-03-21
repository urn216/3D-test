package code.rendering;

import java.awt.image.BufferedImage;

import code.math.MathHelp;
import code.math.tri.Tri2D;
import code.math.tri.Tri3D;
import code.math.vector.Vector2;
import code.math.vector.Vector2I;
import code.math.vector.Vector3;
import code.world.Material;

public class Drawing {
  private final int[] contents;
  private final double[] depths;
  private final int width;
  private final int height;
  private final double aspectRatio;

  public Drawing(int width, int height) {
    this.contents = new int[width*height];
    this.depths   = new double[width*height];
    this.width = width;
    this.height = height;
    this.aspectRatio = 1.0*height/width;
  }

  public int[] getContents() {
    return contents;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public double getAspectRatio() {
    return aspectRatio;
  }

  public void asBufferedImage(BufferedImage image) {
    image.setRGB(0, 0, width, height, contents, 0, width);
  }

  /**
   * Fills this entire {@code Drawing} up with pixels of a given colour 
   * and places them at the furthest possible distance away in the depth buffer.
   * 
   * @param c The colour to fill the canvas with
   */
  public void fill(int c) {
    for (int i = 0; i < contents.length; i++) {
      contents[i] = c;
      depths[i] = 0;
    }
  }

  /**
   * Draws a single pixel onto this {@code Drawing} canvas. If the depth given is behind a previous pixel it will not be drawn.
   * 
   * @param x The {@code x} coordinate to draw to
   * @param y The {@code y} coordinate to draw to
   * @param z The {@code z} coordinate to represent the depth of the pixel in world-space from the viewing camera
   * @param c The colour of the pixel
   */
  public void drawPixel(int x, int y, double z, int c) {
    int i = x+y*width;
    z = 1/z;
    if (depths[i] > z) return;
    contents[i] = c;
    depths[i] = z;
  }

  /**
   * Draws a single pixel onto this {@code Drawing} canvas. Puts it at the front of the depth buffer.
   * 
   * @param x The {@code x} coordinate to draw to
   * @param y The {@code y} coordinate to draw to
   * @param c The colour of the pixel
   */
  public void drawPixel(int x, int y, int c) {
    int i = x+y*width;
    contents[i] = c;
    depths[i] = Double.MAX_VALUE;
  }

  /**
   * Draws a line across this {@code Drawing} canvas without depth considerations
   * 
   * @param x1 One of the {@code x} coordinate endpoints of the line
   * @param y1 One of the {@code y} coordinate endpoints of the line
   * @param x2 The other {@code x} coordinate endpoint of the line
   * @param y2 The other {@code y} coordinate endpoint of the line
   * @param c The colour of the line
   */
  public void drawLine(int x1, int y1, int x2, int y2, int c) {
    MathHelp.line2DToInt(x1, y1, x2, y2, (x, y) -> drawPixel(x, y, c));
  }

  /**
   * Draws a horizontal line across this {@code Drawing} canvas with no depth.
   * 
   * @param x1 One of the endpoints of the horizontal line
   * @param x2 The other endpoint of the horizontal line
   * @param y The {@code y} coordinate to draw the line at
   * @param c The colour of the line
   */
  public void drawLineHoriz(int x1, int x2, int y, int c) {
    int sx = (((x2-x1)>>31)<<1)+1;
    for (; x1!=x2; x1+=sx) {
      drawPixel(x1, y, c);
    }
  }

  /**
   * Draws a horizontal line across this {@code Drawing} canvas with a depth calculation at each pixel.
   * 
   * @param x1 One of the endpoints of the horizontal line
   * @param x2 The other endpoint of the horizontal line
   * @param y The {@code y} coordinate to draw the line at
   * @param p0 Any point lying on the plane this line is to travel across
   * @param normal A unit {@code Vector3} orthogonal to the plane this line is to travel across
   * @param c The colour of the line
   */
  public void drawLineHoriz(int x1, int x2, int y, Vector3 p0, Vector3 normal, int c) {
    double a = normal.x/normal.z;
    double b = normal.y/normal.z;
    double zOff = a*p0.x+b*(p0.y-y)+p0.z;
    int sx = (((x2-x1)>>31)<<1)+1;
    for (; x1!=x2; x1+=sx) {
      drawPixel(x1, y, zOff-a*x1, c);
    }
  }

  /**
   * Draws a vertical line across this {@code Drawing} canvas with no depth.
   * 
   * @param y1 One of the endpoints of the vertical line
   * @param y2 The other endpoint of the vertical line
   * @param x The {@code x} coordinate to draw the line at
   * @param c The colour of the line
   */
  public void drawLineVerti(int y1, int y2, int x, int c) {
    int sy = (((y2-y1)>>31)<<1)+1;
    for (; y1!=y2; y1+=sy) {
      drawPixel(x, y1, c);
    }
  }

  /**
   * Draws a vertical line across this {@code Drawing} canvas with a depth calculation at each pixel.
   * 
   * @param y1 One of the endpoints of the vertical line
   * @param y2 The other endpoint of the vertical line
   * @param x The {@code x} coordinate to draw the line at
   * @param p0 Any point lying on the plane this line is to travel across
   * @param normal A unit {@code Vector3} orthogonal to the plane this line is to travel across
   * @param c The colour of the line
   */
  public void drawLineVerti(int y1, int y2, int x, Vector3 p0, Vector3 normal, int c) {
    double a = normal.x/normal.z;
    double b = normal.y/normal.z;
    double zOff = a*(p0.x-x)+b*p0.y+p0.z;
    int sy = (((y2-y1)>>31)<<1)+1;
    for (; y1!=y2; y1+=sy) {
      drawPixel(x, y1, zOff-b*y1, c);
    }
  }

  /**
   * Draws the outline of a triangle onto this {@code Drawing} canvas. No depth calculations.
   * 
   * @param tri the 2-dimensional triangle to draw
   * @param c the colour of the outline to draw
   */
  public void drawTri(Tri2D tri, int c) {
    Vector2[] vs = tri.getVerts();
    drawLine((int)vs[0].x, (int)vs[0].y, (int)vs[1].x, (int)vs[1].y, c);
    drawLine((int)vs[0].x, (int)vs[0].y, (int)vs[2].x, (int)vs[2].y, c);
    drawLine((int)vs[1].x, (int)vs[1].y, (int)vs[2].x, (int)vs[2].y, c);
  }

  /**
   * Draws the outline of a triangle onto this {@code Drawing} canvas. No depth calculations.
   * 
   * @param tri the 3-dimensional triangle to draw as 2D
   * @param c the colour of the outline to draw
   */
  public void drawTri(Tri3D tri, int c) {
    Vector3[] vs = tri.getVerts();
    drawLine((int)vs[0].x, (int)vs[0].y, (int)vs[1].x, (int)vs[1].y, c);
    drawLine((int)vs[0].x, (int)vs[0].y, (int)vs[2].x, (int)vs[2].y, c);
    drawLine((int)vs[1].x, (int)vs[1].y, (int)vs[2].x, (int)vs[2].y, c);
  }

  public void fillTri(Tri2D tri, Material mat) {
    Vector2I[] p = new Vector2I[] {tri.getVerts()[0].castToInt(), tri.getVerts()[1].castToInt(), tri.getVerts()[2].castToInt()};
    if (p[1].y < p[0].y) {MathHelp.swap(p, 0, 1);}
    if (p[2].y < p[0].y) {MathHelp.swap(p, 0, 2);} //Ordering vertices so p[0] is the highest and p[2] is the lowest
    if (p[2].y < p[1].y) {MathHelp.swap(p, 1, 2);}

    int[] xs = new int[p[2].y-p[0].y+1];
    int offset = -p[0].y;
    
    MathHelp.line2DToInt(p[0].x, p[0].y, p[1].x, p[1].y, (x, y) -> {
      xs[y+offset] = x;
    });
    MathHelp.line2DToInt(p[1].x, p[1].y, p[2].x, p[2].y, (x, y) -> {
      xs[y+offset] = x;
    });
    MathHelp.line2DToInt(p[0].x, p[0].y, p[2].x, p[2].y, (x, y) -> {
      if (xs[y+offset] >= 0) {
        drawLineHoriz(x, xs[y+offset], y, mat.getAbsColour());
        xs[y+offset] = -1;
      }
    });
  }

  public void fillTri(Tri2D tri, int c) {
    Vector2I[] p = new Vector2I[] {tri.getVerts()[0].castToInt(), tri.getVerts()[1].castToInt(), tri.getVerts()[2].castToInt()};
    if (p[1].y < p[0].y) {MathHelp.swap(p, 0, 1);}
    if (p[2].y < p[0].y) {MathHelp.swap(p, 0, 2);} //Ordering vertices so p[0] is the highest and p[2] is the lowest
    if (p[2].y < p[1].y) {MathHelp.swap(p, 1, 2);}

    int[] xs = new int[p[2].y-p[0].y+1];
    int offset = -p[0].y;
    int sx = (((p[0].x-p[1].x)>>31)<<1)+1; //good enough for now
    xs[0] = p[1].x;

    if (p[0].y != p[1].y) MathHelp.line2DToInt(p[0].x, p[0].y, p[1].x, p[1].y, (x, y) -> {
      xs[y+offset] = x;
    });
    if (p[1].y != p[2].y) MathHelp.line2DToInt(p[1].x, p[1].y, p[2].x, p[2].y, (x, y) -> {
      xs[y+offset] = x;
    });
    MathHelp.line2DToInt(p[0].x, p[0].y, p[2].x, p[2].y, (x, y) -> {
      drawLineHoriz(x+sx, xs[y+offset]-sx, y, c);
    });
  }

  public void fillTri(Tri3D tri, int c) {
    Vector3[] p = new Vector3[] {tri.getVerts()[0], tri.getVerts()[1], tri.getVerts()[2]};
    if (p[1].y < p[0].y) {MathHelp.swap(p, 0, 1);}
    if (p[2].y < p[0].y) {MathHelp.swap(p, 0, 2);} //Ordering vertices so p[0] is the highest and p[2] is the lowest
    if (p[2].y < p[1].y) {MathHelp.swap(p, 1, 2);}

    int[] xs = new int[(int)p[2].y-(int)p[0].y+1];
    int offset = (int)-p[0].y;
    int sx = ((((int)(p[0].x-p[1].x))>>31)<<1)+1; //good enough for now. Doesn't perfectly make up for underestimation casting brings
    xs[0] = (int)p[1].x;

    if (p[0].y != p[1].y) MathHelp.line2DToInt((int)p[0].x, (int)p[0].y, (int)p[1].x, (int)p[1].y, (x, y) -> {
      xs[y+offset] = x-sx;
    });
    if (p[1].y != p[2].y) MathHelp.line2DToInt((int)p[1].x, (int)p[1].y, (int)p[2].x, (int)p[2].y, (x, y) -> {
      xs[y+offset] = x-sx;
    });
    MathHelp.line2DToInt((int)p[0].x, (int)p[0].y, (int)p[2].x, (int)p[2].y, (x, y) -> {
      drawLineHoriz(x, xs[y+offset], y, p[0], tri.getNormal(), c);
    });
  }
}
