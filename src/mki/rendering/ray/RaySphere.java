package mki.rendering.ray;

import mki.math.MathHelp;

import mki.math.vector.Vector2;
import mki.math.vector.Vector3;
import mki.rendering.Constants;
import mki.world.Material;
import mki.world.RigidBody;

public class RaySphere {

  /**
  * Shoots out a ray from a position towards a specified direction and retrieves the ARGB colour information at the point the ray collides.
  * This ray looks for spheres.
  *
  * @param rayStart The origin of the ray.
  * @param dir The unit vector towards which the ray will go.
  * @param bodies The array containing all the objects with which to look for.
  * @param numSteps The number of recursive iterations of raytracing to gather cascading lights.
  * @param numRef The number of relection layers visible on a surface.
  *
  * @return An integer containing ARGB data, depicting the colour of a pixel
  */
  public static int getColour(Vector3 rayStart, Vector3 dir, RigidBody[] bodies, int numSteps, int numRef) {
    double closest = Double.POSITIVE_INFINITY;
    RigidBody close = null;
    for (int i = 0; i < bodies.length; i++) {
      RigidBody body = bodies[i];
      if (body == null) continue;

      double rad = body.getModel().getRadius();
      double distSquare = rayStart.subtract(body.getPosition()).magsquare(); //distance squared between ray and sphere centre.
      double DcosA = dir.dot(body.getPosition().subtract(rayStart)); //'adjacent' side of the triangle.
      double distToColl = DcosA-Math.sqrt(DcosA*DcosA+rad*rad-distSquare);
      if (distToColl < -0.000001) {continue;}
      if (distToColl < closest) {closest = distToColl; close = body;}
    }
    if (close == null) { // We did not collide with an object, so we see skybox.
      Vector2 Puv = MathHelp.sphereUVPointInv(dir); //get the uv coordinates for this point in skybox
      return Material.getSkyColour(Puv.x, Puv.y);
    }
    // We did collide with an object, so let's look at it.
    Vector3 surface = rayStart.add(dir.scale(closest));
    Vector3 sNormal = surface.subtract(close.getPosition()).unitize();
    Vector2 Puv = MathHelp.sphereUVPoint(close.getRotation().reverse().rotate(sNormal)); //get the uv coordinates for this point
    Material mat = close.getModel().getMat();

    sNormal = Constants.getSphereNormal().apply(sNormal, mat, Puv.x, Puv.y);

    Vector3 intensity = intensityStep(surface, sNormal, bodies, close, mat.getIntensity(), numSteps);
    if (numRef>0 && mat.getReflectivity() != 0) {return mat.getReflection(getColour(surface, dir.subtract(sNormal.scale(2*sNormal.dot(dir))), bodies, numSteps-1, numRef-1), intensity, Puv.x, Puv.y);}
    return mat.getIntenseColour(intensity, Puv.x, Puv.y);
  }

  private static double reaches(Vector3 rayStart, Vector3 dir, RigidBody[] bodies, RigidBody destBody, RigidBody sourceBody) {
    double closest = Double.POSITIVE_INFINITY;
    RigidBody close = null;
    for (int i = 0; i < bodies.length; i++) {
      RigidBody body = bodies[i];
      if (body == null) continue;

      double rad = body.getModel().getRadius();
      double distSquare = rayStart.subtract(body.getPosition()).magsquare();
      double DcosA = dir.dot(body.getPosition().subtract(rayStart));
      double distToColl = DcosA-Math.sqrt(DcosA*DcosA+rad*rad-distSquare);
      if (distToColl < -0.000001) {continue;}
      if (distToColl < closest) {closest = distToColl; close = body;}
    }
    return (close == null || close != destBody) ? Double.NaN : closest;
  }

  private static Vector3 intensityStep(Vector3 rayStart, Vector3 sNormal, RigidBody[] bodies, RigidBody sourceBody, Vector3 intensity, int numSteps) {
    for (int i = 0; i < bodies.length; i++) {
      RigidBody destBody = bodies[i];
      if (destBody == sourceBody || destBody == null) continue; //don't want to endlessly add own light to self.

      Vector3 otherCI = new Vector3(); //core intensity (emitted)
      Vector3 otherSI = new Vector3(); //surface intensity (reflected)
      Vector3 dir = destBody.getPosition().subtract(rayStart).unitize(); //direction from this surface point to other object
      double distToSurface = reaches(rayStart, dir, bodies, destBody, sourceBody);
      if (Double.isNaN(distToSurface)) continue;
      double distToLightSquare = rayStart.subtract(destBody.getPosition()).magsquare();
      otherCI = destBody.getModel().getMat().getIntensity();
      if (numSteps > 0) {
        otherSI = otherSI.add(destBody.getModel().getMat().getAdjIntensity(
        intensityStep(rayStart.add(dir.scale(Math.sqrt(distToLightSquare)-destBody.getModel().getRadius())), dir.scale(-1), bodies, destBody, new Vector3(), numSteps-1)
        ));
      }
      intensity = intensity.add(otherCI.scale(MathHelp.intensity(Math.abs(sNormal.dot(dir)), distToLightSquare)));
      intensity = intensity.add(otherSI.scale(MathHelp.intensity(1, distToSurface*distToSurface)));
    }
    return intensity;
  }



  public static Vector3 displaceNormal(Vector3 normal, Vector3 displacement) {
    Vector3 pU = normal.y > 0.99 || normal.y < -0.99 ? new Vector3(-1, 0, 0) : Vector3.UNIT_Y.cross(normal).unitize();
    Vector3 pV = normal.cross(pU).unitize();
    
    return normal.subtract(pU.scale(displacement.x)).add(pV.scale(displacement.y)).subtract(normal.scale(displacement.z)).unitize();
  }
}
