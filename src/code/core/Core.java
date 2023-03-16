package code.core;

import java.awt.image.BufferedImage;

import java.awt.event.KeyEvent;

import code.math.vector.Vector3;

import code.rendering.Renderer;

import code.world.RigidBody;
import code.world.models.Sphere;

import java.awt.Graphics;

public abstract class Core {
  
  public static final Window WINDOW;
  
  public static final Settings GLOBAL_SETTINGS;
  
  private static final double TICKS_PER_SECOND = 60;
  private static final double MILLISECONDS_PER_TICK = 1000/TICKS_PER_SECOND;
  
  private static boolean update = true;
  private static boolean first = true;
  
  private static boolean quit = false;
  
  private static RigidBody[] bodies;
  private static Sphere lightSource;
  
  private static Camera3D cam;
  
  private static BufferedImage image;
  private static int[] imageContents;
  private static double imageAspectRatio;

  private static long previousTick    = System.currentTimeMillis();
  private static long deltaTimeMillis = 0;
  
  private static long pFTime = System.currentTimeMillis();
  private static double fps = 0;
  private static int fCount = 0;
  
  static {
    WINDOW = new Window();

    GLOBAL_SETTINGS = new Settings();

    image = new BufferedImage(
      GLOBAL_SETTINGS.getIntSetting("resolution_X"), 
      GLOBAL_SETTINGS.getIntSetting("resolution_Y"), 
      BufferedImage.TYPE_INT_ARGB
    );
    imageContents = new int[image.getWidth() * image.getHeight()];
    imageAspectRatio = 1.0 * image.getWidth() / image.getHeight();
  }
  
  /**
  * Main method. Called on execution. Performs basic startup
  *
  * @param args Ignored for now
  */
  public static void main(String[] args) {
    bodies = Scene.s1();
    lightSource = (Sphere)bodies[0];

    cam = new Camera3D(new Vector3(), Renderer.raySphere());

    Controls.initialiseControls(WINDOW.FRAME);

    playGame();
  }
  
  /**
  * @return the currently active camera
  */
  public static Camera3D getActiveCam() {
    return cam;
  }
  
  /**
  * Sets a flag to close the program at the nearest convenience
  */
  public static void quitToDesk() {
    quit = true;
  }
  
  public static void playGame() {
    while (true) {
      long tickTime = System.currentTimeMillis();
      deltaTimeMillis = tickTime - previousTick;
      previousTick  = tickTime;
      
      if (Controls.KEY_DOWN[KeyEvent.VK_W])     {cam.move        (0, 0,  0.01*deltaTimeMillis);}
      if (Controls.KEY_DOWN[KeyEvent.VK_S])     {cam.move        (0, 0, -0.01*deltaTimeMillis);}
      if (Controls.KEY_DOWN[KeyEvent.VK_A])     {cam.move        (-0.01*deltaTimeMillis, 0, 0);}
      if (Controls.KEY_DOWN[KeyEvent.VK_D])     {cam.move        ( 0.01*deltaTimeMillis, 0, 0);}
      if (Controls.KEY_DOWN[KeyEvent.VK_SHIFT]) {cam.move        (0, -0.005*deltaTimeMillis, 0);}
      if (Controls.KEY_DOWN[KeyEvent.VK_SPACE]) {cam.move        (0,  0.005*deltaTimeMillis, 0);}
      if (Controls.KEY_DOWN[KeyEvent.VK_I])     {lightSource.move(0, 0,  0.01*deltaTimeMillis);}
      if (Controls.KEY_DOWN[KeyEvent.VK_K])     {lightSource.move(0, 0, -0.01*deltaTimeMillis);}
      if (Controls.KEY_DOWN[KeyEvent.VK_J])     {lightSource.move(-0.01*deltaTimeMillis, 0, 0);}
      if (Controls.KEY_DOWN[KeyEvent.VK_L])     {lightSource.move( 0.01*deltaTimeMillis, 0, 0);}
      if (Controls.KEY_DOWN[KeyEvent.VK_O])     {lightSource.move(0, -0.01*deltaTimeMillis, 0);}
      if (Controls.KEY_DOWN[KeyEvent.VK_U])     {lightSource.move(0,  0.01*deltaTimeMillis, 0);}
      if (Controls.KEY_DOWN[KeyEvent.VK_UP])    {cam.pitchCam(-0.1*deltaTimeMillis);}
      if (Controls.KEY_DOWN[KeyEvent.VK_DOWN])  {cam.pitchCam( 0.1*deltaTimeMillis);}
      if (Controls.KEY_DOWN[KeyEvent.VK_LEFT])  {cam.yawCam  (-0.1*deltaTimeMillis);}
      if (Controls.KEY_DOWN[KeyEvent.VK_RIGHT]) {cam.yawCam  ( 0.1*deltaTimeMillis);}
      
      if (update || first) {
        image.setRGB(
          0, 0, image.getWidth(), image.getHeight(), 
          cam.draw(imageContents, image.getWidth(), image.getHeight(), bodies), 
          0, image.getWidth()
        );
        first = false;
      }
      
      if (quit) {
        System.exit(0);
      }
      WINDOW.PANEL.repaint();
      tickTime = System.currentTimeMillis() - tickTime;
      try {
        Thread.sleep(Math.max((long)(MILLISECONDS_PER_TICK - tickTime), 0));
      } catch(InterruptedException e){System.out.println(e); System.exit(0);}
    }
  }
  
  /**
  * Paints the contents of the program to the given {@code Graphics} object.
  * 
  * @param gra the supplied {@code Graphics} object
  */
  public static void paintComponent(Graphics gra) {
    
    int size = Math.min((int)(WINDOW.screenWidth()/imageAspectRatio), WINDOW.screenHeight());
    gra.drawImage(image.getScaledInstance((int)(size*imageAspectRatio), size, BufferedImage.SCALE_DEFAULT), 0, 0, null);
    
    if (fCount >= 100) {
      long cFTime = System.currentTimeMillis();
      fps = fCount*1000.0/(cFTime-pFTime);
      System.out.println(fps);
      pFTime = cFTime;
      fCount=0;
    }
    fCount++;
  }
}
