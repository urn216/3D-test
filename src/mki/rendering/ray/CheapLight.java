package mki.rendering.ray;

import mki.math.vector.Vector3F;
import mki.math.vector.Vector4F;

public abstract class CheapLight {
  public static Vector3F calculateLight(Vector3F rayStart, Vector3F sNormal, Vector3F outDir, Vector4F[] bodies, Vector3F light, Vector3F surfaceColour, float specularity) {
    for (int i = 0; i < bodies.length; i+=4) {
      if (bodies[i+3].z == 0) continue; //if object doesn't give off light, no point looking for light

      Vector3F position = new Vector3F(bodies[i].w, bodies[i].x, bodies[i].y);
      Vector3F otherCI = new Vector3F(bodies[i+3].w, bodies[i+3].x, bodies[i+3].y); //emitted light
      Vector3F dir = position.subtract(rayStart).normal(); //direction from this surface point to other object

      float distToSurface = reaches(rayStart, dir, bodies, i);
      if (distToSurface == -1) continue;
      Vector3F bodyToSrc = position.subtract(rayStart);
      float distToLightSquare = bodyToSrc.dot(bodyToSrc);
      light = light.add(
        otherCI.scale(Math.max(0, sNormal.dot(dir))/(12.566370614359172f * distToLightSquare))
        .scale( //BRDF
          surfaceColour.add( //diffuse
          (float)Math.pow(Math.max(0, sNormal.dot(outDir.add(dir).normal())), specularity) //specular
        )
      ));
    }
    return light;
  }

  private static float reaches(Vector3F rayStart, Vector3F dir, Vector4F[] bodies, int destBody) {
    float closest = Float.MAX_VALUE;
    int close = -1;
    for (int i = 0; i < bodies.length; i+=4) {
      Vector3F position = new Vector3F(bodies[i].w, bodies[i].x, bodies[i].y);
      float    radius   = bodies[i].z;

      Vector3F bodyToSrc = position.subtract(rayStart);
      float distSquare = bodyToSrc.magsquare(); //distance squared between ray and sphere centre.
      float DcosA      = bodyToSrc.dot(dir);       //'adjacent' side of the triangle.
      float distToColl = DcosA-(float)Math.sqrt(DcosA*DcosA+radius*radius-distSquare);
      if (distToColl < -0.001f) {continue;}
      if (distToColl < closest) {closest = distToColl; close = i;}
    }
    return (close == -1 || close != destBody) ? -1 : closest;
  }
}
