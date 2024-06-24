package mki.world;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import mki.io.FileIO;

public abstract class Texture {
  private static final Map<String, int[][]> textureCache = new HashMap<>();

  static {
    clearTextureCache();
  }

  public static void createTexture(String textureName, int w, int h, int... texture) {
    textureCache.put(textureName, new int[][]{texture, {w, h}});
  }

  public static int[][] getTexture(String textureName) {
    int[][] texture = textureCache.get(textureName);

    if (texture != null) return texture;

    BufferedImage img = FileIO.readImage(textureName);
    int w = img.getWidth(), h = img.getHeight();
    texture = new int[][] {img.getRGB(0, 0, w, h, null, 0, w), {w, h}};

    textureCache.put(textureName, texture);

    return texture;
  }

  public static int[] getTextureSize(String textureName) {
    return textureCache.get(textureName)[1];
  }

  public static void clearTextureCache() {
    textureCache.clear();

    createTexture("DEFAULT", 1, 1, ~0);
    createTexture("DEFAULT_NORMAL", 1, 1, -8355585);
  }
}
