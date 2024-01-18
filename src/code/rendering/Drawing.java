package code.rendering;

import java.awt.image.BufferedImage;
import java.util.function.BiFunction;

import mki.math.MathHelp;
import mki.math.vector.Vector2;
import mki.math.vector.Vector2I;
import mki.math.vector.Vector3;
import mki.math.vector.Vector3I;
import code.math.tri.Tri2D;
import code.math.tri.Tri3D;

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
  public synchronized void fill(int c) {
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
  public synchronized void drawPixel(int x, int y, double z, int c) {
    int i = x+y*width;
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
  public synchronized void drawPixel(int x, int y, int c) {
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
   * Draws a line across this {@code Drawing} canvas with a depth calculation at each pixel.
   * 
   * @param x1 One of the {@code x} coordinate endpoints of the line
   * @param y1 One of the {@code y} coordinate endpoints of the line
   * @param x2 The other {@code x} coordinate endpoint of the line
   * @param y2 The other {@code y} coordinate endpoint of the line
   * @param c The colour of the line
   */
  public void drawLine(int x1, int y1, double z1, int x2, int y2, double z2, int colour) {
    MathHelp.line2DToInt(x1, y1, x2, y2, (x, y, c) -> drawPixel(x, y, MathHelp.lerp(z1, z2, c), colour));
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
    int incrx = (x2-x1)>>31;
    x1+=incrx; x2+=incrx;
    int sx = (incrx<<1)+1;
    
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
  public void drawLineHoriz(int x1, int x2, int y, double z1, double z2, int c) {
    int incrx = (x2-x1)>>31;
    x1+=incrx; x2+=incrx;
    int sx = (incrx<<1)+1;

    double zf = (z2-z1)/(sx*(x2-x1));

    for (; x1!=x2; x1+=sx, z1+=zf) {
      drawPixel(x1, y, z1, c);
    }
  }

  /**
   * Draws a horizontal line from a texture across this {@code Drawing} canvas with a depth calculation at each pixel.
   * 
   * @param x1 One of the endpoints of the horizontal line
   * @param x2 The other endpoint of the horizontal line
   * @param y The {@code y} coordinate to draw the line at
   * @param p0 Any point lying on the plane this line is to travel across
   * @param normal A unit {@code Vector3} orthogonal to the plane this line is to travel across
   * @param c The colour of the line
   */
  public void drawLineHoriz(int x1, int x2, int y, double z1, double z2, double u1, double u2, double v1, double v2, BiFunction<Double, Double, Integer> c) {
    int incrx = (x2-x1)>>31;
    x1+=incrx; x2+=incrx;
    int sx = (incrx<<1)+1;

    int numSteps = sx*(x2-x1);

    double zf = (z2-z1)/numSteps;
    double uf = (u2-u1)/numSteps;
    double vf = (v2-v1)/numSteps;

    for (; x1!=x2; x1+=sx, z1+=zf, u1+=uf, v1+=vf) {
      drawPixel(x1, y, z1, c.apply(u1/z1, v1/z1));
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
    Vector2I[] p = new Vector2I[] {tri.getVerts()[0].castToInt(), tri.getVerts()[1].castToInt(), tri.getVerts()[2].castToInt()};
    drawLine(p[0].x, p[0].y, p[1].x, p[1].y, c);
    drawLine(p[0].x, p[0].y, p[2].x, p[2].y, c);
    drawLine(p[1].x, p[1].y, p[2].x, p[2].y, c);
  }

  /**
   * Draws the outline of a triangle onto this {@code Drawing} canvas. No depth calculations.
   * 
   * @param tri the 3-dimensional triangle to draw as 2D
   * @param c the colour of the outline to draw
   */
  public void drawTri(Tri3D tri, int c) {
    Vector3[] vrts = tri.getVerts();
    Vector3I[] p = new Vector3I[] {MathHelp.round(vrts[0]), MathHelp.round(vrts[1]), MathHelp.round(vrts[2])};

    if (p[0].y == p[1].y && p[0].y == p[2].y) return;

    // double zOff = 0.001*tri.getNormal().z;

    drawLine(p[0].x, p[0].y, vrts[0].z, p[1].x, p[1].y, vrts[1].z, c);
    drawLine(p[0].x, p[0].y, vrts[0].z, p[2].x, p[2].y, vrts[2].z, c);
    drawLine(p[1].x, p[1].y, vrts[1].z, p[2].x, p[2].y, vrts[2].z, c);
  }

  /**
   * @param tri
   * @param colour
   */
  public void fillTri(Tri2D tri, int colour) {
    Vector2I[] p = new Vector2I[] {tri.getVerts()[0].castToInt(), tri.getVerts()[1].castToInt(), tri.getVerts()[2].castToInt()};
    if (p[1].y < p[0].y) {MathHelp.swap(p, 0, 1);}
    if (p[2].y < p[0].y) {MathHelp.swap(p, 0, 2);} //Ordering vertices so p[0] is the highest and p[2] is the lowest
    if (p[2].y < p[1].y) {MathHelp.swap(p, 1, 2);}

    int[] xs = new int[p[2].y-p[0].y+1];
    int offset = -p[0].y;
    xs[0] = p[1].x;

    if (p[0].y != p[1].y) MathHelp.line2DToInt(p[0].x, p[0].y, p[1].x, p[1].y, (x, y) -> xs[y+offset] = x);
    if (p[1].y != p[2].y) MathHelp.line2DToInt(p[1].x, p[1].y, p[2].x, p[2].y, (x, y) -> xs[y+offset] = x);
    MathHelp.line2DToInt(p[0].x, p[0].y, p[2].x, p[2].y, (x, y) -> drawLineHoriz(x, xs[y+offset], y, colour));
  }

  /**
   * @param tri
   * @param colour
   */
  public void fillTri(Tri3D tri, int colour) {
    Vector3[] vrts = tri.getVerts();

    Vector3I[] p = new Vector3I[] {
      MathHelp.round(vrts[0]), 
      MathHelp.round(vrts[1]), 
      MathHelp.round(vrts[2])
    };

    if (p[0].y == p[1].y && p[0].y == p[2].y) return;

    Double  [] z = new Double  [] {
      vrts[0].z, 
      vrts[1].z, 
      vrts[2].z
    };

    if (p[1].y < p[0].y) {MathHelp.swap(p, 0, 1); MathHelp.swap(z, 0, 1);}
    if (p[2].y < p[0].y) {MathHelp.swap(p, 0, 2); MathHelp.swap(z, 0, 2);} //Ordering vertices so p[0] is the highest and p[2] is the lowest
    if (p[2].y < p[1].y) {MathHelp.swap(p, 1, 2); MathHelp.swap(z, 1, 2);}

    int[] xs = new int[p[2].y-p[0].y+1];
    double[] zs = new double[xs.length];
    int offset = -p[0].y;
    xs[0] = p[1].x;
    zs[0] = z[1];

    if (p[0].y != p[1].y) MathHelp.line2DToInt(p[0].x, p[0].y, p[1].x, p[1].y, (x, y, c) -> {
      xs[y+offset] = x;
      zs[y+offset] = MathHelp.lerp(z[0], z[1], c);
    });
    if (p[1].y != p[2].y) MathHelp.line2DToInt(p[1].x, p[1].y, p[2].x, p[2].y, (x, y, c) -> {
      xs[y+offset] = x;
      zs[y+offset] = MathHelp.lerp(z[1], z[2], c);
    });
    MathHelp.line2DToInt(p[0].x, p[0].y, p[2].x, p[2].y, (x, y, c) -> drawLineHoriz(
      x, 
      xs[y+offset], 
      y, 
      MathHelp.lerp(z[0], z[2], c),
      zs[y+offset], 
      colour
    ));
  }

  /**
   * @param tri
   * @param mat
   * @param globalIllumination
   */
  public void fillTri(Tri3D tri, BiFunction<Double, Double, Integer> colour) {
    Vector3[] vrts = tri.getVerts();
    Vector2[] uv = tri.getVertUVs();

    Vector3I[] p = new Vector3I[] {
      MathHelp.round(vrts[0]), 
      MathHelp.round(vrts[1]), 
      MathHelp.round(vrts[2])
      // vrts[0].castToInt(), 
      // vrts[1].castToInt(), 
      // vrts[2].castToInt()
    };

    if (p[0].y == p[1].y && p[0].y == p[2].y) return;

    Vector3[] uvs = new Vector3[] {
      new Vector3(uv[0].x, uv[0].y, vrts[0].z),
      new Vector3(uv[1].x, uv[1].y, vrts[1].z),
      new Vector3(uv[2].x, uv[2].y, vrts[2].z)
    };

    if (p[1].y < p[0].y) {MathHelp.swap(p, 0, 1); MathHelp.swap(uvs, 0, 1);}
    if (p[2].y < p[0].y) {MathHelp.swap(p, 0, 2); MathHelp.swap(uvs, 0, 2);} //Ordering vertices so p[0] is the highest and p[2] is the lowest
    if (p[2].y < p[1].y) {MathHelp.swap(p, 1, 2); MathHelp.swap(uvs, 1, 2);}

    int[] xs = new int[p[2].y-p[0].y+1];
    double[] zs = new double[xs.length];
    double[] us = new double[xs.length];
    double[] vs = new double[xs.length];
    int offset = -p[0].y;
    xs[0] =   p[1].x;
    zs[0] = uvs[1].z;
    us[0] = uvs[1].x;
    vs[0] = uvs[1].y;
    
    // Vector3 origin = tri.getVerts()[0];
    // Vector3 normal = tri.getNormal();

    if (p[0].y != p[1].y) MathHelp.line2DToInt(p[0].x, p[0].y, p[1].x, p[1].y, (x, y, c) -> {
      xs[y+offset] = x; 
      zs[y+offset] = MathHelp.lerp(uvs[0].z, uvs[1].z, c);
      us[y+offset] = MathHelp.lerp(uvs[0].x, uvs[1].x, c);
      vs[y+offset] = MathHelp.lerp(uvs[0].y, uvs[1].y, c);
    });
    if (p[1].y != p[2].y) MathHelp.line2DToInt(p[1].x, p[1].y, p[2].x, p[2].y, (x, y, c) -> {
      xs[y+offset] = x; 
      zs[y+offset] = MathHelp.lerp(uvs[1].z, uvs[2].z, c);
      us[y+offset] = MathHelp.lerp(uvs[1].x, uvs[2].x, c);
      vs[y+offset] = MathHelp.lerp(uvs[1].y, uvs[2].y, c);
    });

    MathHelp.line2DToInt(p[0].x, p[0].y, p[2].x, p[2].y, (x, y, c) -> drawLineHoriz(
      x, 
      xs[y+offset], 
      y, 
      MathHelp.lerp(uvs[0].z, uvs[2].z, c),
      zs[y+offset], 
      MathHelp.lerp(uvs[0].x, uvs[2].x, c),
      us[y+offset], 
      MathHelp.lerp(uvs[0].y, uvs[2].y, c),
      vs[y+offset], 
      colour
    ));
  }
}
