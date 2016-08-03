package com.mygdx.managers;

import java.util.ArrayList;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.mygdx.entities.Button;
import com.mygdx.entities.FileChooserWindow;
import com.mygdx.entities.Image;
import com.mygdx.entities.MultiDraw;
import com.mygdx.entities.Text;
import com.mygdx.entities.TrashCan;
import com.mygdx.game.MyGdxGame;
import com.mygdx.gamestates.PlayState;

public class FileChooserManager implements MultiDraw {
	private float[] rectBounds;
	private BitmapFont font;
	private FileHandle[] files;
	private String internalPath;
	private Text[] fileTexts;
	private FileChooserWindow fileChooserWindow;
	private boolean drawingWindow;
	private Image chosenImage;
	private boolean showTextRects;
	private Button backButton;
	private TrashCan trashCan;

	public FileChooserManager() {
		//call init later...
		this.drawingWindow = false;
		this.chosenImage = null;
		this.showTextRects = true;
	}
	public void init(String internalPath, BitmapFont font, float[] windowBounds) {
		this.internalPath = internalPath;
		this.font = font;
		this.rectBounds = windowBounds;
		this.fileChooserWindow = new FileChooserWindow(this.rectBounds);
		updateFiles();
		this.backButton = new Button("Verlet/Tools/back.png",rectBounds[0]+rectBounds[2]-30,
										rectBounds[1]+rectBounds[3]-30f,60,60);
		this.trashCan = new TrashCan(PlayState.WIDTH-50f,50f,50f,50f);
		
	}
	public void updateFileTexts() {
		this.fileTexts = new Text[files.length];
		float x,y;
		int space = 0;
		for (int i = 0; i < files.length; i++) {
			String name = files[i].name();//WithoutExtension();
			fileTexts[i] = new Text(name,this.font);
			float[] returnArray = getNextTextPos(i,space);
			x = returnArray[0];
			y = returnArray[1];
	    	fileTexts[i].setCenter(x, y);
	    	if (returnArray[2] != -1) space++;
	    	space++;
		}
	}
	public float[] getNextTextPos(int i, int space) {
		//used in placing filenames in filechooserWindow
		float[] returnArray = new float[3];
		returnArray[2] = -1; //default
		int nCols = fileChooserWindow.getNCols();
		int nRows = fileChooserWindow.getNRows();
		float winX = this.rectBounds[0], winY = this.rectBounds[1];
		float winW = this.rectBounds[2], winH = this.rectBounds[3];
		returnArray[0] = winX + winW/nCols + winW/nCols * (space % (nCols-1));
    	returnArray[1] = winY + winH - winH/nRows/2 - (winH/nRows)*(int)(space/(nCols-1));
    	if (i == 0) {
			return returnArray;
		} else {
			Text thisText = fileTexts[i];
			Text prevText = fileTexts[i-1];
			if (prevText.getRight() > thisText.getLeft()) {
				//move long texts forward one space
				int newSpace = space+1;
				returnArray[0] = winX + winW/nCols + winW/nCols * (newSpace % (nCols-1));
		    	returnArray[1] = winY + winH - winH/nRows/2 - (winH/nRows)*(int)(newSpace/(nCols-1));
		    	returnArray[2] = 0;//cause skip in for loop where this func is called
			}
		}
		return returnArray;
	}
	public void updateFileHandles() {
		FileHandle dir;
		if (Gdx.app.getType() == ApplicationType.Android) {
			dir = Gdx.files.internal(this.internalPath);
		} else {
			// ApplicationType.Desktop ...
			dir = Gdx.files.internal("./bin/"+this.internalPath);
		}
		FileHandle[] children = dir.list();
		this.files = children;
	}
	public boolean getActive() {
		return drawingWindow;
	}
	public void show() {
		drawingWindow = true;
	}
	public void hide() {
		drawingWindow = false;
	}
	
	public void resetChosenImage() {
		this.chosenImage = null;
	}
	public Image getChosenImage() {
		return chosenImage;
	}
	public String getChosenImageName() {
		if (chosenImage == null) return null;
		return chosenImage.getName();
	}
	public String getChosenImagePath() {
		if (chosenImage == null) return null;
		return chosenImage.getInternalPath();
	}
	public void drawChosenImageSprites(SpriteBatch batch) {
		trashCan.drawSprites(batch);
		//chosenImage shouldn't be null
		chosenImage.drawSprites(batch);
		//print("drawing chosenImage");
	}
	public void drawChosenImageShapes(ShapeRenderer shapeRenderer) {
		trashCan.drawShapes(shapeRenderer);
		chosenImage.drawShapes(shapeRenderer);
	}
	public void drawWindow(ShapeRenderer shapeRenderer) {
		this.fileChooserWindow.draw(shapeRenderer);
	}
	public void drawText(SpriteBatch batch) {
		//print("LEN FILES = "+files.length);
		for (int i = 0; i < files.length; i++) {
    		fileTexts[i].drawSprites(batch);
		}
	}
	public void drawTextRects(ShapeRenderer shapeRenderer) {
		shapeRenderer.set(ShapeType.Line);
		shapeRenderer.setColor(Color.BLACK);
		for (int i = 0; i < files.length; i++) {
    		fileTexts[i].drawShapes(shapeRenderer);
		}
	}
	@Override
	public void drawSprites(SpriteBatch batch) {
		if (drawingWindow) {
			//print("drawing text");
			drawText(batch);
			backButton.drawSprites(batch);
		}
		if (chosenImage != null) {
			drawChosenImageSprites(batch);
		}
	}
	@Override
	public void drawShapes(ShapeRenderer shapeRenderer) {
		if (drawingWindow) {
			this.drawWindow(shapeRenderer);
			if (showTextRects) {
				this.drawTextRects(shapeRenderer);
			}
			backButton.drawShapes(shapeRenderer);
		}
		if (chosenImage != null) {
			drawChosenImageShapes(shapeRenderer);
		}
	}
	
	public int clickedOnFileName(float[] mousePos) {
		//return index of item in items array (or -1 if false)
		if (Gdx.input.isTouched() && drawingWindow) {
			//user clicked/touched screen
			for (int i = 0; i < files.length; i++) {
				if (fileTexts[i].containsPos(mousePos))  {
					return i;
				}
			}
		}
		return -1;
	}
	
	public void print(Object obj) {
		System.out.println(obj);
	}
	
	public void updateChosenImage(float[] mousePos,boolean clicked) {
		if (chosenImage != null) { //clicked a file name recently (a few frames ago)
			//pin center of clicked file image to mousePos
			chosenImage.setCenter(mousePos);
			if (trashCan.containsPos(mousePos) && clicked) {
				//reset chosen image if placed in trash can
				resetChosenImage();
			}
		}
		
	}
	public boolean updateClicks(float[] mousePos) {
		//not dragging chosen image yet
		if (fileChooserWindow.containsPos(mousePos)) {
			if (!drawingWindow) return false;
		} else { //didn't click on file chooser window
			return false;
		}
		
		int ind = clickedOnFileName(mousePos);
		//print("IND");
		if (ind != -1 && chosenImage == null) {
			//update internalPath or create chosen image
			//clicked on a filename at index ind
			if (files[ind].isDirectory()) {
				print("ADDING DIRECTORY "+files[ind].name()+" to "+this.internalPath);
				this.internalPath += "/"+files[ind].name();
				//print(this.internalPath);
				//print(Gdx.files.internal(this.internalPath).list());
				updateFiles();
			} else {
				//user selected image
				print("adding file "+fileTexts[ind].getName()+" to "+this.internalPath);
				String imagePath = this.internalPath+"/"+fileTexts[ind].getName();
				chosenImage = new Image(imagePath,mousePos,60,60);
				
				drawingWindow = false;
			}
		} else {
			print("TRYING TO CHECK BACK");
			//check if clicked back button
			if (backButton.containsPos(mousePos)) {
				int lastSlashInd = internalPath.lastIndexOf("/");
				if (lastSlashInd != -1) {
					//if possible to go back a directory...
					this.internalPath = internalPath.substring(0,lastSlashInd);
					print("NEW PATH = " + this.internalPath);
					updateFiles();
				}
			}
		}
		return true;
	}
	public void updateFiles() {
		updateFileHandles();
		updateFileTexts();
	}
	public float[] getMousePos() {
		//invert y value to make bottom y = 0, not height
		return new float[] {Gdx.input.getX(), PlayState.HEIGHT-Gdx.input.getY()};
	}
	public String[] getFileNames() {
		String[] fileNames = new String[fileTexts.length];
		for (int i = 0; i < fileTexts.length; i++) {
			fileNames[i] = fileTexts[i].getName();
		}
		return fileNames;
	}
}
