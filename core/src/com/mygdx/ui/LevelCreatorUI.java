package com.mygdx.ui;

import java.util.HashMap;

import com.mygdx.GameWorld.GameConstants;
import com.mygdx.managers.AssetLoader;

public class LevelCreatorUI {
	
	public void load(HashMap<String,SimpleButton> toolBar, HashMap<String,SimpleButton> controlBar, HashMap<String,SimpleButton> modifierBar) {
		
		//move tool button
		toolBar.put("MOVE", new SimpleButton(
				GameConstants.LC_PADDING, GameConstants.LC_PADDING, 
				GameConstants.LC_WIDTH, GameConstants.LC_HEIGHT, 
				AssetLoader.moveButtonUp,AssetLoader.moveButtonDown));
		
		//circle/disc
		toolBar.put("CIRCLE", new SimpleButton(
				2*GameConstants.LC_PADDING + GameConstants.LC_WIDTH, GameConstants.LC_PADDING, 
				GameConstants.LC_WIDTH, GameConstants.LC_HEIGHT, 
				AssetLoader.circleButtonUp,AssetLoader.circleButtonDown));
		
		//rectangle
		toolBar.put("RECTANGLE", new SimpleButton(
				3*GameConstants.LC_PADDING + 2*GameConstants.LC_WIDTH, GameConstants.LC_PADDING, 
				GameConstants.LC_WIDTH, GameConstants.LC_HEIGHT, 
				AssetLoader.rectangleButtonUp,AssetLoader.rectangleButtonDown));
		
		// line
		toolBar.put("PATH", new SimpleButton(
				4*GameConstants.LC_PADDING + 3*GameConstants.LC_WIDTH, GameConstants.LC_PADDING, 
				GameConstants.LC_WIDTH, GameConstants.LC_HEIGHT, 
				AssetLoader.pathButtonUp,AssetLoader.pathButtonDown));
		
		//curve tool
		toolBar.put("CURVE", new SimpleButton(
				5*GameConstants.LC_PADDING + 4*GameConstants.LC_WIDTH, GameConstants.LC_PADDING, 
				GameConstants.LC_WIDTH, GameConstants.LC_HEIGHT,  
				AssetLoader.curveButtonUp,AssetLoader.curveButtonDown));
		
		//spring tool
		toolBar.put("SPRING", new SimpleButton(
				6*GameConstants.LC_PADDING + 5*GameConstants.LC_WIDTH, GameConstants.LC_PADDING, 
				GameConstants.LC_WIDTH, GameConstants.LC_HEIGHT, 
				AssetLoader.springButtonUp,AssetLoader.springButtonDown));
		
		//stick tool
		toolBar.put("STICK", new SimpleButton(
				7*GameConstants.LC_PADDING + 6*GameConstants.LC_WIDTH, GameConstants.LC_PADDING,
				GameConstants.LC_WIDTH, GameConstants.LC_HEIGHT,  
				AssetLoader.stickButtonUp,AssetLoader.stickButtonDown));
		
		// velocity modifier
		toolBar.put("VELOCITY", new SimpleButton(
				8*GameConstants.LC_PADDING + 7*GameConstants.LC_WIDTH, GameConstants.LC_PADDING, 
				GameConstants.LC_WIDTH, GameConstants.LC_HEIGHT, 
				AssetLoader.velocityButtonUp,AssetLoader.velocityButtonDown));
		
		// force modifier
		toolBar.put("FORCE", new SimpleButton(
				9*GameConstants.LC_PADDING + 8*GameConstants.LC_WIDTH, GameConstants.LC_PADDING, 
				GameConstants.LC_WIDTH, GameConstants.LC_HEIGHT, 
				AssetLoader.forceButtonUp,AssetLoader.forceButtonDown));
		
		//pinned tool button
		modifierBar.put("PINNED", new SimpleButton(
				10*GameConstants.LC_PADDING + 9*GameConstants.LC_WIDTH, GameConstants.LC_PADDING, 
				GameConstants.LC_WIDTH, GameConstants.LC_HEIGHT, 
				AssetLoader.pinnedButtonUp,AssetLoader.pinnedButtonDown));
		
		//hidden support
		modifierBar.put("HIDDEN", new SimpleButton(
				11*GameConstants.LC_PADDING + 10*GameConstants.LC_WIDTH, GameConstants.LC_PADDING, 
				GameConstants.LC_WIDTH, GameConstants.LC_HEIGHT, 
				AssetLoader.hiddenButtonUp,AssetLoader.hiddenButtonDown));
		
		//regional Modifier support
		toolBar.put("FIELD", new SimpleButton(
				12*GameConstants.LC_PADDING + 11*GameConstants.LC_WIDTH, GameConstants.LC_PADDING, 
				GameConstants.LC_WIDTH, GameConstants.LC_HEIGHT, 
				AssetLoader.rectangleButtonUp,AssetLoader.rectangleButtonDown));
				
		//play/pause button (start it off showing play button)
		controlBar.put("RUN", new SimpleButton(
				GameConstants.WIDTH - GameConstants.LC_WIDTH - GameConstants.LC_PADDING, GameConstants.LC_PADDING, 
				GameConstants.LC_WIDTH, GameConstants.LC_HEIGHT, 
				AssetLoader.playButtonUp,AssetLoader.playButtonDown));
		
		//clear screen button
		controlBar.put("RESTART", new SimpleButton(
				GameConstants.WIDTH - 2*GameConstants.LC_WIDTH - 2*GameConstants.LC_PADDING, GameConstants.LC_PADDING, 
				GameConstants.LC_WIDTH, GameConstants.LC_HEIGHT, 
				AssetLoader.restartButtonUp,AssetLoader.restartButtonDown));
		
		//save button
		controlBar.put("SAVE", new SimpleButton(
				GameConstants.WIDTH - 3*GameConstants.LC_WIDTH - 3*GameConstants.LC_PADDING, GameConstants.LC_PADDING, 
				GameConstants.LC_WIDTH, GameConstants.LC_HEIGHT, 
				AssetLoader.restartButtonUp,AssetLoader.restartButtonDown));
		
		//save button
		controlBar.put("LOAD", new SimpleButton(
				GameConstants.WIDTH - 4*GameConstants.LC_WIDTH - 4*GameConstants.LC_PADDING, GameConstants.LC_PADDING, 
				GameConstants.LC_WIDTH, GameConstants.LC_HEIGHT, 
				AssetLoader.restartButtonUp,AssetLoader.restartButtonDown));
	}

}
