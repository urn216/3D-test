package mki.rendering.ray;

import mki.math.vector.Vector3;
import mki.rendering.gpumath.MathHelp;
import mki.world.Material;
import mki.world.RigidBody;

// import mki.rendering.gpumath.MathHelp;
// import mki.rendering.gpumath.Quaternion;
// import mki.world.Material;

public class GPU_RaySphere {

  private static final int NUM_REFLECTIONS = 2;
  // private static final float SPECULARITY = 100;

  public static Vector3 getColour(Vector3 rayStart, Vector3 rayDir, RigidBody[] bodies) {
    Vector3 totalLight = new Vector3();
    Vector3 surfaceColour = new Vector3(1);
    for (int ref = -2; ref < NUM_REFLECTIONS; ref++) {
      int close = -1;
      double closest = Double.MAX_VALUE;
      for (int i = 0; i < bodies.length; i++) {
        if (bodies[i] == null) continue;
        
        Vector3 position = bodies[i].getPosition();
        double  radius   = bodies[i].getModel().getRadius();

        Vector3 bodyToCam = position.subtract(rayStart);
        double distSquare = bodyToCam.magsquare(); //distance squared between ray and sphere centre.
        double DcosA      = bodyToCam.dot(rayDir);       //'adjacent' side of the triangle.
        double distToColl = DcosA-Math.sqrt(DcosA*DcosA+radius*radius-distSquare);
        if (distToColl < 0f) {continue;}
        if (distToColl < closest) {closest = distToColl; close = i;}
      }
      if (close == -1) { // We did not collide with an object, so we see skybox.
        //get the uv coordinates for this point in skybox
        // return Material.getSkyColour(0.5 + Math.atan2(dir.getX(), dir.getZ()) / 6.283185307179586, 0.5 - TornadoMath.asin(dir.getY()) / Math.PI);
        break;
      }
      // We did collide with an object, so let's look at it.
      Vector3 surface = rayStart.add(rayDir.scale(closest));
      Vector3 sNormal = surface.subtract(bodies[close].getPosition()).normal();
      // Float2 Puv = MathHelp.sphereUVPoint(Quaternion.rotate(Quaternion.reverse(bodies.get(close+1)), sNormal)); //get the uv coordinates for this point
      Material mat = bodies[close].getModel().getMat();

      // sNormal = Constants.getSphereNormal().apply(sNormal, mat, Puv.x, Puv.y);

      int baseColour = mat.getBaseColour();
      Vector3 emittedLight = mat.isEmissive() ? new Vector3(((baseColour>>16)&255), ((baseColour>>8)&255), ((baseColour)&255)) : new Vector3();

      totalLight = totalLight.add(emittedLight.scale(surfaceColour));
      surfaceColour = surfaceColour.scale(((baseColour>>16)&255)/255.0, ((baseColour>>8)&255)/255.0, ((baseColour)&255)/255.0);

      rayStart = surface;
      rayDir = reflect(rayDir, sNormal, mat.getReflectivity());
      // rayDir = Float3.sub(rayDir, Float3.mult(sNormal, 2*Float3.dot(sNormal, rayDir)));
      // rayDir = MathHelp.randomHemisphereRay(sNormal, seed);
    }

    return totalLight;
  }

  // Reflect ray towards the weighted-midpoint between a diffuse reflection and a specular reflection
  private static Vector3 reflect(Vector3 in, Vector3 normal, float reflectivity) {
    return 
      in.subtract(normal.scale(2*normal.dot(in))).scale(reflectivity).add(
      MathHelp.randomHemisphereRay(normal).scale(1-reflectivity))
    .normal();
  }

  // Reflect ray with random chance of diffuse reflection or specular reflection based on reflectivity.
  // private static Vector3 reflect(Vector3 in, Vector3 normal, float reflectivity) {
  //   return Math.random() < reflectivity ? 
  //     in.subtract(normal.scale(2*normal.dot(in))):
  //     MathHelp.randomHemisphereRay(normal);
  // }

  // public static Vector3 displaceNormal(Vector3 normal, Vector3 displacement) {
  //   Vector3 pU = normal.y > 0.99 || normal.y < -0.99 ? new Vector3(-1, 0, 0) : Vector3.UNIT_Y.cross(normal).unitize();
  //   Vector3 pV = normal.cross(pU).unitize();
    
  //   return normal.subtract(pU.scale(displacement.x)).add(pV.scale(displacement.y)).subtract(normal.scale(displacement.z)).unitize();
  // }
}
