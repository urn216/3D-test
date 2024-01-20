package code.rendering;

import code.math.ray.RaySphere;
import code.math.tri.Tri3D;

import code.world.Material;

import mki.math.QuadFunction;
import mki.math.vector.Vector3;

public abstract class Constants {
  
  private static boolean normalMap = false;
  private static final QuadFunction<Vector3, Material, Double, Double, Vector3> sphereNormalFalse = (n, m, u, v) -> n;
  private static final QuadFunction<Vector3, Material, Double, Double, Vector3> sphereNormalTrue  = (n, m, u, v) -> RaySphere.displaceNormal(n, m.getNormal(u, v));
  private static QuadFunction<Vector3, Material, Double, Double, Vector3> sphereNormal = sphereNormalFalse;

  private static final QuadFunction<  Tri3D, Material, Double, Double, Vector3> triNormalFalse    = (t, m, u, v) -> t.getNormal();
  private static final QuadFunction<  Tri3D, Material, Double, Double, Vector3> triNormalTrue     = (t, m, u, v) -> t.getDisplacedNormal(m.getNormal(u, v));
  private static QuadFunction<  Tri3D, Material, Double, Double, Vector3> triNormal = triNormalFalse;


  private static boolean dynamicRasterLighting = true;


  private static QuadFunction<int[], Integer, Double, Double, Integer> filteringMode = Material::getNearestNeighbourFilteringTexel;

  public static boolean usesNormalMap() {
    return normalMap;
  }

  public static QuadFunction<Vector3, Material, Double, Double, Vector3> getSphereNormal() {
    return sphereNormal;
  }

  public static QuadFunction<Tri3D, Material, Double, Double, Vector3> getTriNormal() {
    return triNormal;
  }

  public static boolean usesDynamicRasterLighting() {
    return dynamicRasterLighting;
  }

  public static void setNormalMapUse(boolean normalMap) {
    Constants.normalMap = normalMap;
    if (normalMap) {
      sphereNormal = sphereNormalTrue;
      triNormal = triNormalTrue;
    }
    else {
      sphereNormal = sphereNormalFalse;
      triNormal = triNormalFalse;
    }
  }

  public static QuadFunction<int[], Integer, Double, Double, Integer> getFilteringMode() {
    return filteringMode;
  }

  public static void setFilteringMode(QuadFunction<int[], Integer, Double, Double, Integer> filteringMode) {
    Constants.filteringMode = filteringMode;
  }

  public static void setDynamicRasterLighting(boolean dynamicRasterLighting) {
    Constants.dynamicRasterLighting = dynamicRasterLighting;
  }
}
