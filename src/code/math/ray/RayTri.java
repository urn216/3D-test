package code.math.ray;

import mki.math.MathHelp;
import mki.math.vector.Vector2;
import mki.math.vector.Vector3;

import code.math.tri.Tri3D;
import code.rendering.renderers.Renderer;
import code.world.Material;
import code.world.RigidBody;

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
    Tri3D cTri = null;
    double cU = 0;
    double cV = 0;
    for (RigidBody body : bodies) {
      double rad = body.getModel().getRadius();
      double distSquare = rayStart.subtract(body.getPosition()).magsquare();
      double DcosA = dir.dot(body.getPosition().subtract(rayStart));
      if (Double.isNaN(Math.sqrt(DcosA*DcosA+rad*rad-distSquare))) {continue;}
      for (Tri3D tri : body.getModel().getFaces()) {
        Vector3[] edges = tri.getEdges();
        Vector3 n = edges[2];
        double det = -n.dot(dir);
        if (det < 0.000001) {continue;}
        Vector3[] verts = tri.getVerts();
        Vector3 toTri = rayStart.subtract(verts[0].add(body.getPosition()));
        double distToColl = toTri.dot(n)/det;
        if (distToColl < -0.000001 || distToColl > closest) {continue;}
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
    Vector3 surface = rayStart.add(dir.scale(closest));
    Vector2 Puv = cTri.getUVCoords(cU, cV); //get the uv coordinates for this point
    // Vector3 sNormal = close instanceof Sphere ? surface.subtract(close.getPosition()).unitize() : cTri.getNormal();
    Vector3 sNormal = Renderer.isNormalMap() ? cTri.getDisplacedNormal(close.getModel().getMat().getNormal(Puv.x, Puv.y)) : cTri.getNormal();
    // Vector3 sNormal = cTri.getNormal();
    Vector3 intensity = intensityStep(surface, sNormal, bodies, close, close.getModel().getMat().getIntensity(), numSteps);
    if (numRef>0 && close.getModel().getMat().getReflectivity() != 0) {return close.getModel().getMat().getReflection(getCol(surface, dir.subtract(sNormal.scale(2*sNormal.dot(dir))), bodies, numSteps-1, numRef-1), intensity, Puv.x, Puv.y);}
    return close.getModel().getMat().getIntenseColour(intensity, Puv.x, Puv.y);
    // return close.getMat().getAbsColour();
  }

  public static double reaches(Vector3 rayStart, Vector3 dir, RigidBody[] bodies, RigidBody goal, RigidBody sourceBody) {
    double closest = Double.POSITIVE_INFINITY;
    RigidBody close = null;
    for (RigidBody body : bodies) {
      // double rad = body == sourceBody ? 0 : body.getRad();
      double rad = body.getModel().getRadius();
      double distSquare = rayStart.subtract(body.getPosition()).magsquare();
      double DcosA = dir.dot(body.getPosition().subtract(rayStart));
      if (Double.isNaN(Math.sqrt(DcosA*DcosA+rad*rad-distSquare))) {continue;}
      for (Tri3D tri : body.getModel().getFaces()) {
        Vector3[] edges = tri.getEdges();
        Vector3 n = edges[2];
        double det = -n.dot(dir);
        if (det < 0.000001) {continue;}
        Vector3[] verts = tri.getVerts();
        Vector3 toTri = rayStart.subtract(verts[0].add(body.getPosition()));
        double distToColl = toTri.dot(n)/det;
        if (distToColl < -0.000001 || distToColl > closest) {continue;}
        Vector3 DToTri = toTri.cross(dir);
        double u = edges[1].dot(DToTri)/det;
        double v = -edges[0].dot(DToTri)/det;
        if (u>=0 && v>=0 && u+v<=1) {closest = distToColl; close = body;}
      }
    }
    return (close == null || close != goal) ? Double.NaN : closest;
  }

  public static Vector3 intensityStep(Vector3 rayStart, Vector3 sNormal, RigidBody[] bodies, RigidBody sourceBody, Vector3 intensity, int numSteps) {
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
          intensityStep(rayStart.add(dir.scale(distToSurface)), dir.scale(-1), bodies, body2, new Vector3(), numSteps-1)
        ));
      }
      intensity = intensity.add(otherCI.scale(MathHelp.intensity(Math.max(sNormal.dot(dir), 0), distToLightSquare)));
      intensity = intensity.add(otherSI.scale(MathHelp.intensity(1, distToSurface*distToSurface)));
    }
    return intensity;
  }
}
