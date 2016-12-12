package com.mygdx.XMLService;

import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import com.mygdx.Entities.GameObjects.IGameObject;
import com.mygdx.Entities.Joints.IJoint;
import com.mygdx.Entities.Modifiers.Field;
import com.mygdx.Entities.Modifiers.IModifier;
import com.mygdx.GameWorld.GameManager;

public class XMLSerializer {
	
	private XMLEncoder xmlEncoder;
	private ArrayList<IJoint> joints;
	private ArrayList<IGameObject> points;
	private ArrayList<IModifier> modifiers;
	private ArrayList<Field> fields;
	
	public XMLSerializer() throws FileNotFoundException{
		xmlEncoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream("test.xml")));
	}
	
	public void serialize (){
	
		for(IGameObject point : points){
			Serializable bean = point.getBean();
			xmlEncoder.writeObject(bean);
		}
		for (IJoint joint : joints){
			Serializable bean = joint.getBean();
			xmlEncoder.writeObject(bean);
		}
	}
	
	public void loadGameEntities(GameManager gameManager){
		joints = gameManager.getJoints();
		points = gameManager.getPoints();
		modifiers = gameManager.getModifiers();
		fields = gameManager.getFields();
	}
	
	public void dispose(){
		xmlEncoder.close();
	}

}
