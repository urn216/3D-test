package mki.rendering.gpumath;

import uk.ac.manchester.tornado.api.math.TornadoMath;
import uk.ac.manchester.tornado.api.types.vectors.Float2;
import uk.ac.manchester.tornado.api.types.vectors.Float3;

public abstract class MathHelp extends mki.math.MathHelp {
  public static Float2 sphereUVPoint(Float3 sNormal) {
    return new Float2(0.5f + TornadoMath.atan2(-sNormal.getX(), sNormal.getZ()) / 6.283185307179586f, 0.5f - TornadoMath.asin(sNormal.getY()) / 3.14159265358979323846f);
  }

  public static float pseudoRandom(int seed) {
    seed = seed * 651754651 + 741251556;
    seed = ((seed >> ((seed >> 28) + 4)) ^ seed) * 325261619;
    seed = (seed >> 22) ^ seed;
    return (float)seed / Integer.MAX_VALUE;
  }

  public static float pseudoRandomNormal(int seed) {
    return TornadoMath.sqrt(-2*TornadoMath.log(pseudoRandom(seed^~0))) * TornadoMath.cos(2*3.14159265358979323846f*pseudoRandom(seed));
  }

  public static float randomNormal() {
    return TornadoMath.sqrt(-2*TornadoMath.log((float)Math.random())) * TornadoMath.cos(2*3.14159265358979323846f*(float)Math.random());
  }

  public static Float3 randomHemisphereRay(Float3 normal, int seed) {
    Float3 res = Float3.normalise(new Float3(
      MathHelp.pseudoRandomNormal((int)((seed^1152352616)*1024*normal.getX()+21314)),
      MathHelp.pseudoRandomNormal((int)((seed^741627484)*1024*normal.getY()+14155)),
      MathHelp.pseudoRandomNormal((int)((seed^563785855)*1024*normal.getZ()+85626))
    ));
    
    return Float3.normalise(Float3.add(res, normal));
  }

  public static Float3 randomHemisphereRay(Float3 normal) {
    Float3 res = Float3.normalise(new Float3(
      MathHelp.randomNormal(),
      MathHelp.randomNormal(),
      MathHelp.randomNormal()
    ));
    
    return Float3.normalise(Float3.add(res, normal));
  }
}
