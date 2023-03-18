package code.core;

import java.awt.image.BufferedImage;

import code.math.matrix.Matrix;
import code.math.vector.Vector3;
import code.rendering.Drawing;
import code.rendering.renderers.Renderer;
import code.world.RigidBody;

/**
* A camera allows a player to see what's happening in the game.
*/
public class Camera3D {

  private final Renderer renderer;

  private double fieldOfView = Math.toRadians(80);

  private Vector3 position;

  private Vector3 dir;
  private Vector3 rightDir;
  private Vector3 upDir;

  private double currentPitch;
  
  private BufferedImage image;
  private Drawing imageContents;

  /**
  * @Camera
  *
  * Constructs a camera with a position vector, a default zoom level, and the current resolution of the game window.
  */
  public Camera3D(Vector3 position, int imageWidth, int imageHeight, Renderer renderer) {
    this.position = position;

    setImageDimensions(imageWidth, imageHeight);

    this.renderer = renderer;

    renderer.updateConstants(this.fieldOfView);

    this.dir = new Vector3(0, 0, 1);
    this.rightDir = new Vector3(1, 0, 0);
    this.upDir = new Vector3(0, 1, 0);
    this.currentPitch = 0;
  }

  public double getFieldOfView() {return fieldOfView;}

  public BufferedImage getImage() {return image;}

  public double getImageAspectRatio() {return imageContents.getAspectRatio();}

  public Vector3 getPosition() {return position;}

  public Vector3 getDir() {return dir;}

  public Vector3 getUpDir() {return upDir;}

  public Vector3 getRightDir() {return rightDir;}

  public void setPos(Vector3 position) {this.position = position;}

  public void setFieldOfView(double fieldOfView) {this.fieldOfView = fieldOfView;}

  public void setImageDimensions(int imageWidth, int imageHeight) {
    this.image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB); 
    this.imageContents = new Drawing(imageWidth, imageHeight);
  }

  public void move(double x, double y, double z) {
    position = position.add(dir.scale(z)).add(rightDir.scale(x)).add(upDir.scale(y));
  }

  public void pitchCam(double ang) {
    if (currentPitch+ang > 85) ang = 85-currentPitch;
    if (currentPitch+ang < -85) ang = -85-currentPitch;
    currentPitch+=ang;
    ang = Math.toRadians(ang);
    Matrix pitchMatrix = Matrix.rotateXLocal(ang, dir);
    dir = pitchMatrix.multiply(dir);
    rightDir = pitchMatrix.multiply(rightDir);
    upDir = pitchMatrix.multiply(upDir);
  }

  public void yawCam(double ang) {
    ang = Math.toRadians(ang);
    Matrix yawMatrix = Matrix.rotateY(ang);
    dir = yawMatrix.multiply(dir);
    rightDir = yawMatrix.multiply(rightDir);
    upDir = yawMatrix.multiply(upDir);
  }

  public void draw(RigidBody[] bodies) {
    renderer.render(imageContents, position, dir, upDir, bodies);
    imageContents.asBufferedImage(image);
  }
}
