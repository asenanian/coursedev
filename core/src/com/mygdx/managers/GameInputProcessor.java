package com.mygdx.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.mygdx.Entities.Point;
import com.mygdx.GameWorld.GameConstants;
import com.mygdx.GameWorld.GameWorld;
import com.mygdx.ui.SimpleButton;
import com.mygdx.ui.LevelCreatorUI;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

public class GameInputProcessor implements InputProcessor {
	
	private GameWorld world;
	
	private LevelCreatorUI ui = new LevelCreatorUI();
	private HashMap<String,SimpleButton> controlBar = new HashMap<String,SimpleButton>();
	private HashMap<String,SimpleButton> toolBar = new HashMap<String, SimpleButton>();
	
	public GameInputProcessor(GameWorld world){
		
		this.world = world;
		ui.load(toolBar,controlBar);
	}
	
	
	@Override
	public boolean touchDown (int x, int y, int pointer, int button) {
		if( !world.isCreate() && !world.isRunning())
			return false;  // pass event to next input processor
		
		y = GameConstants.HEIGHT - y;

		// check and label for toolbar touch-downs
		for(SimpleButton toolButton : toolBar.values()){
			if(toolButton.isTouchDown(x, y)) {
				return true; //stop checking for other input
			}
		}
		
		// check and label for controlbar touch-downs
		for(SimpleButton controlButton : controlBar.values()){
			if(controlButton.isTouchDown(x, y)) {
				return true; //stop checking for other input
			}
		}
		
		float mousePos [] = new float[] {x,y};		
		
		if(world.getBounds().containsPos(mousePos)){
			int i = inPoint(mousePos);
			if(world.isCreate()){
				if(i == -1){ //Didn't click on a point
					if(toolBar.get("CURVE").getClicked()){
						Mouse.setIndexBegin(world.getPoints().size());  //last index in points array
						world.addPoint(mousePos, true);
					} else if(toolBar.get("CIRCLE").getClicked()){
						Mouse.setIndexBegin(world.getPoints().size());
						Mouse.clickedPos = mousePos;
						//world.addCircle(mousePos, GameConstants.CIRCLE_RADIUS);
					} 
					return true;
				}
				else { // Did click on a point
					if( world.getPoints().size() > 1 || toolBar.get("CURVE").getClicked()){
						Mouse.setIndexBegin(i);
						for(Point p : world.getPoints()){
							p.makeBig();
						}
					}
				}
				return true;
			} else if(world.isRunning()){
				Mouse.setIndexPinned(i);
				return true;
			}
		}

		return false; // return true to indicate the event was handled
	}
	
	@Override
	public boolean touchUp (int x, int y, int pointer, int button) {
		if( !world.isCreate() && !world.isRunning())
			return false;  // pass event to next input processor
		
		y = GameConstants.HEIGHT - y;
		float mousePos[] = new float[] {x,y};

		if(world.getBounds().containsPos(mousePos)){
			if(world.isCreate()){
				Gdx.app.log("Current State", "Create");
				if(pointToPoint(mousePos)){
					world.addSpring(Mouse.getIndexBegin(),Mouse.getIndexEnd(),toolBar.get("STICK").getClicked(),false);
				}
				for(Point p : world.getPoints())
					p.makeSmall();
				if(Mouse.getIndexBegin() != -1 && Mouse.isDragged() && toolBar.get("CIRCLE").getClicked()){
					float[] pointOnCircle = {x - Mouse.clickedPos[0],y - Mouse.clickedPos[1]};
					float radius = (float) Math.sqrt(Math.pow(pointOnCircle[0],2) + Math.pow(pointOnCircle[1],2));
					world.addCircle(Mouse.clickedPos, radius);
				}
			}
		}
		
		Mouse.setDragged(false);
		
		if(world.isCreate()){
			Mouse.setIndexBegin(-1);
			Mouse.setIndexEnd(-1);
		}
		else
			Mouse.setIndexPinned(-1);
		
		// check if toolbar is hit. Label buttons as being pressed.
		for(Entry<String,SimpleButton> entry : toolBar.entrySet()){
			if(entry.getValue().isTouchUp(x, y)){
				deselectOtherButtons(entry);
				Gdx.app.log("Tool Bar", entry.getKey() + " touched.");
				return true;
			}
		}

		// check if PLAY/PAUSE or RESTART was hit
		for(Entry<String,SimpleButton> entry : controlBar.entrySet()){
			if( entry.getValue().isTouchUp(x, y) ){
				Gdx.app.log("Control Bar", entry.getKey() + " touched.");
				if (entry.getKey().equals("RUN")){
					world.toggleCreative();
					//entry.getValue().release();
				} else if ( entry.getKey().equals("RESTART")){
					entry.getValue().release();
			    	world.restart();
				}
				// TODO handle abstract button
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
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(!world.isCreate() && !world.isRunning())
			return false;
		
		Mouse.setDragged(true);	
		screenY = GameConstants.HEIGHT - screenY;
		
		if(world.isCreate()){
			if(!toolBar.get("MOVE").getClicked()){ //TODO figure out enum for toolbars
				for(Point p : world.getPoints())
					p.makeBig();
				
				float mousePos[] = new float[] {screenX, screenY};
				if(pointToPoint(mousePos)){
					world.addSpring(Mouse.getIndexBegin(), Mouse.getIndexEnd(), toolBar.get("STICK").getClicked(), false);
					Mouse.setIndexBegin(Mouse.getIndexEnd()); // begin building next spring
					Mouse.setIndexEnd(-1);
				}
			}
			
			if(toolBar.get("CURVE").getClicked()){
				world.addCurve(screenX,screenY);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if(!world.isCreate() && !world.isRunning())
			return false;
		
		screenY = GameConstants.HEIGHT - screenY;
		float mousePos[] = new float [] {screenX, screenY};
		int i = inPoint(mousePos);
		
		if( Mouse.getPointHovered() != i && Mouse.getPointHovered() != -1 && world.getPoints().size() > 0){
			world.getPoints().get(Mouse.getPointHovered()).makeSmall();
		}
		if(i != -1 && world.isCreate()){
			world.getPoints().get(i).makeBig();
			Mouse.setPointHovered(i);
		}
		return true;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
	/*
	 * Helpers
	 */
	private int inPoint(float [] pos){
		ArrayList<Point> points = world.getPoints();
		for (Point p : points) {
	    	if (p.containsPos(pos)) {
	    		return points.indexOf(p);
	    	}
		}
		return -1;
	}
	
	/** checks if the mouse was released on a point, AND if had been dragging from a previous, different point.
	 * 
	 * @param mousePos position of the mouse
	 * @return true if released on a mouse, false otherwise.
	 */
	private boolean pointToPoint(float [] mousePos) {
		if (Mouse.getIndexBegin() != -1) {
			//clicked on first point to build a chain...
			int i = inPoint(mousePos);
			if (i != -1 && i != Mouse.getIndexBegin()) {
				Mouse.setIndexEnd(i);
				return true;
			}
				
	    }
		return false;
	}
	
	private void deselectOtherButtons(Entry<String,SimpleButton> button){
		if(button.getKey() == "MOVE"){
			toolBar.get("CIRCLE").release();
			toolBar.get("SPRING").release();
			toolBar.get("CURVE").release();
		} else if(button.getKey() == "CIRCLE"){
			toolBar.get("MOVE").release();
			toolBar.get("SPRING").release();
			toolBar.get("CURVE").release();
		} else if(button.getKey() == "SPRING"){
			toolBar.get("MOVE").release();
			toolBar.get("CIRCLE").release();
			toolBar.get("CURVE").release();
		} else if(button.getKey() == "CURVE"){
			toolBar.get("MOVE").release();
			toolBar.get("SPRING").release();
			toolBar.get("CIRCLE").release();
		}
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
	
}
