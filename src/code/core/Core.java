package code.core;

import java.awt.event.KeyEvent;

import java.awt.image.BufferedImage;

import mki.math.vector.Vector3;

import mki.ui.control.UIController;

import code.rendering.renderers.Renderer;
import code.world.Camera3D;
import code.world.RigidBody;

import java.awt.Graphics;
import java.awt.Graphics2D;

public abstract class Core {
  
  public static final Window WINDOW;
  
  public static final Settings GLOBAL_SETTINGS;
  
  private static final double TICKS_PER_SECOND = 60;
  private static final double MILLISECONDS_PER_TICK = 1000/TICKS_PER_SECOND;
  
  private static boolean quit = false;
  
  private static RigidBody[] bodies;
  private static RigidBody lightSource;
  
  private static Camera3D cam;

  private static double defaultMovementSpeed = 0.01;
  private static double fasterMovementSpeed  = 0.1;

  private static long pTTime = System.currentTimeMillis();
  private static long pFTime = System.currentTimeMillis();
  
  private static double fps = 0;
  private static int fCount = 0;
  
  static {
    WINDOW = new Window("3D Test", (x, y) -> {});

    UIController.putPane("Main Menu", UICreator.createMain());
    UIController.setCurrentPane("Main Menu");
    
    GLOBAL_SETTINGS = new Settings();
    
    bodies = Scene.s5();
    lightSource = bodies[0];

    cam = new Camera3D(
      new Vector3(), 
      GLOBAL_SETTINGS.getIntSetting("resolution_X"), 
      GLOBAL_SETTINGS.getIntSetting("resolution_Y"), 
      Renderer.raySphere()
    );

    Controls.initialiseControls(WINDOW.FRAME);
  }
  
  /**
  * Main method. Called on execution. Performs basic startup
  *
  * @param args Ignored for now
  */
  public static void main(String[] args) {
    playGame();
  }
  
  /**
  * @return the currently active camera
  */
  public static Camera3D getActiveCam() {
    return cam;
  }

  public static double getFps() {
    return fps;
  }

  public static void setRenderer(Renderer r) {
    cam.setRenderer(r);
  }
  
  /**
  * Sets a flag to close the program at the nearest convenience
  */
  public static void quitToDesk() {
    if (quit) System.exit(1);
    
    quit = true;
  }
  
  public static void playGame() {
    while (true) {
      long tickTime = System.currentTimeMillis();
      long deltaTimeMillis = tickTime - pTTime;
      pTTime  = tickTime;
      
      double vel = Controls.KEY_DOWN[KeyEvent.VK_CONTROL] ? fasterMovementSpeed : defaultMovementSpeed;
      if (Controls.KEY_DOWN[KeyEvent.VK_W])     {cam.offsetPositionLocal(0, 0,  vel*deltaTimeMillis    );}
      if (Controls.KEY_DOWN[KeyEvent.VK_S])     {cam.offsetPositionLocal(0, 0, -vel*deltaTimeMillis    );}
      if (Controls.KEY_DOWN[KeyEvent.VK_A])     {cam.offsetPositionLocal(-vel*deltaTimeMillis, 0, 0    );}
      if (Controls.KEY_DOWN[KeyEvent.VK_D])     {cam.offsetPositionLocal( vel*deltaTimeMillis, 0, 0    );}
      if (Controls.KEY_DOWN[KeyEvent.VK_SHIFT]) {cam.offsetPositionLocal(0, -0.5*vel*deltaTimeMillis, 0);}
      if (Controls.KEY_DOWN[KeyEvent.VK_SPACE]) {cam.offsetPositionLocal(0,  0.5*vel*deltaTimeMillis, 0);}
      if (Controls.KEY_DOWN[KeyEvent.VK_I])     {lightSource.offsetPosition(new Vector3(0, 0,  0.001*deltaTimeMillis));}
      if (Controls.KEY_DOWN[KeyEvent.VK_K])     {lightSource.offsetPosition(new Vector3(0, 0, -0.001*deltaTimeMillis));}
      if (Controls.KEY_DOWN[KeyEvent.VK_J])     {lightSource.offsetPosition(new Vector3(-0.001*deltaTimeMillis, 0, 0));}
      if (Controls.KEY_DOWN[KeyEvent.VK_L])     {lightSource.offsetPosition(new Vector3( 0.001*deltaTimeMillis, 0, 0));}
      if (Controls.KEY_DOWN[KeyEvent.VK_O])     {lightSource.offsetPosition(new Vector3(0, -0.001*deltaTimeMillis, 0));}
      if (Controls.KEY_DOWN[KeyEvent.VK_U])     {lightSource.offsetPosition(new Vector3(0,  0.001*deltaTimeMillis, 0));}
      if (Controls.KEY_DOWN[KeyEvent.VK_UP])    {cam.offsetPitch(-0.1*deltaTimeMillis);}
      if (Controls.KEY_DOWN[KeyEvent.VK_DOWN])  {cam.offsetPitch( 0.1*deltaTimeMillis);}
      if (Controls.KEY_DOWN[KeyEvent.VK_LEFT])  {cam.offsetYaw  (-0.1*deltaTimeMillis);}
      if (Controls.KEY_DOWN[KeyEvent.VK_RIGHT]) {cam.offsetYaw  ( 0.1*deltaTimeMillis);}

      if (Controls.KEY_DOWN[KeyEvent.VK_T]) {
        setRenderer(Renderer.rayTri());
      }
      if (Controls.KEY_DOWN[KeyEvent.VK_G]) {
        setRenderer(Renderer.raySphere());
      }
      if (Controls.KEY_DOWN[KeyEvent.VK_B]) {
        setRenderer(Renderer.projection());
      }

      lightSource.offsetYaw(0.05*deltaTimeMillis);
      
      cam.draw(bodies);

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

    int size = Math.min(WINDOW.screenWidth(), (int)(WINDOW.screenHeight()/cam.getImageAspectRatio()));
    gra.drawImage(cam.getImage().getScaledInstance(size, (int)(size*cam.getImageAspectRatio()), BufferedImage.SCALE_DEFAULT), 0, 0, null);
    
    UIController.draw((Graphics2D)gra, WINDOW.screenWidth(), WINDOW.screenHeight());

    long cFTime = System.currentTimeMillis();

    if (cFTime-pFTime >= 1000) {
      fps = fCount*1000.0/(cFTime-pFTime);
      System.out.println(fps);
      pFTime = cFTime;
      fCount=0;
    }
    
    fCount++;
  }
}
