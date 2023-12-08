package code.core;

import code.rendering.renderers.Renderer;
import mki.math.vector.Vector2;
import mki.ui.control.UIHelp;
import mki.ui.control.UIPane;
import mki.ui.control.UIState;
import mki.ui.components.UIComponent;
import mki.ui.components.interactables.*;

import mki.ui.elements.*;

public class UICreator {
  // private static final UIElement VIRTUAL_KEYBOARD = new ElemKeyboard();
  
  private static final double COMPON_HEIGHT = 0.045;
  private static final double BUFFER_HEIGHT = 0.007;

  /**
  * Creates the UI pane for the main menu.
  */
  public static UIPane createMain() {
    UIPane mainMenu = new UIPane();
    
    UIElement outPanel = leftList(
      new UISlider.Double("FPS: %.0f", Core::getFps, (f)->{}, 0, 100),
      new UIButton("Ray S", () -> Core.setRenderer(Renderer.raySphere())),
      new UIButton("Ray T", () -> Core.setRenderer(Renderer.rayTri())),
      new UIButton("Proj" , () -> Core.setRenderer(Renderer.projection())),
      new UIButton("Quit", Core::quitToDesk)
    );

    UIElement fovSlider = new ElemListVert(
      new Vector2(0.25, 0),
      new Vector2(0.75, UIHelp.calculateListHeight(BUFFER_HEIGHT, COMPON_HEIGHT*2)),
      COMPON_HEIGHT,
      BUFFER_HEIGHT,
      new UIComponent[] {
        new UISlider.Double("FOV: %.0f", ()->Core.getActiveCam().getFieldOfView(), (f)->Core.getActiveCam().setFieldOfView(f), 30, 100),
      },
      UIElement.TRANSITION_SLIDE_UP
    );

    mainMenu.addState(UIState.DEFAULT, outPanel);
    mainMenu.addState(UIState.DEFAULT, fovSlider);

    mainMenu.clear();
    
    return mainMenu;
  }

  private static UIElement leftList(UIComponent... components) {
    return new ElemListVert(
      new Vector2(0  , 0),
      new Vector2(0.06, UIHelp.calculateListHeight(BUFFER_HEIGHT, UIHelp.calculateComponentHeights(COMPON_HEIGHT, components))),
      COMPON_HEIGHT,
      BUFFER_HEIGHT,
      components,
      UIElement.TRANSITION_SLIDE_LEFT
    );
  }
}
