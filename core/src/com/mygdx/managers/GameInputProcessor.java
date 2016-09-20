package com.mygdx.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.mygdx.Entities.IGameObject;
import com.mygdx.GameWorld.GameConstants;
import com.mygdx.GameWorld.GameManager;
import com.mygdx.Renderer.GameRenderer;
import com.mygdx.ui.SimpleButton;
import com.mygdx.ui.LevelCreatorUI;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class GameInputProcessor implements InputProcessor {
	
	private GameManager world;
	private GameRenderer renderer;
	private Mouse mouse;
	
	private static class Mouse{
		public int indexDown;
		// index of point on touchDown.
		public float posDown[] = {0,0};
		// position of the indexDown point.
		public float posOnDrag[] = {0,0}; 
		// saves mouse position information to relay to prevPos on next time step
		public float prevPos[] = {0,0};
		public Mouse(){}		
	}
	
	private LevelCreatorUI ui = new LevelCreatorUI();
	
	private HashMap<String,SimpleButton> controlBar = new HashMap<String,SimpleButton>();
	private HashMap<String,SimpleButton> toolBar = new HashMap<String, SimpleButton>();
	private ArrayList<Vector2> vertices = new ArrayList<Vector2>();
	
	public GameInputProcessor(GameManager world, GameRenderer renderer){
		
		this.world = world;
		this.renderer = renderer;
		this.mouse = new Mouse();
		ui.load(toolBar,controlBar);
	}
	
	@Override
	public boolean touchDown (int x, int y, int pointer, int button) {
		
		if ( !world.isCreate() && !world.isRunning() )
			return false;  // pass event to next input processor
		
		// converting to physics coordinates
		float mousePos []  = toWorldCoordinates(x,y);
		mouse.posOnDrag = mousePos; // Gets rid of jumping when starting to pan the camera.
		y = GameConstants.HEIGHT - y;
		
		// index of game object touched down on
		int index = getObjectIndex(mousePos[0], mousePos[1]);  
		
		// if user is moving an object, notify game manager.
		if ( toolBar.get("MOVE").getClicked() && index != -1){
			world.pinObject(index, mousePos);
			return true;
		}
		
		if ( world.isCreate() && world.getBounds().containsPos(x,y) ){
		
			// clicked on a point
			if(index != -1){ 
				mouse.posDown = mousePos;
				
				Vector2 pointPos = world.getPoints().get(index).getBody().getPosition();
				mouse.indexDown = index;
				
				// if user is building spring/stick joints, notify renderer.
				if ( toolBar.get("SPRING").getClicked() || toolBar.get("STICK").getClicked() ){
					renderer.buildJoint(new float [] {pointPos.x, pointPos.y} );
					return true;
					
				// if user is building velocity/force vectors, notify renderer.
				} else if ( toolBar.get("VELOCITY").getClicked() || toolBar.get("FORCE").getClicked()){
					renderer.buildModifier(new float [] {pointPos.x, pointPos.y} );
					return true;	
				}
			}
			
			// didn't click on a point
			else { 
				
				// if user is building a circle, notify renderer
				if ( toolBar.get("CIRCLE").getClicked() ){
					renderer.buildCircle(mousePos);
					mouse.posDown = mousePos;
					return true;
					
				// if user is building a rectangle, notify renderer
				} else if ( toolBar.get("RECT").getClicked() ){
					renderer.buildRect(mousePos);
					mouse.posDown = mousePos;
					return true;
					
				// if user is building a curve, notify renderer
				} else if ( toolBar.get("CURVE").getClicked() ){
					renderer.buildPath(mousePos, vertices);
					mouse.posDown = mousePos;
					return true;
					
				// ----- Path builder
				} else if ( toolBar.get("PATH").getClicked() ){
					
					// notifies renderer to start rendering path builder
					if (vertices.size() == 0){
						renderer.buildPath(mousePos, vertices);
					} 
					
					// escape condition: create polygon if created a loop.
					if ( isALoop(vertices, mousePos) ){
						vertices.add(vertices.get(0).cpy());
						world.addPolygon(vertices, toolBar.get("PINNED").getClicked());
						vertices.clear();
						renderer.endBuilder();
						return true;
						
					// escape condition: create chain if clicked near previous point
					} else if ( distance(mousePos, mouse.posDown ) < 1 ){
						world.addChain(vertices);
						vertices.clear();
						renderer.endBuilder();
						return true;
						
					// else just add a point to the chain.
					} else {
						vertices.add(toVector2(mousePos));
						renderer.updateBuilder(mousePos);
						mouse.posDown = mousePos;
						return true;
					}

				} // ---- End Path builder
			} // ----- End whether or not clicked on point
		} 
		
		/*
		 * TOOLBAR
		 */
		if ( world.isCreate() ){
			for(SimpleButton toolButton : toolBar.values()){
				if( toolButton.isTouchDown(x, y) ) {
					return true; //stop checking for other input
				}
			}
		} else if ( world.isRunning() ){
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
		if( !world.isCreate() && !world.isRunning())
			return false;  // pass event to next input processor
		
		// converting to physics coordinates
		float mousePos [] = toWorldCoordinates(x,y);
		
		// measure from the bottom
		y = GameConstants.HEIGHT - y;
		
		// stop any building on touch up, unless building a path. 
		// the path maker will terminate the builder on its own.
		if ( !toolBar.get("PATH").getClicked() )
			renderer.endBuilder();

		if ( world.isCreate() && world.getBounds().containsPos(x,y) ){
			int index = getObjectIndex(mousePos[0], mousePos[1]);
			
			// did not release on a point. check for possibilities
			if (index == -1){
				
				// mouse was not pushed down on a point either. Check for object creations
				if ( mouse.indexDown == -1){
					
					// add circle to the game world.
					if ( toolBar.get("CIRCLE").getClicked() ){
						float radius = distance(mousePos, mouse.posDown);
						world.addCircle(mouse.posDown, radius, toolBar.get("PINNED").getClicked());
						mouse.indexDown = -1;						// release
						return true;								// stop checking for other actions
						
					// add rectangle to the game world.
					} else if ( toolBar.get("RECT").getClicked() ){
						world.addRectangle(mouse.posDown, mousePos, toolBar.get("PINNED").getClicked());
						mouse.indexDown = -1; 						// release
						return true; 								// stop checking for other actions
						
					// add a curve to the game world.
					} else if ( toolBar.get("CURVE").getClicked() ){
						world.addChain(vertices);
						vertices.clear();
						renderer.endBuilder();
						mouse.indexDown = -1;						// release
						return true;								// stop checking for other actions
					}
				}
				
				// mouse was pushed down on a point. Check for object modifications.
				if ( mouse.indexDown != -1){
					
					if ( toolBar.get("VELOCITY").getClicked() ){					
						world.addImpulse(mouse.indexDown, mousePos);
						mouse.indexDown = -1;						// release
						return true;								// stop checking for other actions
						
					} else if ( toolBar.get("FORCE").getClicked()){
						world.addForce(mouse.indexDown, mousePos);  // release
						mouse.indexDown = -1;						// stop checking for other actions
						return true;
					}
				}
			}
			
			// did release on a point
			else { 
			
				// releasing on a point that is different than the one touched down on
				if ( index != mouse.indexDown ){
					
					// notify the manager that conditions are met to build a spring
					if ( toolBar.get("SPRING").getClicked() ){
						world.addSpring(mouse.indexDown, index); 	// add spring to new point
						mouse.indexDown = -1;						// release
						return true;								// stop checking for other actions
						
					// notify the manager that conditions are met to build a spring
					} else if ( toolBar.get("STICK").getClicked() ){
						world.addSpring(mouse.indexDown, index); 	// add stick to new point
						mouse.indexDown = -1;						// release
						return true;								// stop checking for other actions
					} 
				}
			}
			
		}
		
		mouse.indexDown = -1; // release
		world.releaseObject();
		
		/*
		 * TOOLBAR
		 */
		if ( world.isCreate() ){
			// check if toolbar is hit. Label buttons as being pressed.
			for(Entry<String,SimpleButton> entry : toolBar.entrySet()){
				if(entry.getValue().isTouchUp(x, y)){
					deselectOtherButtons(entry.getKey());
					return true;
				}
			}
		} else if ( world.isRunning() ){
			if ( toolBar.get("MOVE").isTouchUp(x, y) ){
				deselectOtherButtons("MOVE");
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
	public boolean touchDragged(int x, int y, int pointer) {
		if(!world.isCreate() && !world.isRunning())
			return false;
		
		// convert to physics coordinates
		float mousePos [] = toWorldCoordinates(x,y);
		
		// saves mouse information for panning
		mouse.prevPos = mouse.posOnDrag;
		mouse.posOnDrag = mousePos;
		y = GameConstants.HEIGHT - y;
		
		// move points 
		world.updatePinner(mousePos);
		
		if ( world.isCreate() && world.getBounds().containsPos(x, y) ){

			// if creating an object, pass the current mouse position to the renderer.
			if ( renderer.isBuilding() ){
				renderer.updateBuilder(mousePos);
			}
			
			// index of game object currently dragging over
			int index = getObjectIndex(mousePos[0], mousePos[1]);  
			
			if (index == -1 && mouse.indexDown == -1){
				if ( toolBar.get("CURVE").getClicked() && distance(mouse.posDown, mousePos) > 0.5){
					vertices.add(toVector2(mousePos));
					renderer.updateBuilder(mousePos);
					mouse.posDown = mousePos;
				}
			}
			
			// if been dragging from a point and mouse is currently over another, different point
			if(index != mouse.indexDown && mouse.indexDown != -1 && index != -1){
				
				// get position of point currently dragging over to begin building a spring
				Vector2 pointPos = world.getPoints().get(index).getBody().getPosition();
				
				// hovered over another point. notify the renderer to stop and start rendering next spring.
				if( toolBar.get("SPRING").getClicked() ){
					world.addSpring(mouse.indexDown, index); 	// add spring to new point
					mouse.indexDown = index;					// begin building next spring
					renderer.endBuilder();
					renderer.buildJoint(new float [] {pointPos.x, pointPos.y} );
					return true;
					
				// hovered over another point. notify the renderer to stop and start rendering next stick.
				} else if ( toolBar.get("STICK").getClicked() ){
					world.addStick(mouse.indexDown, index); 	// add stick to new point
					mouse.indexDown = index;					// begin building next stick
					renderer.buildJoint(new float [] {pointPos.x, pointPos.y} );
					return true;
				}
			}
		}
		
		// pan camera
		if (!toolBar.get("MOVE").getClicked() && !renderer.isBuilding()){
			float dx = mouse.prevPos[0] - mousePos[0];
			float dy = mouse.prevPos[1] - mousePos[1];
			renderer.translateCamera(dx, dy);
			return true;
		}

		return false;
	}

	@Override
	public boolean mouseMoved(int x, int y) {
		if(!world.isCreate() && !world.isRunning())
			return false;
		
		float mousePos [] = toWorldCoordinates(x,y);
		
		if ( toolBar.get("PATH").getClicked()){
			renderer.updateBuilder(mousePos);
		}
		return true;
	}

	@Override
	public boolean scrolled(int amount) {
		if(!world.isCreate() && !world.isRunning())
			return false;
		renderer.zoomCamera(amount);
		// TODO Auto-generated method stub
		return true;
	}
	/*
	 * Helpers
	 */
	
	private int getObjectIndex(float x, float y){
		ArrayList<IGameObject> points = world.getPoints();
		for(IGameObject p : points){
			if(p.containsPos(x,y)){
				return points.indexOf(p);
			}
		}
		return -1;
	}
	
	private boolean isALoop(ArrayList<Vector2> vertices, float [] newPoint){
		return (vertices.size() > 2 && distance(new float [] {vertices.get(0).x, vertices.get(0).y}, newPoint) < 1 );
	}
	
	private float distance( float [] pos1, float [] pos2){
		float distance = (pos2[0] - pos1[0])*(pos2[0] - pos1[0]) + (pos2[1] - pos1[1])*(pos2[1] - pos1[1]);
		return (float)(Math.sqrt(distance));
	}
	
	private Vector2 toVector2( float [] vec) {
		return new Vector2( vec[0], vec[1]);
	}
	
	private void deselectOtherButtons(String key){
		if ( key == "PINNED" || key == "HIDDEN") return;
		for(Entry<String,SimpleButton> otherButton : toolBar.entrySet()){
			if (otherButton.getKey() != key && otherButton.getKey() != "PINNED" && otherButton.getKey() != "HIDDEN")
				otherButton.getValue().release();
		}

	}
	
	private float [] toWorldCoordinates(int x, int y){
		Vector3 coords = renderer.getWorldCam().unproject(new Vector3(x,y,0));
		return new float [] {coords.x, coords.y};
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
	
	public ArrayList<Vector2> getChainQueue(){
		return vertices;
	}	
}
