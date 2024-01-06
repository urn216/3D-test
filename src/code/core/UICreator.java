package code.core;

import code.rendering.renderers.Renderer;
import mki.math.vector.Vector2;
import mki.math.vector.Vector2I;
import mki.ui.control.*;
import mki.ui.elements.*;
import mki.ui.components.*;
import mki.ui.components.interactables.*;

public class UICreator {
  // private static final UIElement VIRTUAL_KEYBOARD = new ElemKeyboard();
  
  private static final double COMPON_HEIGHT = 0.045;
  private static final double BUFFER_HEIGHT = 0.007;

  private static final UIComponent[] optionList = {
    new UIToggle(
      "Fullscreen", 
      Core.WINDOW::isFullScreen, 
      (b) -> {
        Core.GLOBAL_SETTINGS.setBoolSetting("fullScreen", b); 
        Core.WINDOW.setFullscreen(b);
      }
    ),
    new UIDropDown<Vector2I>(
      "Resolution: %s",
      ( ) -> {
        Vector2I v = Core.getActiveCam().getImageDimensions();
        return v.x + " x " + v.y;
      },
      (v) -> {
        Core.GLOBAL_SETTINGS.setIntSetting("resolution_X", v.x);
        Core.GLOBAL_SETTINGS.setIntSetting("resolution_Y", v.y);
        Core.getActiveCam().setImageDimensions(v.x, v.y);
      },
      new Vector2I(256 , 144 ),
      new Vector2I(560 , 315 ),
      new Vector2I(1280, 720 ),
      new Vector2I(1920, 1080)
    ),
    new UISlider.Double(
      "FOV: %.0f",
      ( ) -> Core.getActiveCam().getFieldOfView(),
      (f) -> {
        Core.GLOBAL_SETTINGS.setDoubleSetting("fieldOfView", f);
        Core.getActiveCam().setFieldOfView(f);
      },
      30,
      100
    ),
    new UIButton("Back", UIController::back)
  };

  /**
  * Creates the UI pane for the main menu.
  */
  public static UIPane createMain() {
    UIPane mainMenu = new UIPane();
    
    UIElement outPanel = leftList(
      new UIButton("Play"   , () -> UIController.setState(UIState.NEW_GAME)),
      new UIButton("Options", () -> UIController.setState(UIState.OPTIONS) ),
      new UIButton("Quit"   , Core::quitToDesk)
    );

    UIElement newGame = leftList(
      new UIButton("Marbles" , () -> Core.loadScene(Scene.s1())),
      new UIButton("Gunship" , () -> Core.loadScene(Scene.s2())),
      new UIButton("Room"    , () -> Core.loadScene(Scene.s3())),
      new UIButton("Terrain" , () -> Core.loadScene(Scene.s4())),
      new UIButton("Voxels"  , () -> Core.loadScene(Scene.s4_1())),
      new UIButton("Marble 2", () -> Core.loadScene(Scene.s5())),
      new UIButton("Back", UIController::back)
    );

    UIElement options = leftList(0.2, optionList);

    mainMenu.addState(UIState.DEFAULT , outPanel);
    mainMenu.addState(UIState.NEW_GAME, newGame  , UIState.DEFAULT);
    mainMenu.addState(UIState.OPTIONS , options  , UIState.DEFAULT, checkSettings);

    mainMenu.clear();
    
    return mainMenu;
  }

  public static UIPane createHUD() {
    UIPane HUD = new UIPane();
    
    UIElement outPanel = leftList(
      new UISlider.Double("FPS: %.0f", Core::getFps, (f)->{}, 0, 100),
      new UIButton("Ray S"  , () -> Core.setRenderer(Renderer.raySphere())),
      new UIButton("Ray T"  , () -> Core.setRenderer(Renderer.rayTri())),
      new UIButton("Proj"   , () -> Core.setRenderer(Renderer.projection())),
      new UIButton("Options", () -> UIController.setState(UIState.OPTIONS) ),
      new UIButton("Menu"   , Core::quitToMenu),
      new UIButton("Quit"   , Core::quitToDesk)
    );

    UIElement options = rightList(0.2, optionList);

    HUD.addState(UIState.DEFAULT, outPanel);
    HUD.addState(UIState.OPTIONS, outPanel , UIState.DEFAULT, checkSettings);
    HUD.addState(UIState.OPTIONS, options );

    HUD.clear();
    
    return HUD;
  }

  protected static UIElement leftList(UIComponent... components) {
    return leftList(0.07, components);
  }

  protected static UIElement leftList(double width, UIComponent... components) {
    return new ElemListVert(
      new Vector2(0    , 0),
      new Vector2(width, UIHelp.calculateListHeight(BUFFER_HEIGHT, UIHelp.calculateComponentHeights(COMPON_HEIGHT, components))),
      COMPON_HEIGHT,
      BUFFER_HEIGHT,
      components,
      UIElement.TRANSITION_SLIDE_LEFT
    );
  }

  protected static UIElement rightList(UIComponent... components) {
    return rightList(0.07, components);
  }

  protected static UIElement rightList(double width, UIComponent... components) {
    return new ElemListVert(
      new Vector2(1-width, 0),
      new Vector2(1      , UIHelp.calculateListHeight(BUFFER_HEIGHT, UIHelp.calculateComponentHeights(COMPON_HEIGHT, components))),
      COMPON_HEIGHT,
      BUFFER_HEIGHT,
      components,
      UIElement.TRANSITION_SLIDE_RIGHT
    );
  }

  private static final ElemConfirmation settingsChanged = new ElemConfirmation(
    new Vector2(0.35, 0.5-UIHelp.calculateListHeight(BUFFER_HEIGHT, COMPON_HEIGHT/2, COMPON_HEIGHT)/2),
    new Vector2(0.65, 0.5+UIHelp.calculateListHeight(BUFFER_HEIGHT, COMPON_HEIGHT/2, COMPON_HEIGHT)/2), 
    BUFFER_HEIGHT, 
    UIElement.TRANSITION_SLIDE_DOWN,
    () -> {Core.GLOBAL_SETTINGS.saveChanges();   UIController.retState();},
    () -> {
      Core.GLOBAL_SETTINGS.revertChanges();
      Core.getActiveCam().setImageDimensions(
        Core.GLOBAL_SETTINGS.getIntSetting("resolution_X"), 
        Core.GLOBAL_SETTINGS.getIntSetting("resolution_Y")
      );
      Core.getActiveCam().setFieldOfView(Core.GLOBAL_SETTINGS.getDoubleSetting("fieldOfView"));
      UIController.retState();
    },
    "Save Changes?"
  );
  
  /**
  * A lambda function which, in place of transitioning back a step,
  * checks if the global settings have been changed and if so, 
  * brings up a confirmation dialogue to handle the changes before transitioning back.
  */
  public static final UIAction checkSettings = () -> {
    if (Core.GLOBAL_SETTINGS.hasChanged()) UIController.displayTempElement(settingsChanged);
    else UIController.retState();
  };
}
