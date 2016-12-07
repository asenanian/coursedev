package com.mygdx.Renderer;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.GameWorld.GameConstants;

class Builder {
	private float beginX, beginY;
	private float endX, endY;
	private ArrayList<Vector2> path;
	private BitmapFont font;
	
	public Builder(){
		font = new BitmapFont();
	}
	
	public void init( float x, float y){
		this.beginX = x;
		this.beginY = y;
		this.endX = x;
		this.endY = y;
	}
	
	public void init(float x, float y, ArrayList<Vector2> path){
		this.beginX = x;
		this.beginY = y;
		this.endX = x;
		this.endY = y;
		this.path = path;
	}
	
	public void update( float x, float y){
		this.endX = x;
		this.endY = y;
	}
	
	public void dispose(){
		path = null;
	}

	public void drawCircle(ShapeRenderer renderer){
		float radius = (float) Math.sqrt(Math.pow(endX - beginX,2) + Math.pow(endY - beginY,2));

		renderer.set(ShapeType.Line);
		renderer.setColor(Color.RED);
		renderer.circle(beginX,beginY,radius,24);
	}
	
	public void drawRectangle(ShapeRenderer renderer){
		renderer.set(ShapeType.Line);
		renderer.setColor(Color.RED);
		renderer.rect(beginX,	beginY, 
				endX - beginX, endY - beginY);		
	}
	
	public void drawModifier(ShapeRenderer renderer){
		
		Vector2 pos1 = new Vector2(beginX, beginY);
		Vector2 pos2 = new Vector2(endX, endY);
		Vector2 dpos = new Vector2(endX - beginX, endY - beginY);
		Vector2 triPoint = pos2.cpy().sub(pos1).setLength(GameConstants.MODIFIER_WIDTH*2);
		
		float arcSize = dpos.len()/3 > 5 ? dpos.len()/3 : 5;
		
		renderer.set(ShapeType.Line);
		renderer.setColor(Color.WHITE);
		
		if ( pos2.x > pos1.x  && pos2.y > pos1.y ){
			renderer.arc(pos1.x, pos1.y, arcSize, 0, dpos.angle(),24);
		} else if ( pos2.x > pos1.x && pos2.y < pos1.y ){
			renderer.arc(pos1.x, pos1.y, arcSize, dpos.angle() , 360-dpos.angle(), 24);
		} else if ( pos2.x < pos1.x ){
			renderer.arc(pos1.x, pos1.y, arcSize, 180,  (dpos.angle() - 180), 24);
		}

		renderer.setColor(Color.BLACK);
		renderer.set(ShapeType.Line);
		renderer.rectLine(pos1.x , pos1.y, pos2.x, pos2.y, GameConstants.MODIFIER_WIDTH);
		renderer.triangle(
				pos2.x + triPoint.cpy().rotate90(1).x, 
				pos2.y + triPoint.cpy().rotate90(1).y, 
				pos2.x + triPoint.cpy().rotate90(-1).x, 
				pos2.y + triPoint.cpy().rotate90(-1).y, 
				pos2.x + triPoint.x, 
				pos2.y + triPoint.y);

	}
	
	public void drawModifierText(SpriteBatch batcher, OrthographicCamera cam){
		
		Vector2 pos1 = new Vector2(beginX, beginY);
		Vector2 pos2 = new Vector2(endX, endY);
		Vector2 dpos = new Vector2(endX - beginX, endY - beginY);
		
		Vector3 coords1 = cam.project(new Vector3(pos1.x,pos1.y,0));
		Vector3 coords2 = cam.project(new Vector3(pos2.x,pos2.y,0));
		
		font.setColor(Color.WHITE);
		font.getData().setScale(2f);
		font.draw(batcher, String.format("%.1f °", dpos.angle()), coords2.x, .5f*(coords2.y + coords1.y)  );
		font.draw(batcher, String.format("%.1f N", dpos.len()*GameConstants.MODIFIER_SCL), coords2.x  - 60, coords2.y + 60 );

	}
	
	public void drawJoint(ShapeRenderer renderer){
		
		renderer.set(ShapeType.Filled);
		renderer.setColor(Color.RED);
		renderer.rectLine( beginX, beginY, endX, endY, GameConstants.SPRING_WIDTH);
	}
	
	public void drawPath(ShapeRenderer renderer){
		if(path.size() < 1) return;

		renderer.set(ShapeType.Filled);
		renderer.setColor(Color.BLACK);
		
		for(int i = 0; i < path.size() - 1; i++){
			renderer.rectLine(path.get(i),path.get(i+1),GameConstants.MODIFIER_WIDTH);
		}
		renderer.set(ShapeType.Line);
		renderer.setColor(Color.RED);
		renderer.rectLine(path.get(path.size() - 1),
				new Vector2(endX, endY),
				GameConstants.MODIFIER_WIDTH);
	}
}
