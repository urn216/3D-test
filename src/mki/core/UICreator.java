package mki.core;

import mki.math.TriFunction;
import mki.math.vector.Vector2;
import mki.math.vector.Vector2I;
import mki.rendering.Constants;
import mki.rendering.renderers.Renderer;
import mki.ui.components.*;
import mki.ui.components.interactables.*;
import mki.ui.control.*;
import mki.ui.elements.*;
import mki.world.Material;

class UICreator {
  // private static final UIElement VIRTUAL_KEYBOARD = new ElemKeyboard();
  
  private static final double COMPON_HEIGHT = 0.045;
  private static final double BUFFER_HEIGHT = 0.007;

  private static final UIComponent[] optionList = {
    new UIToggle(
      "Fullscreen", 
      Core.WINDOW::isFullScreen, 
      (b) -> Core.GLOBAL_SETTINGS.setBoolSetting("v_fullScreen", b)
    ),
    new UIDropDown<Vector2I>(
      "Resolution: %s",
      ( ) -> {
        Vector2I v = Core.getActiveCam().getImageDimensions();
        return v.x + " x " + v.y;
      },
      (v) -> Core.GLOBAL_SETTINGS.setVector2ISetting("v_resolution", v),
      new Vector2I(256 , 144 ),
      new Vector2I(512 , 288 ),
      new Vector2I(560 , 315 ),
      new Vector2I(1280, 720 ),
      new Vector2I(1920, 1080)
    ),
    new UIText("", 1, 0),
    new UIToggle(
      "Normal Maps", 
      Constants::usesNormalMap, 
      (b) -> Core.GLOBAL_SETTINGS.setBoolSetting("v_normalmapping", b)
    ),
    new UIToggle(
      "Dynamic Lights",
      Constants::usesDynamicRasterLighting, 
      (b) -> Core.GLOBAL_SETTINGS.setBoolSetting("v_fancylighting", b)
    ),
    new UIDropDown<TriFunction<int[][], Double, Double, Integer>>(
      "Filtering Mode: %s",
      ( ) -> {
        return "NN";//Constants.getFilteringMode().toString();
      },
      (f) -> {
        Constants.setFilteringMode(f);
      },
      Material::getNearestNeighbourFilteringTexel,
      Material::getBilinearFilteringTexel
    ),
    new UISlider.Double(
      "FOV: %.0f",
      ( ) -> Core.getActiveCam().getFieldOfView(),
      (f) -> Core.GLOBAL_SETTINGS.setDoubleSetting("v_fieldOfView", f),
      30,
      100
    ),
    new UIText("", 1, 0),
    new UIButton("Reset To Defaults", Core.GLOBAL_SETTINGS::resetToDefault),
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
      new UIButton("Marbles" , () -> Core.loadScene(Scene::spheres)),
      new UIButton("Gunship" , () -> Core.loadScene(Scene::gunship)),
      new UIButton("Room"    , () -> Core.loadScene(Scene::roomBright)),
      new UIButton("Terrain" , () -> Core.loadScene(Scene::heightMap)),
      new UIButton("Voxels"  , () -> Core.loadScene(Scene::voxelMap)),
      new UIButton("Marble 2", () -> Core.loadScene(Scene::threeSpheres)),
      new UIButton("Room 2"  , () -> Core.loadScene(Scene::roomReflection)),
      new UIButton("Cubes"   , () -> Core.loadScene(Scene::cubes)),
      new UIButton("Cubes NM", () -> Core.loadScene(Scene::cubesNM)),
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

    UIText fps = new UIText(() -> String.format("FPS: %.0f", Core.getFps()), 1, 1);
    UIElement fpsCounter = new ElemListVert(
      new Vector2(0   , 0),
      new Vector2(0.07, UIHelp.calculateListHeight(BUFFER_HEIGHT, UIHelp.calculateComponentHeights(COMPON_HEIGHT, fps))),
      COMPON_HEIGHT,
      BUFFER_HEIGHT,
      new UIComponent[]{fps},
      UIElement.TRANSITION_SLIDE_LEFT
    );
    
    UIElement outPanel = leftList(
      new UIButton("Return"   , UIController::back                          ),
      new UIButton("Options"  , () -> UIController.setState(UIState.OPTIONS)),
      new UIButton("Main Menu", Core::quitToMenu                            ),
      new UIButton("Quit"     , Core::quitToDesk                            )
    );

    UIElement renderMethod = rightList(0.1,
      new UIText("Ray Tracing", 1, 1),
      new UIButton("Spheres-Old" , () -> Core.setRenderer(Renderer.raySphere())),
      new UIButton("Spheres-Fast", () -> Core.setRenderer(Renderer.gpuFastSphere())),
      new UIButton("Spheres-Slow", () -> Core.setRenderer(Renderer.gpuRaySphere())),
      new UIButton("Tris-Old"    , () -> Core.setRenderer(Renderer.rayTri())),
      new UIText("Rasterizing", 1, 1),
      new UIButton("Rasterizer"  , () -> Core.setRenderer(Renderer.rasterizer())),
      new UIButton("Motion Sense", () -> Core.setRenderer(Renderer.motionSensor())),
      new UIButton("Wireframe"   , () -> Core.setRenderer(Renderer.wireframe()))
    );

    UIElement options = leftList(0.2, optionList);

    HUD.setModeParent(UIState.DEFAULT, UIState.PAUSED);

    HUD.addState(UIState.DEFAULT, fpsCounter );
    HUD.addState(UIState.PAUSED , outPanel    , UIState.DEFAULT);
    HUD.addState(UIState.PAUSED , fpsCounter );
    HUD.addState(UIState.OPTIONS, renderMethod, UIState.PAUSED  , checkSettings);
    HUD.addState(UIState.OPTIONS, options    );
    HUD.addState(UIState.OPTIONS, fpsCounter );

    HUD.clear();
    
    return HUD;
  }

  protected static UIElement leftList(UIComponent... components) {
    return leftList(0.078, components);
  }

  protected static UIElement leftList(double width, UIComponent... components) {
    double height = UIHelp.calculateListHeight(BUFFER_HEIGHT, UIHelp.calculateComponentHeights(COMPON_HEIGHT, components));
    return new ElemListVert(
      new Vector2(0    , 0.4-height/2),
      new Vector2(width, 0.4+height/2),
      COMPON_HEIGHT,
      BUFFER_HEIGHT,
      components,
      UIElement.TRANSITION_SLIDE_LEFT
    );
  }

  protected static UIElement rightList(UIComponent... components) {
    return rightList(0.078, components);
  }

  protected static UIElement rightList(double width, UIComponent... components) {
    double height = UIHelp.calculateListHeight(BUFFER_HEIGHT, UIHelp.calculateComponentHeights(COMPON_HEIGHT, components));
    return new ElemListVert(
      new Vector2(1-width, 0.4-height/2),
      new Vector2(1      , 0.4+height/2),
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
        Core.GLOBAL_SETTINGS.getIntSetting("v_resolution_X"), 
        Core.GLOBAL_SETTINGS.getIntSetting("v_resolution_Y")
      );
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
