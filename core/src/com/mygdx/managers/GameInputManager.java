package com.mygdx.managers;

import java.util.HashMap;
import java.util.Map.Entry;


import com.mygdx.Actions.CircleAction;
import com.mygdx.Actions.CurveAction;
import com.mygdx.Actions.FieldAction;
import com.mygdx.Actions.ForceAction;
import com.mygdx.Actions.IAction;
import com.mygdx.Actions.MoveAction;
import com.mygdx.Actions.PathAction;
import com.mygdx.Actions.RectangleAction;
import com.mygdx.Actions.SpringAction;
import com.mygdx.Actions.StickAction;
import com.mygdx.Actions.VelocityAction;
import com.mygdx.GameWorld.GameConstants;
import com.mygdx.GameWorld.GameManager;
import com.mygdx.Renderer.GameRenderer;
import com.mygdx.ui.SimpleButton;
import com.mygdx.ui.LevelCreatorUI;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;

public class GameInputManager implements InputProcessor {
	
	private GameManager gameManager;
	private GameRenderer renderer;
	private Mouse mouse;
	private String selectedToolButton;
	
	private LevelCreatorUI ui = new LevelCreatorUI();
	
	private HashMap<String,SimpleButton> controlBar = new HashMap<String,SimpleButton>();
	private HashMap<String,SimpleButton> modifierBar = new HashMap<String,SimpleButton>();
	private HashMap<String,SimpleButton> toolBar = new HashMap<String, SimpleButton>();
	private HashMap<String,IAction> actions = new HashMap<String, IAction>();
	
	public GameInputManager(GameManager gameManager, GameRenderer renderer){
		this.gameManager = gameManager;
		this.renderer = renderer;
		this.mouse = new Mouse();
		ui.load(toolBar,controlBar,modifierBar);
		loadActions();
	}
	
	private void loadActions(){
		
		actions.put("MOVE", new MoveAction(gameManager,renderer));
		actions.put("CIRCLE", new CircleAction(gameManager,renderer,modifierBar.get("PINNED")));
		actions.put("RECTANGLE", new RectangleAction(gameManager,renderer,modifierBar.get("PINNED")));
		actions.put("PATH", new PathAction(gameManager,renderer,modifierBar.get("PINNED")));
		actions.put("CURVE", new CurveAction(gameManager,renderer));
		actions.put("FIELD", new FieldAction(gameManager,renderer));
		actions.put("SPRING", new SpringAction(gameManager,renderer));
		actions.put("STICK", new StickAction(gameManager,renderer));
		actions.put("VELOCITY", new VelocityAction(gameManager,renderer));
		actions.put("FORCE", new ForceAction(gameManager,renderer));
	}
	
	@Override
	public boolean touchDown (int x, int y, int pointer, int button) {
		
		if ( !gameManager.isCreate() && !gameManager.isRunning() )
			return false;  // pass event to next input processor
		
		// converting to physics coordinates
		float mousePos []  = toWorldCoordinates(x,y);
		mouse.posOnDrag = mousePos; // Gets rid of jumping when starting to pan the camera.
		y = GameConstants.HEIGHT - y;
		
		if ( gameManager.isCreate() && inCanvasBounds(x,y)  ){
			IAction action = actions.get(selectedToolButton);
			
			if ( action != null )
				return action.actOnTouchDown(mousePos);
		}
		
		/*
		 * TOOLBAR
		 */
		
		// TODO: tidy up all the code below
		if ( gameManager.isCreate() ){
			for(SimpleButton toolButton : toolBar.values()){
				if( toolButton.isTouchDown(x, y) ) {
					return true; //stop checking for other input
				}
			}
			for(SimpleButton modifierButton : modifierBar.values()){
				if( modifierButton.isTouchDown(x, y)){
					return true;
				}
			}
		} else if ( gameManager.isRunning() ){
			if ( toolBar.get("MOVE").isTouchDown(x, y) ){ 
				return true; //stop checking for other input
			}
		}
		
		// check and label for controlbar touch-downs
		for(SimpleButton controlButton : controlBar.values()){
			if( controlButton.isTouchDown(x, y) ) {
				return true; //stop checking for other input
			}
		}

		return true; // return true to indicate the event was handled
	}

	@Override
	public boolean touchUp (int x, int y, int pointer, int button) {
		if( !gameManager.isCreate() && !gameManager.isRunning())
			return false;  // pass event to next input processor
		
		// converting to physics coordinates
		float mousePos [] = toWorldCoordinates(x,y);
		y = GameConstants.HEIGHT - y;

		if ( gameManager.isCreate() && inCanvasBounds(x,y) ){
			IAction action = actions.get(selectedToolButton);
			
			if ( action != null )
				return action.actOnTouchUp(mousePos);
		}
		
		/*
		 * TOOLBAR
		 */
		// TODO: tidy up all the code below
		if ( gameManager.isCreate() ){
			// check if toolbar is hit. Label buttons as being pressed.
			for(Entry<String,SimpleButton> entry : toolBar.entrySet()){
				if(entry.getValue().isTouchUp(x, y)){
					if ( entry.getValue().getClicked() ) 
						selectedToolButton = entry.getKey(); 
					deselectOtherButtons(entry.getKey());
					return true;
				}
			}
			selectedToolButton = "";
			for(Entry<String,SimpleButton> entry : modifierBar.entrySet()){
				if(entry.getValue().isTouchUp(x, y)){
					return true;
				}
			}
		} else if ( gameManager.isRunning() ){
			if ( toolBar.get("MOVE").isTouchUp(x, y) ){
				selectedToolButton = "MOVE";
				deselectOtherButtons("MOVE");
				return true;
			}
		}

		// check if PLAY/PAUSE or RESTART was hit
		for(Entry<String,SimpleButton> entry : controlBar.entrySet()){
			if( entry.getValue().isTouchUp(x, y) ){
				Gdx.app.log("Control Bar", entry.getKey() + " touched.");
				if (entry.getKey().equals("RUN")){
					gameManager.toggleCreative();
					//entry.getValue().release();
				} else if ( entry.getKey().equals("RESTART")){
					Gdx.app.log("Stick button pressed", "yes.");
					controlBar.get("RUN").release();
					gameManager.restart();
				} else if ( entry.getKey().equals("SAVE") ){
					gameManager.save();
					entry.getValue().release();
				}
				return true;
			}
		}
		
		return true; // return true to indicate the event was handled
	}
	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		if(!gameManager.isCreate() && !gameManager.isRunning())
			return false;
		
		// convert to physics coordinates
		float mousePos [] = toWorldCoordinates(x,y);
		
		// saves mouse information for panning
		mouse.prevPos = mouse.posOnDrag;
		mouse.posOnDrag = mousePos;
		y = GameConstants.HEIGHT - y;
		
		if ( gameManager.isCreate() && inCanvasBounds(x,y) ){
			IAction action = actions.get(selectedToolButton);
			
			if ( action != null )
				return action.actOnTouchDragged(mousePos);
		}
		
		// pan camera
		if (!gameManager.isPinning() && !renderer.isBuilding()){
			float dx = mouse.prevPos[0] - mousePos[0];
			float dy = mouse.prevPos[1] - mousePos[1];
			renderer.translateCamera(dx, dy);
			return true;
		}

		return false;
	}

	@Override
	public boolean mouseMoved(int x, int y) {
		if(!gameManager.isCreate() && !gameManager.isRunning())
			return false;
		
		float mousePos [] = toWorldCoordinates(x,y);
		
		if ( toolBar.get("PATH").getClicked()){
			renderer.updateBuilder(mousePos);
		}
		return true;
	}

	@Override
	public boolean scrolled(int amount) {
		if(!gameManager.isCreate() && !gameManager.isRunning())
			return false;
		renderer.zoomCamera(amount);
		// TODO Auto-generated method stub
		return true;
	}
	/*
	 * Helpers
	 */
	
	private void deselectOtherButtons(String key){
		for(Entry<String,SimpleButton> otherButton : toolBar.entrySet()){
			if ( otherButton.getKey() != key ) otherButton.getValue().release();
		}

	}
	
	private float [] toWorldCoordinates(int x, int y){
		Vector3 coords = renderer.getWorldCam().unproject(new Vector3(x,y,0));
		return new float [] {coords.x, coords.y};
	}	
	
	private boolean inCanvasBounds(int x, int y){
		return (x > GameConstants.LEFTWALL && x < GameConstants.RIGHTWALL
				&& y > GameConstants.FLOOR && y < GameConstants.CEILING);
	}
	
	/*
	 * Getters
	 */
	public HashMap<String,SimpleButton> getToolbar(){
		return toolBar;
	}
	
	public HashMap<String,SimpleButton> getControlBar(){
		return controlBar;
	}
	
	public HashMap<String,SimpleButton> getModifierBar(){
		return modifierBar;
	}
	
}
