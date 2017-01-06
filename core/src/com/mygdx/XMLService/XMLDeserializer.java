package com.mygdx.XMLService;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

import com.mygdx.Entities.GameObjects.Chain;
import com.mygdx.Entities.GameObjects.Circle;
import com.mygdx.Entities.GameObjects.Field;
import com.mygdx.Entities.GameObjects.PolyBody;
import com.mygdx.Entities.GameObjects.Rectangle;
import com.mygdx.Entities.Joints.Spring;
import com.mygdx.Entities.Joints.Stick;
import com.mygdx.Entities.Modifiers.Force;
import com.mygdx.Entities.Modifiers.Velocity;
import com.mygdx.GameWorld.GameManager;
import com.mygdx.XMLService.Beans.*;

public class XMLDeserializer {
	
	private HashMap<String,Loader> loaders;
	
	private XMLDecoder decoder;
	
	public XMLDeserializer() throws FileNotFoundException{
		decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream("test.xml")));
		
		loaders = new HashMap<String,Loader>();
		
		loaders.put("CIRCLE", new Loader(){
			public void loadEntity(GameManager manager, Object object)
			{ manager.addGameObject(new Circle((CircleBean) object)); }
		});
		
		loaders.put("RECTANGLE", new Loader(){
			public void loadEntity(GameManager manager, Object object)
			{ manager.addGameObject(new Rectangle((RectangleBean) object));	}
		});
		
		loaders.put("CHAIN", new Loader(){
			public void loadEntity(GameManager manager, Object object)
			{ manager.addGameObject(new Chain((ChainBean) object));	}
		});
		
		loaders.put("POLYGON", new Loader(){
			public void loadEntity(GameManager manager, Object object)
			{ manager.addGameObject(new PolyBody((PolygonBean) object)); }
		});
		
		loaders.put("SPRING", new Loader(){
			public void loadEntity(GameManager manager, Object object)
			{ manager.addJoint(new Spring((SpringBean) object,manager)); }
		});
		
		loaders.put("STICK", new Loader(){
			public void loadEntity(GameManager manager, Object object)
			{ manager.addJoint(new Stick((StickBean) object,manager)); }
		});
		
		loaders.put("FIELD", new Loader(){
			public void loadEntity(GameManager manager, Object object)
			{ manager.addField(new Field((FieldBean) object)); }
		});
		
		loaders.put("FORCE", new Loader(){
			public void loadEntity(GameManager manager, Object object)
			{ manager.addModifier(new Force((ForceBean) object, manager)); }
		});
		
		loaders.put("VELOCITY", new Loader(){
			public void loadEntity(GameManager manager, Object object)
			{ manager.addModifier(new Velocity((VelocityBean) object, manager)); }
		});
		
	}
	
	public void deserialize (GameManager manager){
		Object object;
		while(true){
			try{ object = decoder.readObject(); }
			catch(ArrayIndexOutOfBoundsException be)
			{ break; }
			
			String name = object.toString();
			loaders.get(name).loadEntity(manager, object);
		}
	}
	
	public void dispose(){
		decoder.close();
	}

}
