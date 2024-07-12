package mki.world;

import java.awt.image.BufferedImage;

import mki.math.MathHelp;
import mki.math.matrix.Quaternion;
import mki.math.vector.Vector2I;
import mki.math.vector.Vector3;
import mki.math.vector.Vector4;
import mki.rendering.Drawing;
import mki.rendering.renderers.Renderer;

/**
* A camera allows a player to see what's happening in the game.
*/
public class Camera3D {

  private Renderer renderer;

  private double fieldOfView;

  private Vector3 position;

  private Vector3 dir;
  private Vector3 rightDir;
  private Vector3 upDir;

  private double pitch;
  private double yaw;
  private double roll;

  private Vector4 q;
  
  private BufferedImage image;
  private Drawing imageContents;

  /**
  * @Camera
  *
  * Constructs a camera with a position vector, a default zoom level, and the current resolution of the game window.
  */
  public Camera3D(Vector3 position, int imageWidth, int imageHeight, double fieldOfView, Renderer renderer) {
    if (position == null) throw new IllegalArgumentException("Camera must have a position Vector!");
    if (renderer == null) throw new IllegalArgumentException("Camera must have a rendering method!");

    this.position = position;

    this.renderer = renderer;
    
    this.fieldOfView = Math.toRadians(fieldOfView);
    
    setImageDimensions(imageWidth, imageHeight);
    
    resetRotation();
    
    this.renderer.initialise(imageContents);
  }

  public void initialise() {
    setPosition(new Vector3());
    resetRotation();
    this.renderer.initialise(imageContents);
  }

  public void destroy() {
    this.renderer.destroy();
  }

  public double getFieldOfView() {return Math.toDegrees(fieldOfView);}

  public BufferedImage getImage() {return image;}

  public double getImageAspectRatio() {return imageContents.getAspectRatio();}

  public Vector2I getImageDimensions() {
    return new Vector2I(imageContents.getWidth(), imageContents.getHeight());
  }

  public Vector3 getPosition() {return position;}

  public Vector3 getDir() {return dir;}

  public Vector3 getUpDir() {return upDir;}

  public Vector3 getRightDir() {return rightDir;}

  public double getPitch() {
    return pitch;
  }

  public double getYaw() {
    return yaw;
  }

  public double getRoll() {
    return roll;
  }

  public Vector4 getRotation() {
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

  public synchronized void setRenderer(Renderer renderer) {
    this.renderer.destroy();
    this.renderer = renderer;
    this.renderer.updateConstants(this.fieldOfView, this.image.getWidth(), this.image.getHeight());
    this.renderer.initialise(imageContents);
  }

  public void resetRotation() {
    this.pitch = this.yaw = this.roll = 0;

    this.dir = new Vector3(0, 0, 1);
    this.rightDir = new Vector3(1, 0, 0);
    this.upDir = new Vector3(0, 1, 0);

    this.q = new Vector4(1, 0, 0, 0);
  }

  public void offsetPitch(double theta) {
    setPitch(this.pitch+theta);
  }

  public void setPitch(double theta) {
    this.pitch = fixPitch(theta);

    updateQ();
  }

  protected static double fixPitch(double theta) {
    return MathHelp.clamp(theta, -90, 90);
  }

  public void offsetYaw(double theta) {
    setYaw(this.yaw+theta);
  }

  public void setYaw(double theta) {
    this.yaw = fixYaw(theta);

    updateQ();
  }

  protected static double fixYaw(double theta) {
    return (360 + theta) % 360;
  }

  public void offsetRoll(double theta) {
    setRoll(this.roll+theta);
  }

  public void setRoll(double theta) {
    this.roll = fixRoll(theta);

    updateQ();
  }

  protected static double fixRoll(double theta) {
    return (360 + theta) % 360;
  }

  public void setRotation(double pitch, double yaw, double roll) {
    this.pitch = fixPitch(pitch);
    this.yaw   = fixYaw  (yaw  );
    this.roll  = fixRoll (roll );

    updateQ();
  }

  private void updateQ() {
    this.q = Quaternion.fromPitchYawRoll(Math.toRadians(this.pitch), Math.toRadians(this.yaw), Math.toRadians(this.roll));

    this.dir      = Quaternion.rotate(this.q, new Vector3(0, 0, 1));
    this.rightDir = Quaternion.rotate(this.q, new Vector3(1, 0, 0));
    this.upDir    = Quaternion.rotate(this.q, new Vector3(0, 1, 0));
  }

  public void draw(RigidBody[] bodies) {
    renderer.render(imageContents, position, q, bodies);
    imageContents.asBufferedImage(image);
    // synchronized (this) {
    //   imageContents.asBufferedImage(image);
    // }
  }

  public void draw() {
    draw(RigidBody.getActiveBodies());
  }
}
