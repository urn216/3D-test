package code.world;

import mki.io.FileIO;

import mki.math.vector.Vector3;
import mki.math.vector.Vector3I;

public class Material {
  private static final int RED_MASK = 255 << 16;
  private static final int GREEN_MASK = 255 << 8;
  private static final int BLUE_MASK = 255;
  private static final int ALPHA_MASK = 255 << 24;

  private final int r, g, b;
  private final float rf, gf, bf;
  private final boolean emissive;
  private final Vector3 intensity;
  private final float reflectivity;

  private final int[] texture;
  private final int tSize;

  public Material(Vector3I rgb, float reflectivity, Vector3 intensity) {
    this(rgb, reflectivity, intensity, "decal/mono.png");
  }

  public Material(Vector3I rgb, float reflectivity, Vector3 intensity, String tFile) {
    if (rgb.x > 255 || rgb.x < 0 || rgb.y > 255 || rgb.y < 0 || rgb.z > 255 || rgb.z < 0) {throw new RuntimeException("rgb must be between 0 and 255!");}
    if (intensity.x < 0 || intensity.y < 0 || intensity.z < 0) {throw new RuntimeException("Cannot have negative light intensity!");}
    if (reflectivity < 0 || reflectivity > 1) {throw new RuntimeException("Reflectivity must be a percentage (0 <= r <= 1)!");}
    this.r = rgb.x;
    this.rf = r/255f;
    this.g = rgb.y;
    this.gf = g/255f;
    this.b = rgb.z;
    this.bf = b/255f;
    this.reflectivity = reflectivity;
    this.emissive = intensity.x+intensity.y+intensity.z != 0;
    this.intensity = intensity;

    this.texture = Texture.getTexture(tFile);
    this.tSize = (int)Math.sqrt(this.texture.length);
  }

  private static int colourFix(double col) {return Math.min(255, (int)col);}

  public int getAbsColour() {return (ALPHA_MASK) | (r << 16) | (g << 8) | (b);}

  public int getIntenseColour(Vector3 oIntensity) {return (ALPHA_MASK) | (colourFix(r*oIntensity.x) << 16) | (colourFix(g*oIntensity.y) << 8) | (colourFix(b*oIntensity.z));}

  public int getIntenseColour(Vector3 oIntensity, double u, double v) {
    int rgb = getNearestNeighbourFilteringTexel(u, v);

    return (ALPHA_MASK) | (colourFix(((rgb & RED_MASK) >> 16)*this.rf*oIntensity.x) << 16) | (colourFix(((rgb & GREEN_MASK) >> 8)*this.gf*oIntensity.y) << 8) | (colourFix((rgb & BLUE_MASK)*this.bf*oIntensity.z));
  }

  public Vector3 getIntensity() {
    return intensity;
  }

  public Vector3 getAdjIntensity(Vector3 oIntensity) {
    return new Vector3(rf*oIntensity.x, gf*oIntensity.y, bf*oIntensity.z);
  }

  public boolean isEmissive() {
    return emissive;
  }

  public float getReflectivity() {
    return reflectivity;
  }

  public int[] getTexture() {
    return texture;
  }

  public int getTextureSize() {
    return tSize;
  }

  public int getReflection(int other, Vector3 oIntensity) {
    float r = ((other & RED_MASK  ) >> 16)*reflectivity+this.r*(1-reflectivity);
    float g = ((other & GREEN_MASK) >> 8 )*reflectivity+this.g*(1-reflectivity);
    float b = ( other & BLUE_MASK        )*reflectivity+this.b*(1-reflectivity);

    return (ALPHA_MASK) | (colourFix(r*oIntensity.x) << 16) | (colourFix(g*oIntensity.y) << 8) | (colourFix(b*oIntensity.z));
  }

  public int getReflection(int other, Vector3 oIntensity, double u, double v) {
    int rgb = getNearestNeighbourFilteringTexel(u, v);

    float r = ((other & RED_MASK  ) >> 16)*reflectivity+((rgb & RED_MASK  ) >> 16)*(this.r/255f)*(1-reflectivity);
    float g = ((other & GREEN_MASK) >> 8 )*reflectivity+((rgb & GREEN_MASK) >> 8 )*(this.g/255f)*(1-reflectivity);
    float b = ( other & BLUE_MASK        )*reflectivity+( rgb & BLUE_MASK        )*(this.b/255f)*(1-reflectivity);

    return (ALPHA_MASK) | (colourFix(r*oIntensity.x) << 16) | (colourFix(g*oIntensity.y) << 8) | (colourFix(b*oIntensity.z));
  }

  public int getNearestNeighbourFilteringTexel(double u, double v) {
    return texture[((int)(u*tSize)+(int)(v*tSize)*tSize) % texture.length];
  }

  public int getBilinearFilteringTexel(double u, double v) {
    u = (u%1)*tSize;
    int uMin = (int)u;
    int uMax = (uMin+1)%tSize;
    double uFrc = u-uMin;

    v = (v%1)*tSize;
    int vMin = (int)v;
    int vMax = (vMin+1)%tSize;
    double vFrc = v-vMin;

    int TL = texture[(uMin+vMin*tSize)];
    int TR = texture[(uMax+vMin*tSize)];
    int BL = texture[(uMin+vMax*tSize)];
    int BR = texture[(uMax+vMax*tSize)];

    return ALPHA_MASK |
    ((int)(
      (1-vFrc) * ((1-uFrc)*((TL&RED_MASK  )>>16)+(uFrc) * ((TR&RED_MASK)  >>16)) +
      (  vFrc) * ((1-uFrc)*((BL&RED_MASK  )>>16)+(uFrc) * ((BR&RED_MASK)  >>16))
    ) << 16) |
    ((int)(
      (1-vFrc) * ((1-uFrc)*((TL&GREEN_MASK)>> 8)+(uFrc) * ((TR&GREEN_MASK)>> 8)) +
      (  vFrc) * ((1-uFrc)*((BL&GREEN_MASK)>> 8)+(uFrc) * ((BR&GREEN_MASK)>> 8))
    ) << 8 )|
    ((int)(
      (1-vFrc) * ((1-uFrc)*((TL&BLUE_MASK )    )+(uFrc) * ((TR&BLUE_MASK )    )) +
      (  vFrc) * ((1-uFrc)*((BL&BLUE_MASK )    )+(uFrc) * ((BR&BLUE_MASK )    ))
    ));
  }

  //////////
  /////   STATIC METHODS
  //////////

  private static final int[] skybox = FileIO.readImageInt("BG/star_map.png");
  private static final int skyboxSize = (int)Math.sqrt(skybox.length);
  /**
  * A static method for getting a point in a skybox.
  *
  * @param u the u coordinate in the skybox sphere
  * @param v the v coordinate in the skybox sphere
  *
  * @return the ARGB int value for this point in the sphere
  */
  public static int getSkyColour(double u, double v) {
    return skybox[(int)(u*(skyboxSize-1))+(int)(v*(skyboxSize-1))*skyboxSize];
  }
}
