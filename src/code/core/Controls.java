package code.core;

import java.awt.Cursor;
import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import mki.math.vector.Vector2I;
import mki.ui.control.UIActionSetter;
import mki.ui.control.UIController;

/**
 * Handles all user input within the game
 */
abstract class Controls {
  
  public static final boolean[] KEY_DOWN = new boolean[65536];
  public static final boolean[] MOUSE_DOWN = new boolean[Math.max(MouseInfo.getNumberOfButtons(), 3)];
  
  public static Vector2I mousePos = new Vector2I();
  public static Vector2I mouseOff = new Vector2I();

  public static Cursor backupCursor;
  public static UIActionSetter<MouseEvent> mouseUpdateAction = Controls::updateMousePos;
  public static UIActionSetter<MouseEvent> mouseBackupAction = Controls::captureMousePos;

  private static Robot robot = null;
  
  /**
  * Starts up all the listeners for the window. Only to be called once on startup.
  */
  public static void initialiseControls(JFrame FRAME) {

    try {
      robot = new Robot();
    } catch (AWTException e) {
      e.printStackTrace();
    }

    backupCursor = FRAME.getToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(), null);
    
    // Mouse Controls
    FRAME.addMouseMotionListener(new MouseAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        mouseUpdateAction.set(e);
      }
      
      @Override
      public void mouseDragged(MouseEvent e) {
        mouseUpdateAction.set(e);
      }
    });
    FRAME.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        mouseUpdateAction.set(e);

        MOUSE_DOWN[e.getButton()] = true;
        
        if (UIController.getHighlightedInteractable() == null) MOUSE_DOWN[e.getButton()] = true;
        
        //left click
        if (e.getButton() == 1) {
          if (UIController.press()) return;
        }
      }
      
      @Override
      public void mouseReleased(MouseEvent e) {
        mouseUpdateAction.set(e);
        
        MOUSE_DOWN[e.getButton()] = false;
        
        //left click
        if (e.getButton() == 1) {
          UIController.release();
        }

        if (e.getButton() == 3) {
          mousePos = new Vector2I(FRAME.getX()+FRAME.getWidth()/2, FRAME.getY()+FRAME.getHeight()/2);
          robot.mouseMove(mousePos.x, mousePos.y);

          Cursor tempCursor = FRAME.getCursor();
          FRAME.setCursor(backupCursor);
          backupCursor = tempCursor;

          UIActionSetter<MouseEvent> tempAction = mouseUpdateAction;
          mouseUpdateAction = mouseBackupAction;
          mouseBackupAction = tempAction;
        }
      }
      
      @Override
      public void mouseExited(MouseEvent e) {
        mousePos = new Vector2I(Core.WINDOW.screenWidth()/2, Core.WINDOW.screenHeight()/2);
      }
    });
    
    // FRAME.addMouseWheelListener(new MouseAdapter() {
    //   public void mouseWheelMoved(MouseWheelEvent e) {

    //   }
    // });
    
    //Keyboard Controls
    FRAME.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        
        // if (UIController.getActiveTextfield() != null && !KEY_DOWN[KeyEvent.VK_CONTROL]) UIController.typeKey(e);
        
        if(KEY_DOWN[keyCode]) return; //Key already in
        KEY_DOWN[keyCode] = true;
        
        switch (keyCode) {
          case KeyEvent.VK_F11:
          Core.WINDOW.toggleFullscreen();
          break;
          case KeyEvent.VK_ESCAPE:
          UIController.release();
          UIController.back();
          break;
          case KeyEvent.VK_ENTER:
          UIController.press();
          break;
          default:
          break;
        }
      }
      
      @Override
      public void keyReleased(KeyEvent e){
        int keyCode = e.getKeyCode();
        KEY_DOWN[keyCode] = false;
        
        if (keyCode == KeyEvent.VK_ENTER) {
          UIController.release();
        }
      }
    });
  }
  
  /**
  * Updates the program's understanding of the location of the mouse cursor after a supplied {@code MouseEvent}.
  * 
  * @param e the {@code MouseEvent} to determine the cursor's current position from
  */
  public static void updateMousePos(MouseEvent e) {
    int x = e.getX() - Core.WINDOW.toolBarLeft;
    int y = e.getY() - Core.WINDOW.toolBarTop;
    mousePos = new Vector2I(x, y);
    
    UIController.cursorMove(mousePos);
  }

  public static void captureMousePos(MouseEvent e) {
    mouseOff = mouseOff.add(
      e.getXOnScreen() - mousePos.x, 
      e.getYOnScreen() - mousePos.y
    );

    robot.mouseMove(mousePos.x, mousePos.y);
  }
}
