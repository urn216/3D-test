package mki.rendering.gpumath;

import mki.math.vector.Vector2;
import mki.math.vector.Vector3;

public abstract class MathHelp extends mki.math.MathHelp {
  public static Vector2 sphereUVPoint(Vector3 sNormal) {
    return new Vector2(0.5 + Math.atan2(-sNormal.x, sNormal.z) / 6.283185307179586, 0.5 - Math.asin(sNormal.y) / 3.14159265358979323846);
  }

  public static double pseudoRandom(int seed) {
    seed = seed * 651754651 + 741251556;
    seed = ((seed >> ((seed >> 28) + 4)) ^ seed) * 325261619;
    seed = (seed >> 22) ^ seed;
    return (double)seed / Integer.MAX_VALUE;
  }

  public static double pseudoRandomNormal(int seed) {
    return Math.sqrt(-2*Math.log(pseudoRandom(seed^~0))) * Math.cos(2*3.14159265358979323846f*pseudoRandom(seed));
  }

  public static double randomNormal() {
    return Math.sqrt(-2*Math.log((float)Math.random())) * Math.cos(2*3.14159265358979323846f*Math.random());
  }

  public static Vector3 randomHemisphereRay(Vector3 normal, int seed) {
    Vector3 res = new Vector3(
      MathHelp.pseudoRandomNormal((int)((seed^1152352616)*1024*normal.x+21314)),
      MathHelp.pseudoRandomNormal((int)((seed^741627484)*1024*normal.y+14155)),
      MathHelp.pseudoRandomNormal((int)((seed^563785855)*1024*normal.z+85626))
    ).normal();
    
    return res.add(normal).normal();
  }

  public static Vector3 randomHemisphereRay(Vector3 normal) {
    Vector3 res = new Vector3(
      MathHelp.randomNormal(),
      MathHelp.randomNormal(),
      MathHelp.randomNormal()
    ).normal();
    
    return res.add(normal).normal();
  }
}
