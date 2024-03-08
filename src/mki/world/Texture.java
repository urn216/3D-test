package mki.world;

import java.util.HashMap;
import java.util.Map;

import mki.io.FileIO;

public class Texture {
  private static final Map<String, int[]> textureCache = new HashMap<>();

  public static int[] getTexture(String tFile) {
    int[] texture = textureCache.get(tFile);

    if (texture != null) return texture;

    texture = FileIO.readImageInt(tFile);

    textureCache.put(tFile, texture);

    return texture;
  }

  public static void clearTextureCache() {
    textureCache.clear();
  }
}
