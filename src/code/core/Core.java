package code.core;

import code.rendering.renderers.Renderer;

import code.world.Camera3D;
import code.world.RigidBody;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import mki.io.FileIO;

import mki.math.vector.Vector3;

import mki.ui.control.UIController;

enum State {
  MAINMENU,
  RUN,
  SPLASH
}

public abstract class Core {
  
  public static final Window WINDOW;
  
  public static final Settings GLOBAL_SETTINGS;
  
  private static final long TICKS_PER_SECOND = 60;
  private static final long MILLISECONDS_PER_TICK = 900/TICKS_PER_SECOND;

  private static final long START_TIME = System.currentTimeMillis();
  private static final int SPLASH_TIME = 1000;
  
  private static final BufferedImage SPLASH;

  private static State state = State.SPLASH;
  
  private static boolean quit = false;
  
  private static RigidBody[] bodies = {};
  private static RigidBody lightSource = null;
  
  private static Camera3D cam;
  private static double camFOVChange = -1;

  private static long pTTime = System.currentTimeMillis();
  private static long pFTime = System.currentTimeMillis();
  
  private static double fps = 0;
  private static int fCount = 0;
  
  static {
    WINDOW = new Window("3D Test", (x, y) -> {});
    
    GLOBAL_SETTINGS = new Settings();

    SPLASH = FileIO.readImage("splash.png");
    WINDOW.FRAME.setBackground(new Color(173, 173, 173));

    UIController.putPane("Main Menu", UICreator.createMain());
    UIController.putPane("HUD"      , UICreator.createHUD ());

    cam = new Camera3D(
      new Vector3(),
      GLOBAL_SETTINGS.getIntSetting("resolution_X"),
      GLOBAL_SETTINGS.getIntSetting("resolution_Y"),
      GLOBAL_SETTINGS.getDoubleSetting("fieldOfView"),
      Renderer.rasterizer()
    );
  }
  
  /**
   * Main method. Called on execution. Performs basic startup
   *
   * @param args Ignored for now
   * @throws InterruptedException if thread sleeping fails for whatever reason. Catastrophic error should kill process.
   */
  public static void main(String[] args) throws InterruptedException {
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

  public static State getState() {
    return state;
  }

  public static void setRenderer(Renderer r) {
    cam.setRenderer(r);
  }

  public static void setFieldOfView(double f) {
    camFOVChange = f;
  }

  public static void quitToMenu() {
    cam.setPosition(new Vector3());
    cam.resetRotation();
    Core.bodies = new RigidBody[0];
    Core.lightSource = null;
    Core.state = State.MAINMENU;
    UIController.setCurrentPane("Main Menu");
  }

  public static void loadScene(RigidBody[] bodies) {
    cam.setPosition(new Vector3());
    cam.resetRotation();
    Core.bodies = bodies;
    Core.lightSource = bodies[0];
    Core.state = State.RUN;
    UIController.setCurrentPane("HUD");
  }
  
  /**
  * Sets a flag to close the program at the nearest convenience
  */
  public static void quitToDesk() {
    if (quit) System.exit(1);
    
    quit = true;
  }
  
  public static void playGame() throws InterruptedException {
    while (true) {
      long tickTime = System.currentTimeMillis();
      long deltaTimeMillis = tickTime - pTTime;
      pTTime  = tickTime;

      if (state == State.SPLASH && tickTime-START_TIME >= SPLASH_TIME) {
        Controls.initialiseControls(WINDOW.FRAME);
        quitToMenu();
      }
      else if (state == State.RUN) {
        Controls.doInput(deltaTimeMillis, cam);

        if (lightSource != null) {
          Controls.targetInput(deltaTimeMillis, lightSource);

          lightSource.offsetYaw  (0.05*deltaTimeMillis);
          lightSource.offsetPitch(0.01*deltaTimeMillis);
          lightSource.offsetRoll (0.02*deltaTimeMillis);
        }
      }
      
      cam.draw(bodies);

      if (camFOVChange > 0) {
        cam.setFieldOfView(camFOVChange);
        camFOVChange = -1;
      }

      if (quit) {
        System.exit(0);
      }
      WINDOW.PANEL.repaint();
      
      tickTime = System.currentTimeMillis() - tickTime;
      Thread.sleep(Math.max(MILLISECONDS_PER_TICK - tickTime, 0));
    }
  }
  
  /**
  * Paints the contents of the program to the given {@code Graphics} object.
  * 
  * @param gra the supplied {@code Graphics} object
  */
  public static void paintComponent(Graphics gra) {

    switch (state) {
      case SPLASH:
        gra.drawImage(SPLASH, (WINDOW.screenWidth()-SPLASH.getWidth())/2, (WINDOW.screenHeight()-SPLASH.getHeight())/2, null);
      break;
      default:
        int size = Math.min(WINDOW.screenWidth(), (int)(WINDOW.screenHeight()/cam.getImageAspectRatio()));
        gra.drawImage(cam.getImage().getScaledInstance(size, (int)(size*cam.getImageAspectRatio()), BufferedImage.SCALE_DEFAULT), 0, 0, null);
    
        UIController.draw((Graphics2D)gra, WINDOW.screenWidth(), WINDOW.screenHeight());
      break;
    }

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
