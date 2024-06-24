package mki.world;

import mki.io.FileIO;

import mki.math.vector.Vector3;
import mki.rendering.Constants;

public class Material {
  private static final int RED_MASK = 255 << 16;
  private static final int GREEN_MASK = 255 << 8;
  private static final int BLUE_MASK = 255;
  private static final int ALPHA_MASK = 255 << 24;

  private int baseColour = ~0;
  private final boolean emissive;
  private final Vector3 emissivity;
  private final float reflectivity;

  private final int[][] texture;
  private final int[][] normals;
  private final float[] normalMagnitudes;

  public Material() {
    this(~0, 0, new Vector3(), Texture.getTexture("DEFAULT"), Texture.getTexture("DEFAULT_NORMAL"));
  }
  
  public Material(java.awt.Color baseColour) {
    this(baseColour, 0, new Vector3(), Texture.getTexture("DEFAULT"), Texture.getTexture("DEFAULT_NORMAL"));
  }
  
  public Material(java.awt.Color baseColour, float reflectivity) {
    this(baseColour, reflectivity, new Vector3(), Texture.getTexture("DEFAULT"), Texture.getTexture("DEFAULT_NORMAL"));
  }

  public Material(java.awt.Color baseColour, float reflectivity, Vector3 emissivity) {
    this(baseColour, reflectivity, emissivity, Texture.getTexture("DEFAULT"), Texture.getTexture("DEFAULT_NORMAL"));
  }

  public Material(java.awt.Color baseColour, float reflectivity, Vector3 emissivity, String tFile) {
    this(baseColour, reflectivity, emissivity, Texture.getTexture(tFile), Texture.getTexture("DEFAULT_NORMAL"));
  }

  public Material(java.awt.Color baseColour, float reflectivity, Vector3 emissivity, String tFile, String nFile) {
    this(baseColour, reflectivity, emissivity, Texture.getTexture(tFile), Texture.getTexture(nFile));
  }

  public Material(java.awt.Color baseColour, float reflectivity, Vector3 emissivity, int[][] texture, int[][] normals) {
    this(baseColour.getRGB(), reflectivity, emissivity, texture, normals);
  }
  
  public Material(int baseColour) {
    this(baseColour, 0, new Vector3(), Texture.getTexture("DEFAULT"), Texture.getTexture("DEFAULT_NORMAL"));
  }
  
  public Material(int baseColour, float reflectivity) {
    this(baseColour, reflectivity, new Vector3(), Texture.getTexture("DEFAULT"), Texture.getTexture("DEFAULT_NORMAL"));
  }

  public Material(int baseColour, float reflectivity, Vector3 emissivity) {
    this(baseColour, reflectivity, emissivity, Texture.getTexture("DEFAULT"), Texture.getTexture("DEFAULT_NORMAL"));
  }

  public Material(int baseColour, float reflectivity, Vector3 emissivity, String tFile) {
    this(baseColour, reflectivity, emissivity, Texture.getTexture(tFile), Texture.getTexture("DEFAULT_NORMAL"));
  }

  public Material(int baseColour, float reflectivity, Vector3 emissivity, String tFile, String nFile) {
    this(baseColour, reflectivity, emissivity, Texture.getTexture(tFile), Texture.getTexture(nFile));
  }

  public Material(int baseColour, float reflectivity, Vector3 emissivity, int[][] texture, int[][] normals) {
    if (emissivity.x < 0 || emissivity.y < 0 || emissivity.z < 0) {throw new RuntimeException("Cannot have negative light intensity!");}
    if (reflectivity < 0 || reflectivity > 1) {throw new RuntimeException("Reflectivity must be a percentage (0 <= r <= 1)!");}
    this.baseColour = baseColour;
    this.reflectivity = reflectivity;
    this.emissive = emissivity.x+emissivity.y+emissivity.z != 0;
    this.emissivity = emissivity;

    this.texture = texture;
    
    this.normals = normals;
    
    this.normalMagnitudes = new float[this.normals[0].length];
    for (int i = 0; i < normalMagnitudes.length; i++) {
      int norm = this.normals[0][i];
      this.normalMagnitudes[i] = (float)new Vector3(((norm&RED_MASK)>>16)/128.0 - 1, ((norm&GREEN_MASK)>>8)/128.0 - 1, (norm&BLUE_MASK)/-128.0 + 1).magnitude();
    }
  }

  private static int colourFix(Vector3 intensity, int... colours) {
    double r = intensity.x, g = intensity.y, b = intensity.z;
    for (int i = 0; i < colours.length; i++) {
      // a *= ((colours[i] & ALPHA_MASK) >> 24);
      r *= ((colours[i] & RED_MASK  ) >> 16);
      g *= ((colours[i] & GREEN_MASK) >>  8);
      b *= ((colours[i] & BLUE_MASK )      );
    }
    double denom = Math.pow(255, colours.length-1);
    return(
      // (int)Math.min(a/denom, 255) << 24 |
      ALPHA_MASK |
      (int)Math.min(r/denom, 255) << 16 |
      (int)Math.min(g/denom, 255) <<  8 |
      (int)Math.min(b/denom, 255)
    );
  }

  public static int blendColours(int one, double p, int two) {
    return (
      ALPHA_MASK |
      (int)(((one & RED_MASK  ) >> 16) * p + ((two & RED_MASK  ) >> 16) * (1-p)) << 16 |
      (int)(((one & GREEN_MASK) >>  8) * p + ((two & GREEN_MASK) >>  8) * (1-p)) <<  8 |
      (int)(((one & BLUE_MASK )      ) * p + ((two & BLUE_MASK )      ) * (1-p))
    );
  }

  public void setBaseColour(int baseColour) {
    this.baseColour = baseColour;
  }

  public void setBaseColour(java.awt.Color baseColour) {
    this.baseColour = baseColour.getRGB();
  }

  public int getBaseColour() {
    return this.baseColour;
  }

  public int getIntenseColour(Vector3 recievedIntensity) {
    return colourFix(recievedIntensity, baseColour);
  }

  public int getIntenseColour(Vector3 recievedIntensity, double u, double v) {
    return colourFix(recievedIntensity, baseColour, Constants.getFilteringMode().apply(texture, u, v));
  }

  public Vector3 getEmissivity() {
    return emissivity;
  }

  public Vector3 getAdjIntensity(Vector3 recievedIntensity) {
    return new Vector3(
      ((baseColour & RED_MASK  ) >> 16)/255.0*recievedIntensity.x,
      ((baseColour & GREEN_MASK) >>  8)/255.0*recievedIntensity.y,
      ((baseColour & BLUE_MASK )      )/255.0*recievedIntensity.z
    );
  }

  public boolean isEmissive() {
    return emissive;
  }

  public int[] getNormalMap() {
    return normals[0];
  }

  public float getReflectivity() {
    return reflectivity;
  }

  public int[] getTexture() {
    return texture[0];
  }

  public int getTextureWidth() {
    return texture[1][0];
  }

  public int getTextureHeight() {
    return texture[1][1];
  }

  public int getReflection(int reflectedColour, Vector3 recievedIntensity) {
    double r = (
      ((reflectedColour & RED_MASK  ) >> 16)*   reflectivity +
      ((     baseColour & RED_MASK  ) >> 16)/255.0*(1-reflectivity)
    );
    double g = (
      ((reflectedColour & GREEN_MASK) >>  8)*   reflectivity +
      ((     baseColour & GREEN_MASK) >>  8)/255.0*(1-reflectivity)
    );
    double b = (
      ( reflectedColour & BLUE_MASK        )*   reflectivity +
      (      baseColour & BLUE_MASK        )/255.0*(1-reflectivity)
    );

    return (ALPHA_MASK)
    | ((int)Math.min(255, r*recievedIntensity.x) << 16)
    | ((int)Math.min(255, g*recievedIntensity.y) <<  8)
    | ((int)Math.min(255, b*recievedIntensity.z)      );
  }

  public int getReflection(int reflectedColour, Vector3 recievedIntensity, double u, double v) {
    int textureColour = Constants.getFilteringMode().apply(texture, u, v);

    double r = (
      ((reflectedColour & RED_MASK  ) >> 16)*   reflectivity +
      ((  textureColour & RED_MASK  ) >> 16)*(1-reflectivity)*
      ((     baseColour & RED_MASK  ) >> 16)/255.0
    );
    double g = (
      ((reflectedColour & GREEN_MASK) >>  8)*   reflectivity +
      ((  textureColour & GREEN_MASK) >>  8)*(1-reflectivity)*
      ((     baseColour & GREEN_MASK) >>  8)/255.0
    );
    double b = (
      ( reflectedColour & BLUE_MASK        )*   reflectivity +
      (   textureColour & BLUE_MASK        )*(1-reflectivity)*
      (      baseColour & BLUE_MASK        )/255.0
    );

    return (ALPHA_MASK)
    | ((int)Math.min(255, r*recievedIntensity.x) << 16)
    | ((int)Math.min(255, g*recievedIntensity.y) <<  8)
    | ((int)Math.min(255, b*recievedIntensity.z)      );
  }

  public Vector3 getNormal(double u, double v) {
    int rgb = Constants.getFilteringMode().apply(normals, u, v);
    float magnitude = normalMagnitudes[((int)(u*normals[1][0])+(int)(v*normals[1][1])*normals[1][0]) % normalMagnitudes.length];

    return new Vector3(
      (((rgb &   RED_MASK)>>16) /  128.0 - 1) / magnitude, 
      (((rgb & GREEN_MASK)>> 8) /  128.0 - 1) / magnitude, 
      (( rgb &  BLUE_MASK     ) / -128.0 + 1) / magnitude
    );
  }

  public static int getNearestNeighbourFilteringTexel(int[][] texture, double u, double v) {
    int w = texture[1][0], h = texture[1][1];
    return texture[0][((int)(u*w)+(int)(v*h)*w) % texture[0].length];
  }

  public static int getBilinearFilteringTexel(int[][] texture, double u, double v) {
    int w = texture[1][0], h = texture[1][1];
    
    u = (u%1)*w;
    int uMin = (int)u;
    int uMax = (uMin+1)%w;
    double uFrc = u-uMin;

    v = (v%1)*h;
    int vMin = (int)v;
    int vMax = (vMin+1)%h;
    double vFrc = v-vMin;

    int TL = texture[0][(uMin+vMin*w)];
    int TR = texture[0][(uMax+vMin*w)];
    int BL = texture[0][(uMin+vMax*w)];
    int BR = texture[0][(uMax+vMax*w)];

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

  private static int[] skybox = {-16777216};
  private static int skyboxSize = 1;

  public static void setSkybox(String tFile) {
    skybox = FileIO.readImageInt(tFile);
    skyboxSize = (int)Math.sqrt(skybox.length);
  }
  
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
