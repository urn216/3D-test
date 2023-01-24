package code.core.scene;
import code.math.IOHelp;
import code.math.Vector3;

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

  public Material(Vector3 rgb, float reflectivity, Vector3 intensity) {
    this(rgb, reflectivity, intensity, "decal/mono.png");
  }

  public Material(Vector3 rgb, float reflectivity, Vector3 intensity, String tFile) {
    if (rgb.x > 255 || rgb.x < 0 || rgb.y > 255 || rgb.y < 0 || rgb.z > 255 || rgb.z < 0) {throw new RuntimeException("rgb must be between 0 and 255!");}
    if (intensity.x < 0 || intensity.y < 0 || intensity.z < 0) {throw new RuntimeException("Cannot have negative light intensity!");}
    if (reflectivity < 0 || reflectivity > 1) {throw new RuntimeException("Reflectivity must be a percentage (0 <= r <= 1)!");}
    this.r = (int)rgb.x;
    this.rf = r/255f;
    this.g = (int)rgb.y;
    this.gf = g/255f;
    this.b = (int)rgb.z;
    this.bf = b/255f;
    this.reflectivity = reflectivity;
    this.emissive = intensity.x+intensity.y+intensity.z != 0;
    this.intensity = intensity;

    texture = IOHelp.readImageInt(tFile);
    tSize = (int)Math.sqrt(texture.length);
  }

  private static int colourFix(double col) {return (int)Math.min(255, col);}

  public int getAbsColour() {return (ALPHA_MASK) | (r << 16) | (g << 8) | (b);}

  public int getIntenseColour(Vector3 oIntensity) {return (ALPHA_MASK) | (colourFix(r*oIntensity.x) << 16) | (colourFix(g*oIntensity.y) << 8) | (colourFix(b*oIntensity.z));}

  public int getIntenseColour(Vector3 oIntensity, double u, double v) {
    int rgb = texture[(int)(u*(tSize))+(int)(v*(tSize))*tSize];
    return (ALPHA_MASK) | (colourFix(((rgb & RED_MASK) >> 16)*(this.r/255f)*oIntensity.x) << 16) | (colourFix(((rgb & GREEN_MASK) >> 8)*(this.g/255f)*oIntensity.y) << 8) | (colourFix((rgb & BLUE_MASK)*(this.b/255f)*oIntensity.z));
  }

  public Vector3 getIntensity() {return intensity;}

  public Vector3 getAdjIntensity(Vector3 oIntensity) {return new Vector3(rf*oIntensity.x, gf*oIntensity.y, bf*oIntensity.z);}

  public boolean isEmissive() {return emissive;}

  public float getReflectivity() {return reflectivity;}

  public int getReflection(int other, Vector3 oIntensity) {
    double r = ((other & RED_MASK) >> 16)*reflectivity+this.r*(1f-reflectivity);
    double g = ((other & GREEN_MASK) >> 8)*reflectivity+this.g*(1f-reflectivity);
    double b = (other & BLUE_MASK)*reflectivity+this.b*(1f-reflectivity);
    return (ALPHA_MASK) | (colourFix(r*oIntensity.x) << 16) | (colourFix(g*oIntensity.y) << 8) | (colourFix(b*oIntensity.z));
  }

  public int getReflection(int other, Vector3 oIntensity, double u, double v) {
    int rgb = texture[(int)(u*(tSize))+(int)(v*(tSize))*tSize];

    double r = ((other & RED_MASK) >> 16)*reflectivity+((rgb & RED_MASK) >> 16)*(this.r/255f)*(1f-reflectivity);
    double g = ((other & GREEN_MASK) >> 8)*reflectivity+((rgb & GREEN_MASK) >> 8)*(this.g/255f)*(1f-reflectivity);
    double b = (other & BLUE_MASK)*reflectivity+(rgb & BLUE_MASK)*(this.b/255f)*(1f-reflectivity);
    return (ALPHA_MASK) | (colourFix(r*oIntensity.x) << 16) | (colourFix(g*oIntensity.y) << 8) | (colourFix(b*oIntensity.z));
  }


  private static final int[] skybox = IOHelp.readImageInt("BG/star_map.png");
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
