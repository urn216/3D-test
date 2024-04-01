package mki.core;

import mki.math.QuadFunction;
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
    new UIText("", 1, 0),
    new UIToggle(
      "Normal Maps", 
      Constants::usesNormalMap, 
      Constants::setNormalMapUse
    ),
    new UIToggle(
      "Dynamic Lights", //TEMPORARY (Want dedicated options for each renderer. This only matters for rasterizer)
      Constants::usesDynamicRasterLighting, 
      Constants::setDynamicRasterLighting
    ),
    new UIDropDown<QuadFunction<int[], Integer, Double, Double, Integer>>(
      "Filtering: %s",
      ( ) -> {
        return Constants.getFilteringMode().toString();
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
      (f) -> {
        Core.GLOBAL_SETTINGS.setDoubleSetting("fieldOfView", f);
        Core.setFieldOfView(f);
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
    
    UIElement outPanel = leftList(
      new UISlider.Double("FPS: %.0f", Core::getFps, (f)->{}, 0, 100),
      new UIButton("Ray (SPH)" , () -> Core.setRenderer(Renderer.raySphere())),
      new UIButton("Ray (TRI)" , () -> Core.setRenderer(Renderer.rayTri())),
      new UIButton("Rasterizer", () -> Core.setRenderer(Renderer.rasterizer())),
      new UIButton("Mtn Raster", () -> Core.setRenderer(Renderer.motionSensor())),
      new UIButton("Wireframe" , () -> Core.setRenderer(Renderer.wireframe())),
      new UIButton("Options"   , () -> UIController.setState(UIState.OPTIONS) ),
      new UIButton("Main Menu" , Core::quitToMenu),
      new UIButton("Quit"      , Core::quitToDesk)
    );

    UIElement options = rightList(0.2, optionList);

    HUD.addState(UIState.DEFAULT, outPanel);
    HUD.addState(UIState.OPTIONS, outPanel , UIState.DEFAULT, checkSettings);
    HUD.addState(UIState.OPTIONS, options );

    HUD.clear();
    
    return HUD;
  }

  protected static UIElement leftList(UIComponent... components) {
    return leftList(0.078, components);
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
