package com.mygdx.managers;

import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.Entities.GameObjects.Circle;
import com.mygdx.Entities.GameObjects.IGameObject;
import com.mygdx.Entities.GameObjects.Rectangle;
import com.mygdx.Entities.Joints.IJoint;
import com.mygdx.Entities.Modifiers.Field;
import com.mygdx.Entities.Modifiers.IModifier;
import com.mygdx.GameWorld.GameManager;
import com.mygdx.game.shared.circlePacket;
import com.mygdx.game.shared.rectanglePacket;

public class Encoder {
	
	private XMLEncoder xmlEncoder;
	private ArrayList<IJoint> joints;
	private ArrayList<IGameObject> points;
	private ArrayList<IModifier> modifiers;
	private ArrayList<Field> fields;
	
	public Encoder() throws FileNotFoundException{
		xmlEncoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream("test.xml")));
	}
	
	public void Encode(){
		for(IGameObject point : points){
			Object packet = point.getPacket();
			xmlEncoder.writeObject(packet);
		}
	}
	
	public Object serializeGameObject(IGameObject gameObject){
		if ( gameObject instanceof Circle){
			return new circlePacket(new float [] {gameObject.getBody().getPosition().x,gameObject.getBody().getPosition().y},
						gameObject.getHeight(),false);
		}
		else if ( gameObject instanceof Rectangle){
			Body body = gameObject.getBody();
			return new rectanglePacket(body.getPosition().x,body.getPosition().y,
					gameObject.getWidth(),gameObject.getHeight(),true);
		}
		else return null;
	}
	
	public void load(GameManager gameManager){
		joints = gameManager.getJoints();
		points = gameManager.getPoints();
		modifiers = gameManager.getModifiers();
		fields = gameManager.getFields();
	}
	
	public void dispose(){
		xmlEncoder.close();
	}

}
