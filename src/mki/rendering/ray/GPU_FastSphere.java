package mki.rendering.ray;

// import mki.rendering.gpumath.MathHelp;
// import mki.rendering.gpumath.Quaternion;
// import mki.world.Material;
import uk.ac.manchester.tornado.api.math.TornadoMath;
import uk.ac.manchester.tornado.api.types.collections.VectorFloat4;
// import uk.ac.manchester.tornado.api.types.vectors.Float2;
import uk.ac.manchester.tornado.api.types.vectors.Float3;

public class GPU_FastSphere {

  private static final int NUM_REFLECTIONS = 2;

  public static Float3 getColour(Float3 start, Float3 dir, VectorFloat4 bodies) {
    Float3 rayStart = start; 
    Float3 rayDir = dir;
    float prevReflectivity = 1f;
    Float3 totalLight = new Float3();
    Float3 surfaceColour = new Float3(1, 1, 1);
    for (int ref = -1; ref < NUM_REFLECTIONS; ref++) {
      int close = -1;
      float closest = Float.MAX_VALUE;
      for (int i = 0; i < bodies.getLength(); i+=4) {
        Float3 position = bodies.get(i).asFloat3();
        float  radius   = bodies.get(i).getW();

        Float3 bodyToCam = Float3.sub(position, rayStart);
        float distSquare = Float3.dot(bodyToCam, bodyToCam); //distance squared between ray and sphere centre.
        float DcosA      = Float3.dot(bodyToCam, rayDir);       //'adjacent' side of the triangle.
        float distToColl = DcosA-TornadoMath.sqrt(DcosA*DcosA+radius*radius-distSquare);
        if (distToColl < 0f) {continue;}
        if (distToColl < closest) {closest = distToColl; close = i;}
      }
      if (close == -1) { // We did not collide with an object, so we see skybox.
        //get the uv coordinates for this point in skybox
        // return Material.getSkyColour(0.5 + Math.atan2(dir.getX(), dir.getZ()) / 6.283185307179586, 0.5 - TornadoMath.asin(dir.getY()) / Math.PI);
        break;
      }
      // We did collide with an object, so let's look at it.
      Float3 surface = Float3.add(rayStart, Float3.mult(rayDir, closest));
      Float3 sNormal = Float3.normalise(Float3.sub(surface, bodies.get(close).asFloat3()));
      // Float2 Puv = MathHelp.sphereUVPoint(Quaternion.rotate(Quaternion.reverse(bodies.get(close+1)), sNormal)); //get the uv coordinates for this point
      // Material mat = close.getModel().getMat();

      // sNormal = Constants.getSphereNormal().apply(sNormal, mat, Puv.x, Puv.y);

      Float3 emittedLight = bodies.get(close+3).asFloat3();
      // totalLight = Float3.add(totalLight, Float3.mult(emittedLight, surfaceColour));
      surfaceColour = Float3.mult(surfaceColour, bodies.get(close+2).asFloat3());
      float reflectivity = bodies.get(close+2).getW();
      Float3 light = calculateLight(surface, sNormal, Float3.mult(rayDir, -1), bodies, emittedLight, reflectivity, 20*TornadoMath.tan(1.56688f*reflectivity));

      // if (numRef>0 && mat.getReflectivity() != 0) {return mat.getReflection(getColour(surface, dir.subtract(sNormal.scale(2*sNormal.dot(dir))), bodies, numSteps-1, numRef-1), intensity, Puv.x, Puv.y);}
      totalLight = Float3.add(totalLight, Float3.mult(light, Float3.mult(surfaceColour, prevReflectivity)));
      if (reflectivity <= 0) break;
      prevReflectivity *= reflectivity * reflectivity;
      rayStart = surface;
      rayDir = Float3.sub(rayDir, Float3.mult(sNormal, 2*Float3.dot(sNormal, rayDir)));
    }

    return totalLight;
  }

  public static Float3 calculateLight(Float3 rayStart, Float3 sNormal, Float3 outDir, VectorFloat4 bodies, Float3 light, float reflectivity, float specularity) {
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
        Float3.mult(otherCI, TornadoMath.max(0, Float3.dot(sNormal, dir))/(12.566370614359172f * distToLightSquare)), //BRDF
        1-reflectivity + //diffuse
        TornadoMath.pow(TornadoMath.max(0, Float3.dot(sNormal, Float3.normalise(Float3.add(outDir, dir)))), specularity) * reflectivity //specular
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

  // public static Vector3 displaceNormal(Vector3 normal, Vector3 displacement) {
  //   Vector3 pU = normal.y > 0.99 || normal.y < -0.99 ? new Vector3(-1, 0, 0) : Vector3.UNIT_Y.cross(normal).unitize();
  //   Vector3 pV = normal.cross(pU).unitize();
    
  //   return normal.subtract(pU.scale(displacement.x)).add(pV.scale(displacement.y)).subtract(normal.scale(displacement.z)).unitize();
  // }
}
