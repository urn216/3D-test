package code.world;

import java.awt.image.BufferedImage;

import mki.math.MathHelp;
import mki.math.matrix.Quaternion;
import mki.math.vector.Vector3;

import code.rendering.Drawing;
import code.rendering.renderers.Renderer;

/**
* A camera allows a player to see what's happening in the game.
*/
public class Camera3D {

  private Renderer renderer;

  private double fieldOfView = Math.toRadians(80);

  private Vector3 position;

  private Vector3 dir;
  private Vector3 rightDir;
  private Vector3 upDir;

  private double pitch;
  private double yaw;
  private double roll;

  private Quaternion q;
  
  private BufferedImage image;
  private Drawing imageContents;

  /**
  * @Camera
  *
  * Constructs a camera with a position vector, a default zoom level, and the current resolution of the game window.
  */
  public Camera3D(Vector3 position, int imageWidth, int imageHeight, Renderer renderer) {
    if (position == null) throw new IllegalArgumentException("Camera must have a position Vector!");
    if (renderer == null) throw new IllegalArgumentException("Camera must have a rendering method!");

    this.position = position;

    this.renderer = renderer;

    setImageDimensions(imageWidth, imageHeight);

    this.dir = new Vector3(0, 0, 1);
    this.rightDir = new Vector3(1, 0, 0);
    this.upDir = new Vector3(0, 1, 0);

    this.q = Quaternion.fromAxisAngle(0, new Vector3());
  }

  public double getFieldOfView() {return Math.toDegrees(fieldOfView);}

  public synchronized BufferedImage getImage() {return image;}

  public double getImageAspectRatio() {return imageContents.getAspectRatio();}

  public Vector3 getPosition() {return position;}

  public Vector3 getDir() {return dir;}

  public Vector3 getUpDir() {return upDir;}

  public Vector3 getRightDir() {return rightDir;}

  public Quaternion getRotationQ() {
    return q;
  }

  public void offsetPositionGlobal(double x, double y, double z) {
    position = position.add(x, y, z);
  }
  
  public void offsetPositionLocal(double x, double y, double z) {
    position = position.add(dir.scale(z)).add(rightDir.scale(x)).add(upDir.scale(y));
  }

  public void setPosition(Vector3 position) {this.position = position;}

  public void setFieldOfView(double fieldOfView) {
    this.fieldOfView = Math.toRadians(fieldOfView);
    this.renderer.updateConstants(this.fieldOfView, this.image.getWidth(), this.image.getHeight());
  }

  public void setImageDimensions(int imageWidth, int imageHeight) {
    this.image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB); 
    this.imageContents = new Drawing(imageWidth, imageHeight);
    this.renderer.updateConstants(this.fieldOfView, imageWidth, imageHeight);
  }

  public void setRenderer(Renderer renderer) {
    this.renderer = renderer;
    this.renderer.updateConstants(this.fieldOfView, this.image.getWidth(), this.image.getHeight());
  }

  public void offsetPitch(double theta) {
    setPitch(this.pitch+theta);
  }

  public void setPitch(double theta) {
    this.pitch = MathHelp.clamp(theta, -85, 85);

    updateQ();
  }

  public void offsetYaw(double theta) {
    setYaw(this.yaw+theta);
  }

  public void setYaw(double theta) {
    this.yaw = (360 + theta) % 360;

    updateQ();
  }

  public void offsetRoll(double theta) {
    setRoll(this.roll+theta);
  }

  public void setRoll(double theta) {
    this.roll = theta;

    updateQ();
  }

  private void updateQ() {
    this.q = Quaternion.fromPitchYawRoll(Math.toRadians(this.pitch), Math.toRadians(this.yaw), Math.toRadians(this.roll));

    this.dir = this.q.rotate(new Vector3(0, 0, 1));
    this.rightDir = this.q.rotate(new Vector3(1, 0, 0));
    this.upDir = this.q.rotate(new Vector3(0, 1, 0));
  }

  public void draw(RigidBody[] bodies) {
    renderer.render(imageContents, position, q, bodies);
    synchronized (this) {
      imageContents.asBufferedImage(image);
    }
  }
}
