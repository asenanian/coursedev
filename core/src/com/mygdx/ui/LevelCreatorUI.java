package com.mygdx.ui;

import java.util.HashMap;

import com.mygdx.GameWorld.GameConstants;
import com.mygdx.managers.AssetLoader;

public class LevelCreatorUI {
	
	public void load(HashMap<String,SimpleButton> toolBar, HashMap<String,SimpleButton> controlBar) {
		
		//move tool button
		toolBar.put("MOVE", new SimpleButton(
				GameConstants.B_PADDING, GameConstants.B_PADDING, 
				GameConstants.B_WIDTH, GameConstants.B_HEIGHT, 
				AssetLoader.moveButtonUp,AssetLoader.moveButtonDown));
		
		//circle/disc
		toolBar.put("CIRCLE", new SimpleButton(
				2*GameConstants.B_PADDING + GameConstants.B_WIDTH, GameConstants.B_PADDING, 
				GameConstants.B_WIDTH, GameConstants.B_HEIGHT, 
				AssetLoader.circleButtonUp,AssetLoader.circleButtonDown));
		
		//spring tool
		toolBar.put("SPRING", new SimpleButton(
				3*GameConstants.B_PADDING + 2*GameConstants.B_WIDTH, GameConstants.B_PADDING, 
				GameConstants.B_WIDTH, GameConstants.B_HEIGHT, 
				AssetLoader.springButtonUp,AssetLoader.springButtonDown));
		
		//curve tool
		toolBar.put("CURVE", new SimpleButton(
				4*GameConstants.B_PADDING + 3*GameConstants.B_WIDTH, GameConstants.B_PADDING, 
				GameConstants.B_WIDTH, GameConstants.B_HEIGHT,  
				AssetLoader.curveButtonUp,AssetLoader.curveButtonDown));
		
		//stick tool
		toolBar.put("STICK", new SimpleButton(
				5*GameConstants.B_PADDING + 4*GameConstants.B_WIDTH, GameConstants.B_PADDING,
				GameConstants.B_WIDTH, GameConstants.B_HEIGHT,  
				AssetLoader.stickButtonUp,AssetLoader.stickButtonDown));
		
		//pinned tool button
		toolBar.put("PINNED", new SimpleButton(
				6*GameConstants.B_PADDING + 5*GameConstants.B_WIDTH, GameConstants.B_PADDING, 
				GameConstants.B_WIDTH, GameConstants.B_HEIGHT, 
				AssetLoader.pinnedButtonUp,AssetLoader.pinnedButtonDown));
		
		//hidden support
		toolBar.put("HIDDEN", new SimpleButton(
				7*GameConstants.B_PADDING + 6*GameConstants.B_WIDTH, GameConstants.B_PADDING, 
				GameConstants.B_WIDTH, GameConstants.B_HEIGHT, 
				AssetLoader.hiddenButtonUp,AssetLoader.hiddenButtonDown));
				
		//play/pause button (start it off showing play button)
		controlBar.put("RUN", new SimpleButton(
				GameConstants.WIDTH - GameConstants.B_WIDTH - GameConstants.B_PADDING, GameConstants.B_PADDING, 
				GameConstants.B_WIDTH, GameConstants.B_HEIGHT, 
				AssetLoader.playButtonUp,AssetLoader.playButtonDown));
		
		//clear screen button
		controlBar.put("RESTART", new SimpleButton(
				GameConstants.WIDTH - 2*GameConstants.B_WIDTH - 2*GameConstants.B_PADDING, GameConstants.B_PADDING, 
				GameConstants.B_WIDTH, GameConstants.B_HEIGHT, 
				AssetLoader.restartButtonUp,AssetLoader.restartButtonDown));
	}

}
