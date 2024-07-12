package mki.rendering.ray;

import mki.math.vector.Vector3;
import mki.world.Material;
import mki.world.RigidBody;

// import mki.rendering.gpumath.MathHelp;
// import mki.rendering.gpumath.Quaternion;
// import mki.world.Material;

public class GPU_FastSphere {

  private static final int NUM_REFLECTIONS = 2;

  public static Vector3 getColour(Vector3 start, Vector3 dir, RigidBody[] bodies) {
    Vector3 rayStart = start; 
    Vector3 rayDir = dir;
    double prevReflectivity = 1f;
    Vector3 totalLight = new Vector3();
    Vector3 surfaceColour = new Vector3(1);
    for (int ref = -1; ref < NUM_REFLECTIONS; ref++) {
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

      Vector3 emittedLight = mat.getEmissivity();
      int baseColour = mat.getBaseColour();
      // totalLight = Float3.add(totalLight, Float3.mult(emittedLight, surfaceColour));
      surfaceColour = surfaceColour.scale(((baseColour>>16)&255)/255.0, ((baseColour>>8)&255)/255.0, ((baseColour)&255)/255.0);
      float reflectivity = mat.getReflectivity();
      Vector3 light = calculateLight(surface, sNormal, rayDir.scale(-1), bodies, emittedLight, reflectivity, 20*Math.tan(1.56688f*reflectivity));

      // if (numRef>0 && mat.getReflectivity() != 0) {return mat.getReflection(getColour(surface, dir.subtract(sNormal.scale(2*sNormal.dot(dir))), bodies, numSteps-1, numRef-1), intensity, Puv.x, Puv.y);}
      totalLight = totalLight.add(light.scale(surfaceColour.scale(prevReflectivity)));
      if (reflectivity <= 0) break;
      prevReflectivity *= reflectivity * reflectivity;
      rayStart = surface;
      rayDir = rayDir.subtract(sNormal.scale(2*sNormal.dot(rayDir)));
    }

    return totalLight;
  }

  public static Vector3 calculateLight(Vector3 rayStart, Vector3 sNormal, Vector3 outDir, RigidBody[] bodies, Vector3 light, double reflectivity, double specularity) {
    for (int i = 0; i < bodies.length; i++) {
      if (bodies[i] == null) continue;
      
      if (!bodies[i].getModel().getMat().isEmissive()) continue; //if object doesn't give off light, no point looking for light

      Vector3 position = bodies[i].getPosition();
      Vector3 otherCI = bodies[i].getModel().getMat().getEmissivity(); //emitted light
      Vector3 dir = position.subtract(rayStart).normal(); //direction from this surface point to other object

      double distToSurface = reaches(rayStart, dir, bodies, i);
      if (distToSurface == -1) continue;
      Vector3 bodyToSrc = position.subtract(rayStart);
      double distToLightSquare = bodyToSrc.magsquare();
      light = light.add(
        otherCI.scale(Math.max(0, sNormal.dot(dir))/(12.566370614359172f * distToLightSquare)).scale( //BRDF
        1-reflectivity + //diffuse
        Math.pow(Math.max(0, sNormal.dot(outDir.add(dir).normal())), specularity) * reflectivity //specular
      ));
    }
    return light;
  }

  private static double reaches(Vector3 rayStart, Vector3 dir, RigidBody[] bodies, int destBody) {
    double closest = Float.MAX_VALUE;
    int close = -1;
    for (int i = 0; i < bodies.length; i++) {
      if (bodies[i] == null) continue;
      
      Vector3 position = bodies[i].getPosition();
      double  radius   = bodies[i].getModel().getRadius();

      Vector3 bodyToSrc = position.subtract(rayStart);
      double distSquare = bodyToSrc.magsquare(); //distance squared between ray and sphere centre.
      double DcosA      = bodyToSrc.dot(dir);       //'adjacent' side of the triangle.
      double distToColl = DcosA-Math.sqrt(DcosA*DcosA+radius*radius-distSquare);
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
