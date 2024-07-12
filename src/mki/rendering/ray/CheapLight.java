package mki.rendering.ray;

import uk.ac.manchester.tornado.api.math.TornadoMath;
import uk.ac.manchester.tornado.api.types.collections.VectorFloat4;
import uk.ac.manchester.tornado.api.types.vectors.Float3;

public abstract class CheapLight {
  public static Float3 calculateLight(Float3 rayStart, Float3 sNormal, Float3 outDir, VectorFloat4 bodies, Float3 light, Float3 surfaceColour, float specularity) {
    for (int i = 0; i < bodies.getLength(); i+=4) {
      if (bodies.get(i+3).getW() == 0) continue; //if object doesn't give off light, no point looking for light

      Float3 position = bodies.get(i).asFloat3();
      Float3 otherCI = bodies.get(i+3).asFloat3(); //emitted light
      Float3 dir = Float3.normalise(Float3.sub(position, rayStart)); //direction from this surface point to other object

      float distToSurface = reaches(rayStart, dir, bodies, i);
      if (distToSurface == -1) continue;
      Float3 bodyToSrc = Float3.sub(position, rayStart);
      float distToLightSquare = Float3.dot(bodyToSrc, bodyToSrc);
      light = Float3.add(light, Float3.mult(
        Float3.mult(otherCI, TornadoMath.max(0, Float3.dot(sNormal, dir))/(12.566370614359172f * distToLightSquare)),
        Float3.add( //BRDF
          surfaceColour, //diffuse
          TornadoMath.pow(TornadoMath.max(0, Float3.dot(sNormal, Float3.normalise(Float3.add(outDir, dir)))), specularity) //specular
        )
      ));
    }
    return light;
  }

  private static float reaches(Float3 rayStart, Float3 dir, VectorFloat4 bodies, int destBody) {
    float closest = Float.MAX_VALUE;
    int close = -1;
    for (int i = 0; i < bodies.getLength(); i+=4) {
      Float3 position = bodies.get(i).asFloat3();
      float  radius   = bodies.get(i).getW();

      Float3 bodyToSrc = Float3.sub(position, rayStart);
      float distSquare = Float3.dot(bodyToSrc, bodyToSrc); //distance squared between ray and sphere centre.
      float DcosA      = Float3.dot(bodyToSrc, dir);       //'adjacent' side of the triangle.
      float distToColl = DcosA-TornadoMath.sqrt(DcosA*DcosA+radius*radius-distSquare);
      if (distToColl < -0.001f) {continue;}
      if (distToColl < closest) {closest = distToColl; close = i;}
    }
    return (close == -1 || close != destBody) ? -1 : closest;
  }
}
