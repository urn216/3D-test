package code.rendering;

import java.awt.image.BufferedImage;

import code.math.MathHelp;
import code.math.tri.Tri2D;
import code.math.tri.Tri3D;
import code.math.vector.Vector2;
import code.math.vector.Vector2I;
import code.math.vector.Vector3;
import code.math.vector.Vector3I;
import code.world.Material;

public class Drawing {
  private final int[] contents;
  private final int[] depths;
  private final int width;
  private final int height;
  private final double aspectRatio;

  public Drawing(int width, int height) {
    this.contents = new int[width*height];
    this.depths   = new int[width*height];
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

  public void fill(int c) {
    for (int i = 0; i < contents.length; i++) {
      contents[i] = c;
      depths[i] = Integer.MAX_VALUE;
    }
  }

  public void drawPixel(int x, int y, int z, int c) {
    int i = x+y*width;
    if (depths[i] < z) return;
    contents[i] = c;
    depths[i] = z;
  }

  public void drawPixel(int x, int y, int c) {
    int i = x+y*width;
    contents[i] = c;
    depths[i] = 0;
  }

  public void drawLine(int x1, int y1, int x2, int y2, int c) {
    MathHelp.line2DToInt(x1, y1, x2, y2, (x, y) -> drawPixel(x, y, c));
  }

  public void drawLine(int x1, int y1, int z1, int x2, int y2, int z2, int c) {
    MathHelp.line3DToInt(x1, y1, z1, x2, y2, z2, (x, y, z) -> drawPixel(x, y, z, c));
  }

  public void drawLineHoriz(int x1, int x2, int y, int c) {
    int sx = (((x2-x1)>>31)<<1)+1; //normalises dx
    for (; x1!=x2; x1+=sx) {
      drawPixel(x1, y, c);
    }
  }

  public void drawLineHoriz(int x1, int x2, int y, int z1, int z2, int c) {
    MathHelp.line2DToInt(x1, z1, x2, z2, (x, z) -> drawPixel(x, y, z, c));
  }

  public void drawLineVerti(int y1, int y2, int x, int c) {
    int sy = (((y2-y1)>>31)<<1)+1; //normalises dy
    for (; y1!=y2; y1+=sy) {
      drawPixel(x, y1, c);
    }
  }

  public void drawTri(Tri2D tri, int c) {
    Vector2[] vs = tri.getVerts();
    drawLine((int)vs[0].x, (int)vs[0].y, (int)vs[1].x, (int)vs[1].y, c);
    drawLine((int)vs[0].x, (int)vs[0].y, (int)vs[2].x, (int)vs[2].y, c);
    drawLine((int)vs[1].x, (int)vs[1].y, (int)vs[2].x, (int)vs[2].y, c);
  }

  public void drawTri(Tri3D tri, int c) {
    Vector3[] vs = tri.getVerts();
    drawLine((int)vs[0].x, (int)vs[0].y, (int)(vs[0].z*1000), (int)vs[1].x, (int)vs[1].y, (int)(vs[1].z*1000), c);
    drawLine((int)vs[0].x, (int)vs[0].y, (int)(vs[0].z*1000), (int)vs[2].x, (int)vs[2].y, (int)(vs[2].z*1000), c);
    drawLine((int)vs[1].x, (int)vs[1].y, (int)(vs[1].z*1000), (int)vs[2].x, (int)vs[2].y, (int)(vs[2].z*1000), c);
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
    Vector3I[] p = new Vector3I[] {tri.getVerts()[0].castToInt(1000), tri.getVerts()[1].castToInt(1000), tri.getVerts()[2].castToInt(1000)};
    if (p[1].y < p[0].y) {MathHelp.swap(p, 0, 1);}
    if (p[2].y < p[0].y) {MathHelp.swap(p, 0, 2);} //Ordering vertices so p[0] is the highest and p[2] is the lowest
    if (p[2].y < p[1].y) {MathHelp.swap(p, 1, 2);}

    int[] xs = new int[p[2].y-p[0].y+1];
    int[] zs = new int[p[2].y-p[0].y+1];
    int offset = -p[0].y;
    xs[0] = p[1].x;
    zs[0] = p[1].z;

    if (p[0].y != p[1].y) MathHelp.line3DToInt(p[0].x, p[0].y, p[0].z, p[1].x, p[1].y, p[1].z, (x, y, z) -> {
      xs[y+offset] = x;
      zs[y+offset] = z;
    });
    if (p[1].y != p[2].y) MathHelp.line3DToInt(p[1].x, p[1].y, p[1].z, p[2].x, p[2].y, p[2].z, (x, y, z) -> {
      xs[y+offset] = x;
      zs[y+offset] = z;
    });
    MathHelp.line3DToInt(p[0].x, p[0].y, p[0].z, p[2].x, p[2].y, p[2].z, (x, y, z) -> {
      if (x == xs[y+offset]) drawPixel(x, y, z, c);
      else drawLineHoriz(x, xs[y+offset], y, z, zs[y+offset], c);
    });
  }
}
