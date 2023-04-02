package code.math.ray;
import code.math.MathHelp;
import code.math.vector.Vector2;
import code.math.vector.Vector3;
import code.world.Material;
import code.world.RigidBody;

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
  public static int getCol(Vector3 rayStart, Vector3 dir, RigidBody[] bodies, int numSteps, int numRef) {
    double closest = Double.POSITIVE_INFINITY;
    RigidBody close = null;
    for (RigidBody body : bodies) {
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
    Vector3 intensity = intensityStep(surface, sNormal, bodies, close, true, numSteps);
    Vector2 Puv = MathHelp.sphereUVPoint(sNormal); //get the uv coordinates for this point
    if (numRef>0 && close.getModel().getMat().getReflectivity() != 0) {return close.getModel().getMat().getReflection(getCol(surface, dir.subtract(sNormal.scale(2*sNormal.dot(dir))), bodies, numSteps-1, numRef-1), intensity, Puv.x, Puv.y);}
    return close.getModel().getMat().getIntenseColour(intensity, Puv.x, Puv.y);
  }

  private static double reaches(Vector3 rayStart, Vector3 dir, RigidBody[] bodies, RigidBody goal, RigidBody sourceBody) {
    double closest = Double.POSITIVE_INFINITY;
    RigidBody close = null;
    for (RigidBody body : bodies) {
      // if (sourceBody == body) {continue;}
      double rad = body.getModel().getRadius();
      double distSquare = rayStart.subtract(body.getPosition()).magsquare();
      double DcosA = dir.dot(body.getPosition().subtract(rayStart));
      double distToColl = DcosA-Math.sqrt(DcosA*DcosA+rad*rad-distSquare);
      if (distToColl < -0.000001) {continue;}
      if (distToColl < closest) {closest = distToColl; close = body;}
    }
    if (close == null || close != goal) {return Double.NaN;}
    return closest;
  }

  private static Vector3 intensityStep(Vector3 rayStart, Vector3 sNormal, RigidBody[] bodies, RigidBody sourceBody, boolean first, int numSteps) {
    Vector3 intensity = new Vector3();
    if (first) {intensity = intensity.add(sourceBody.getModel().getMat().getIntensity());} //add its own brightness only if first step. Stops endless light buildup.
    for (RigidBody body2 : bodies) {
      if (body2 == sourceBody) {continue;} //don't want to endlessly add own light to self.
      Vector3 otherCI = new Vector3(); //core intensity (emitted)
      Vector3 otherSI = new Vector3(); //surface intensity (reflected)
      Vector3 dir = body2.getPosition().subtract(rayStart).unitize(); //direction from this surface point to other object
      double distToSurface = reaches(rayStart, dir, bodies, body2, sourceBody);
      if (Double.isNaN(distToSurface)) {continue;}
      double distToLightSquare = rayStart.subtract(body2.getPosition()).magsquare();
      otherCI = body2.getModel().getMat().getIntensity();
      if (numSteps > 0) {
        otherSI = otherSI.add(body2.getModel().getMat().getAdjIntensity(
        intensityStep(rayStart.add(dir.scale(Math.sqrt(distToLightSquare)-body2.getModel().getRadius())), dir.scale(-1), bodies, body2, false, numSteps-1)
        ));
      }
      intensity = intensity.add(otherCI.scale(MathHelp.intensity(Math.abs(sNormal.dot(dir)), distToLightSquare)));
      intensity = intensity.add(otherSI.scale(MathHelp.intensity(1, distToSurface*distToSurface)));
    }
    return intensity;
  }
}
