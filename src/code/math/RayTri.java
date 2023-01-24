package code.math;
import code.world.Material;
import code.world.RigidBody;
import code.world.models.Tri;

public class RayTri {

  /**
  * Shoots out a ray from a position towards a specified direction and retrieves the ARGB colour information at the point the ray collides.
  * This ray looks for triangles.
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
    Tri cTri = null;
    double cU = 0;
    double cV = 0;
    for (RigidBody body : bodies) {
      double rad = body.getRad();
      double distSquare = rayStart.subtract(body.getPos()).magsquare();
      double DcosA = dir.dot(body.getPos().subtract(rayStart));
      if (Double.isNaN(Math.sqrt(DcosA*DcosA+rad*rad-distSquare))) {continue;}
      for (Tri tri : body.getTris()) {
        Vector3 n = tri.getNorm();
        double det = -n.dot(dir);
        if (det < 0.000001) {continue;}
        Vector3[] verts = tri.getVerts();
        Vector3 toTri = rayStart.subtract(verts[0].add(body.getPos()));
        double distToColl = toTri.dot(n)/det;
        if (distToColl < -0.000001 || distToColl > closest) {continue;}
        Vector3[] edges = tri.getEdges();
        Vector3 DToTri = toTri.cross(dir);
        double u = edges[1].dot(DToTri)/det;
        double v = -edges[0].dot(DToTri)/det;
        if (u>=0 && v>=0 && u+v<=1) {closest = distToColl; close = body; cTri = tri; cU = u; cV = v;}
      }
    }
    if (close == null) { // We did not collide with an object, so we see skybox.
      Vector2 Puv = MathHelp.sphereUVPointInv(dir); //get the uv coordinates for this point in skybox
      return Material.getSkyColour(Puv.x, Puv.y);
    }
    Vector3 surface = rayStart.add(dir.multiply(closest));
    Vector3 sNormal = cTri.getEdges()[2];
    Vector3 intensity = intensityStep(surface, sNormal, bodies, close, true, numSteps);
    Vector2 Puv = cTri.getUVCoords(cU, cV); //get the uv coordinates for this point
    if (numRef>0 && close.getMat().getReflectivity() != 0) {return close.getMat().getReflection(getCol(surface, dir.subtract(sNormal.multiply(2*sNormal.dot(dir))), bodies, numSteps-1, numRef-1), intensity, Puv.x, Puv.y);}
    return close.getMat().getIntenseColour(intensity, Puv.x, Puv.y);
    // return close.getMat().getAbsColour();
  }

  public static double reaches(Vector3 rayStart, Vector3 dir, RigidBody[] bodies, RigidBody goal, RigidBody sourceBody) {
    double closest = Double.POSITIVE_INFINITY;
    RigidBody close = null;
    for (RigidBody body : bodies) {
      // double rad = body == sourceBody ? 0 : body.getRad();
      double rad = body.getRad();
      double distSquare = rayStart.subtract(body.getPos()).magsquare();
      double DcosA = dir.dot(body.getPos().subtract(rayStart));
      if (Double.isNaN(Math.sqrt(DcosA*DcosA+rad*rad-distSquare))) {continue;}
      for (Tri tri : body.getTris()) {
        Vector3 n = tri.getNorm();
        double det = -n.dot(dir);
        if (det < 0.000001) {continue;}
        Vector3[] verts = tri.getVerts();
        Vector3 toTri = rayStart.subtract(verts[0].add(body.getPos()));
        double distToColl = toTri.dot(n)/det;
        if (distToColl < -0.000001 || distToColl > closest) {continue;}
        Vector3[] edges = tri.getEdges();
        Vector3 DToTri = toTri.cross(dir);
        double u = edges[1].dot(DToTri)/det;
        double v = -edges[0].dot(DToTri)/det;
        if (u>=0 && v>=0 && u+v<=1) {closest = distToColl; close = body;}
      }
    }
    if (close == null || close != goal) {return Double.NaN;}
    return closest;
  }

  public static Vector3 intensityStep(Vector3 rayStart, Vector3 sNormal, RigidBody[] bodies, RigidBody sourceBody, boolean first, int numSteps) {
    Vector3 intensity = new Vector3();
    if (first) {intensity = intensity.add(sourceBody.getMat().getIntensity());} //add its own brightness only if first step. Stops endless light buildup.
    for (RigidBody body2 : bodies) {
      if (body2 == sourceBody) {continue;} //don't want to endlessly add own light to self.
      Vector3 otherCI = new Vector3(); //core intensity (emitted)
      Vector3 otherSI = new Vector3(); //surface intensity (reflected)
      Vector3 dir = body2.getPos().subtract(rayStart).unitize(); //direction from this surface point to other object
      double distToSurface = reaches(rayStart, dir, bodies, body2, sourceBody);
      if (Double.isNaN(distToSurface)) {continue;}
      double distToLightSquare = rayStart.subtract(body2.getPos()).magsquare();
      otherCI = body2.getMat().getIntensity();
      if (numSteps > 0) {
        otherSI = otherSI.add(body2.getMat().getAdjIntensity(
        intensityStep(rayStart.add(dir.multiply(distToSurface)), dir.multiply(-1), bodies, body2, false, numSteps-1)
        ));
      }
      intensity = intensity.add(otherCI.multiply(MathHelp.intensity(Math.abs(sNormal.dot(dir)), distToLightSquare)));
      intensity = intensity.add(otherSI.multiply(MathHelp.intensity(1, distToSurface*distToSurface)));
    }
    return intensity;
  }
}
