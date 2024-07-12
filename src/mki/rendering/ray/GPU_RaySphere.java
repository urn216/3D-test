package mki.rendering.ray;

import mki.rendering.gpumath.MathHelp;
// import mki.rendering.gpumath.MathHelp;
// import mki.rendering.gpumath.Quaternion;
// import mki.world.Material;
import uk.ac.manchester.tornado.api.math.TornadoMath;
import uk.ac.manchester.tornado.api.types.collections.VectorFloat4;
// import uk.ac.manchester.tornado.api.types.vectors.Float2;
import uk.ac.manchester.tornado.api.types.vectors.Float3;

public class GPU_RaySphere {

  private static final int NUM_REFLECTIONS = 1;
  // private static final float SPECULARITY = 100;

  public static Float3 getColour(Float3 rayStart, Float3 rayDir, VectorFloat4 bodies, int seed) {
    Float3 totalLight = new Float3();
    Float3 surfaceColour = new Float3(1, 1, 1);
    for (int ref = -2; ref < NUM_REFLECTIONS; ref++) {
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
      totalLight = Float3.add(totalLight, Float3.mult(emittedLight, surfaceColour));
      surfaceColour = Float3.mult(surfaceColour, bodies.get(close+2).asFloat3());

      rayStart = surface;
      rayDir = reflect(rayDir, sNormal, bodies.get(close+2).getW(), seed);
      // rayDir = Float3.sub(rayDir, Float3.mult(sNormal, 2*Float3.dot(sNormal, rayDir)));
      // rayDir = MathHelp.randomHemisphereRay(sNormal, seed);
    }

    return totalLight;
  }

  // Reflect ray towards the weighted-midpoint between a diffuse reflection and a specular reflection
  private static Float3 reflect(Float3 in, Float3 normal, float reflectivity, int seed) {
    return Float3.normalise(Float3.add(
      Float3.mult(Float3.sub(in, Float3.mult(normal, 2*Float3.dot(normal, in))), reflectivity),
      Float3.mult(MathHelp.randomHemisphereRay(normal, seed), 1-reflectivity)
    ));
  }

  // Reflect ray with random chance of diffuse reflection or specular reflection based on reflectivity.
  // private static Float3 reflect(Float3 in, Float3 normal, float reflectivity, int seed) {
  //   return MathHelp.pseudoRandom(seed) < reflectivity ? 
  //     Float3.sub(in, Float3.mult(normal, 2*Float3.dot(normal, in))):
  //     MathHelp.randomHemisphereRay(normal, seed);
  // }

  // public static Vector3 displaceNormal(Vector3 normal, Vector3 displacement) {
  //   Vector3 pU = normal.y > 0.99 || normal.y < -0.99 ? new Vector3(-1, 0, 0) : Vector3.UNIT_Y.cross(normal).unitize();
  //   Vector3 pV = normal.cross(pU).unitize();
    
  //   return normal.subtract(pU.scale(displacement.x)).add(pV.scale(displacement.y)).subtract(normal.scale(displacement.z)).unitize();
  // }
}
