package com.mygdx.Entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.GameWorld.GameConstants;

public class Spring {
	
	private float equilibriumLength;
	private int p1Index, p2Index;
	private float k; //spring constant
	private boolean hidden; //if spring is hidden or not
	private boolean rigid; //if spring is springy or rigid
	private Color color;
	
	public Spring(ArrayList<Point> points, int p1Index, int p2Index, float k, boolean hidden) {	
		this.equilibriumLength = distance(points.get(p1Index).getPos(),points.get(p2Index).getPos());
		this.p1Index = p1Index;
		this.p2Index = p2Index;
		this.k = k;
		this.hidden = hidden;
		this.rigid = this.k > 10f;
		this.color = (this.rigid ? Color.BLUE : Color.RED);
	}
	
	private float distance(float [] pos1, float [] pos2){
		return (float)Math.sqrt(Math.pow(pos1[0]-pos2[0],2) + 
				Math.pow(pos1[1]-pos2[1],2));
	}
	
	private float[] getRestoringAccels(Point p1, Point p2, float displacement,float dt) {
		float force = k*displacement*dt;
		
		float accel1 = force / p1.getMass();
		float accel2 = force / p2.getMass();
		
		return new float[] {accel1, -accel2};
	}
	
	public void draw(ArrayList<Point> points, ShapeRenderer shapeRenderer) {
		if (!this.hidden) {
			float[] pos1 = points.get(p1Index).getPos();
			float[] pos2 = points.get(p2Index).getPos();
			
			shapeRenderer.setColor(this.color);
			shapeRenderer.rectLine(pos1[0], pos1[1], pos2[0], pos2[1], GameConstants.SPRING_WIDTH);
		}
	}
	
	public void update(ArrayList<Point> points, float dt) {
		//for updating spring force or stick algorithm (only for 2D)
		Point p1 = points.get(p1Index);
		Point p2 = points.get(p2Index);
		float dx = p2.getPos()[0] - p1.getPos()[0];
		float dy = p2.getPos()[1] - p1.getPos()[1];
		
		float dist = (float)Math.sqrt(dx * dx + dy * dy); //dist bt points
		float diff = dist - equilibriumLength;
		//print("It is quite " + this.rigid + " that this spring is rigid.");
		if (this.rigid) {
			//for rigid spring
			if (diff > 0) {
				float percent = diff / dist / 2;
				float offsetX = dx * percent;
				float offsetY = dy * percent;
				p1.addXY(offsetX, offsetY);
				p2.addXY(-offsetX, -offsetY);
			}
		} else {
			//for springy (low k) spring
			float[] restoringAccels = this.getRestoringAccels(p1,p2, diff,dt);
			p1.addVect2D(restoringAccels[0],p1,p2);
			p2.addVect2D(restoringAccels[1],p1,p2);
		}
	}
	
}
