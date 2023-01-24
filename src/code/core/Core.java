package code.core;

/**
* Core class for the evolution simulator
*/

import java.awt.image.BufferedImage;
import java.awt.Insets;

import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
// import java.awt.event.MouseEvent;
// import java.awt.event.MouseAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;

import javax.swing.JFrame;
import javax.swing.JPanel;

import code.core.scene.RigidBody;
import code.core.scene.Scene;
import code.core.scene.models.Sphere;
import code.math.IOHelp;
import code.math.Vector2;
import code.math.Vector3;

import java.awt.Graphics;
import java.awt.Graphics2D;

public class Core extends JPanel
{
  private static final long serialVersionUID = 1;

  // public static final Vector2 DEFAULT_SCREEN_SIZE = new Vector2(2560, 1440);
  public static final Vector2 DEFAULT_SCREEN_SIZE = new Vector2(1920, 1080);
  // public static final Vector2 DEFAULT_SCREEN_SIZE = new Vector2(1280, 720);
  // public static final Vector2 DEFAULT_SCREEN_SIZE = new Vector2(350, 350);

  private JFrame f;
  private boolean maximized = true;

  private boolean quit = false;

  public int toolBarLeft, toolBarRight, toolBarTop, toolBarBot;

  private boolean[] keyDown = new boolean[65536];
  // private boolean[] mouseDown = new boolean[4];
  // private Vector2 mousePos;

  boolean update = false;
  boolean first = true;

  RigidBody[] bodies;
  Sphere lightSource;

  Camera3D cam;
  double scale;
  int screenSizeX;
  int screenSizeY;
  int smallScreenX = (int)DEFAULT_SCREEN_SIZE.x;
  int smallScreenY = (int)DEFAULT_SCREEN_SIZE.y;

  // int[][] colourArray = new int[smallScreenX][smallScreenY];
  BufferedImage image = new BufferedImage(smallScreenX, smallScreenY, BufferedImage.TYPE_INT_ARGB);
  // BufferedImage bg = new BufferedImage(smallScreenX, smallScreenY, BufferedImage.TYPE_INT_ARGB);

  private long pTTime = System.currentTimeMillis();
  // private double tps = 0;
  // private int TCount = 0;

  private long pFTime = System.currentTimeMillis();
  private double fps = 0;
  private int fCount = 0;

  public static void main(String[] args) {
    // Spheres s = new Spheres(new Vector3());
    Core core = new Core();
    core.start();
    core.playGame();
  }

  public void start() {
    f = new JFrame("3D Test");
    f.getContentPane().add(this);
    f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    f.setResizable(true);
    BufferedImage image = IOHelp.readImage("icon.png");
    f.setIconImage(image);
    f.addWindowListener( new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        quit = true;
      }
    });
    f.addComponentListener( new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        screenSizeX = f.getWidth() - toolBarLeft - toolBarRight;
        screenSizeY = f.getHeight() - toolBarTop - toolBarBot;
        if (cam != null) {cam.setScreenSize(screenSizeX, screenSizeY);}
      }
    });
    f.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_F11) {
          doFull();
        }
        if (e.getKeyCode() == KeyEvent.VK_MINUS) {
          cam.setZoom(cam.getZoom()/2);
        }
        if (e.getKeyCode() == KeyEvent.VK_EQUALS) {
          cam.setZoom(cam.getZoom()*2);
        }
        keyDown[e.getKeyCode()] = true;
      }

      @Override
      public void keyReleased(KeyEvent e){
        keyDown[e.getKeyCode()] = false;
      }
    });
    // f.addMouseMotionListener(new MouseAdapter() {
    //   @Override
    //   public void mouseMoved(MouseEvent e) {
    //     double x = e.getX() - toolBarLeft;
    //     double y = e.getY() - toolBarTop;
    //     mousePos = new Vector2(x, y);
    //   }
    //
    //   @Override
    //   public void mouseDragged(MouseEvent e) {
    //     double x = e.getX() - toolBarLeft;
    //     double y = e.getY() - toolBarTop;
    //     mousePos = new Vector2(x, y);
    //   }
    // });
    // f.addMouseListener(new MouseAdapter() {
    //   @Override
    //   public void mousePressed(MouseEvent e) {
    //     double x = e.getX() - toolBarLeft;
    //     double y = e.getY() - toolBarTop;
    //     mousePos = new Vector2(x, y);
    //     mouseDown[e.getButton()] = true;
    //   }
    //
    //   @Override
    //   public void mouseReleased(MouseEvent e){
    //     mouseDown[e.getButton()] = false;
    //   }
    // });
    f.setExtendedState(JFrame.MAXIMIZED_BOTH);
    f.setUndecorated(true);
    f.setVisible(true);
    f.requestFocus();
    screenSizeX = f.getWidth();
    screenSizeY = f.getHeight();
    scale = screenSizeY/DEFAULT_SCREEN_SIZE.y;

    // bodies = new RigidBody[7];

    cam = new Camera3D(new Vector3(), 1, screenSizeX, screenSizeY);
    this.bodies = Scene.s1();
    lightSource = (Sphere)bodies[0];

    // bg = IOHelp.readImage("BG/space.png");
  }

  /**
  * A helper method that updates the window insets to match their current state
  */
  public void updateInsets() {
    Insets i = f.getInsets(); //Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration())
    // System.out.println(i);
    toolBarLeft = i.left;
    toolBarRight = i.right;
    toolBarTop = i.top;
    toolBarBot = i.bottom;
  }

  /**
  * A helper method that toggles fullscreen for the window
  */
  public void doFull() {
    f.removeNotify();
    if (maximized) {
      f.setExtendedState(JFrame.NORMAL);
      f.setUndecorated(false);
      f.addNotify();
      updateInsets();
      f.setSize(smallScreenX + toolBarLeft + toolBarRight, smallScreenY + toolBarTop + toolBarBot);
    }
    else {
      smallScreenX = screenSizeX;
      smallScreenY = screenSizeY;
      f.setVisible(false);
      f.setExtendedState(JFrame.MAXIMIZED_BOTH);
      f.setUndecorated(true);
      f.setVisible(true);
      updateInsets();
      f.addNotify();
    }
    f.requestFocus();
    maximized = !maximized;
  }

  public void playGame() {
    while (true) {
      long cTTime = System.currentTimeMillis();
      if (cTTime-pTTime >= 31) {
        pTTime = cTTime;

        if (keyDown[KeyEvent.VK_W]) {cam.move(0, 0, 0.5);}
        if (keyDown[KeyEvent.VK_S]) {cam.move(0, 0, -0.5);}
        if (keyDown[KeyEvent.VK_A]) {cam.move(-0.5, 0, 0);}
        if (keyDown[KeyEvent.VK_D]) {cam.move(0.5, 0, 0);}
        if (keyDown[KeyEvent.VK_SHIFT]) {cam.move(0, -0.5, 0);}
        if (keyDown[KeyEvent.VK_SPACE]) {cam.move(0, 0.5, 0);}
        if (keyDown[KeyEvent.VK_I]) {lightSource.move(0, 0, 0.5);}
        if (keyDown[KeyEvent.VK_K]) {lightSource.move(0, 0, -0.5);}
        if (keyDown[KeyEvent.VK_J]) {lightSource.move(-0.5, 0, 0);}
        if (keyDown[KeyEvent.VK_L]) {lightSource.move(0.5, 0, 0);}
        if (keyDown[KeyEvent.VK_O]) {lightSource.move(0, -0.5, 0);}
        if (keyDown[KeyEvent.VK_U]) {lightSource.move(0, 0.5, 0);}
        if (keyDown[KeyEvent.VK_UP]) {cam.rotateX(-5);}
        if (keyDown[KeyEvent.VK_DOWN]) {cam.rotateX(5);}
        if (keyDown[KeyEvent.VK_LEFT]) {cam.rotateY(-5);}
        if (keyDown[KeyEvent.VK_RIGHT]) {cam.rotateY(5);}
        if (keyDown[KeyEvent.VK_M]) {cam.setSparse(10); update = true;}
        if (keyDown[KeyEvent.VK_N]) {cam.setSparse(1); update = false; first = true;}

        if (update || first) {image.setRGB(0, 0, (int)DEFAULT_SCREEN_SIZE.x, (int)DEFAULT_SCREEN_SIZE.y, cam.draw(bodies), 0, (int)DEFAULT_SCREEN_SIZE.x); first = false;}

        // bg.setRGB(0, 0, (int)DEFAULT_SCREEN_SIZE.x, (int)DEFAULT_SCREEN_SIZE.y, cam.background(), 0, (int)DEFAULT_SCREEN_SIZE.x);
        if (quit) {
          System.exit(0);
        }
      }
      repaint();
    }
  }

  public void paint(Graphics gra) {
    Graphics2D g = (Graphics2D) gra;

    // g.drawImage(bg, 0, 0, null);

    g.drawImage(image, 0, 0, null);

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
