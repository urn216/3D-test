package code.rendering;

import java.awt.image.BufferedImage;

import code.math.MathHelp;
import code.math.tri.Tri2D;
import code.math.vector.Vector2;
import code.math.vector.Vector2I;
import code.world.Material;

public class Drawing {
  private final int[] contents;
  private final int width;
  private final int height;
  private final double aspectRatio;

  public Drawing(int width, int height) {
    this.contents = new int[width*height];
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
    }
  }

  public void drawPixel(int x, int y, int c) {
    contents[x+y*width] = c;
  }

  public void drawLine(int x1, int y1, int x2, int y2, int c) {
    MathHelp.performAlongIntLine(x1, y1, x2, y2, (x, y) -> drawPixel(x, y, c));
  }

  public void drawLineHoriz(int x1, int x2, int y, int c) {
    int sx = (((x2-x1)>>31)<<1)+1; //normalises dx
    for (; x1!=x2; x1+=sx) {
      drawPixel(x1, y, c);
    }
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

  public void fillTri(Tri2D tri, Material mat) {
    Vector2[] p = new Vector2[] {tri.getVerts()[0], tri.getVerts()[1], tri.getVerts()[2]};
    if (p[1].y < p[0].y) {MathHelp.swap(p, 0, 1);}
    if (p[2].y < p[0].y) {MathHelp.swap(p, 0, 2);}
    if (p[2].y < p[1].y) {MathHelp.swap(p, 1, 2);}

    int[] xs = new int[(int)(p[2].y-p[0].y)];

    MathHelp.performAlongIntLine((int)p[0].x, (int)p[0].y, (int)p[1].x, (int)p[1].y, (x, y) -> {xs[y] = x;});
    MathHelp.performAlongIntLine((int)p[1].x, (int)p[1].y, (int)p[2].x, (int)p[2].y, (x, y) -> {xs[y] = x;});
    MathHelp.performAlongIntLine((int)p[0].x, (int)p[0].y, (int)p[2].x, (int)p[2].y, (x, y) -> {
      drawLineHoriz(x, xs[y], y, mat.getAbsColour());
    });
  }

  public void fillTri(Tri2D tri, int c) {
    Vector2I[] p = new Vector2I[] {tri.getVerts()[0].round(), tri.getVerts()[1].round(), tri.getVerts()[2].round()};
    if (p[1].y < p[0].y) {MathHelp.swap(p, 0, 1);}
    if (p[2].y < p[0].y) {MathHelp.swap(p, 0, 2);}
    if (p[2].y < p[1].y) {MathHelp.swap(p, 1, 2);}

    int[] xs = new int[p[2].y-p[0].y+1];

    MathHelp.performAlongIntLine(p[0].x, p[0].y, p[1].x, p[1].y, (x, y) -> {xs[y-p[0].y] = x;});
    int offset = p[1].y-p[0].y;
    MathHelp.performAlongIntLine(p[1].x, p[1].y, p[2].x, p[2].y, (x, y) -> {xs[y-p[1].y+offset] = x;});
    MathHelp.performAlongIntLine(p[0].x, p[0].y, p[2].x, p[2].y, (x, y) -> {
      drawLineHoriz(x, xs[y-p[0].y], y, c);
    });//TODO fix black streaks
  }
}
