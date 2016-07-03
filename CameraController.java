package com.mygdx.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;

public class CameraController extends CameraInputController {
	private Vector3 tmpV1 = new Vector3();
	private Vector3 tmpV2 = new Vector3();
	public CameraController(final Camera camera) {
		super(camera);
	}
	@Override
	public boolean zoom (float amount) {
		if (!alwaysScroll && activateKey != 0 && !activatePressed) return false;
		camera.translate(tmpV1.set(camera.direction).scl(amount));
		if (scrollTarget) target.add(tmpV1);
		if (autoUpdate) camera.update();
		return true;
	}
	
	@Override
	public void update () {
		if (rotateRightPressed || rotateLeftPressed || forwardPressed || backwardPressed) {
			final float delta = Gdx.graphics.getDeltaTime();
			if (rotateRightPressed) camera.rotate(camera.up, -delta * rotateAngle);
			if (rotateLeftPressed) camera.rotate(camera.up, delta * rotateAngle);
			if (forwardPressed) {
				camera.translate(tmpV1.set(camera.direction).scl(delta * translateUnits));
				if (forwardTarget) target.add(tmpV1);
			}
			if (backwardPressed) {
				camera.translate(tmpV1.set(camera.direction).scl(-delta * translateUnits));
				if (forwardTarget) target.add(tmpV1);
			}
			if (autoUpdate) camera.update();
		}
	}
	
	@Override
	protected boolean process (float deltaX, float deltaY, int button) {
		if (button == rotateButton) {
			tmpV1.set(camera.direction).crs(camera.up).y = 0f;
			
			//SWITCHED NEGATIVE SIGN ON LINES 47 
			camera.rotateAround(target, tmpV1.nor(), deltaY * rotateAngle);
			camera.rotateAround(target, Vector3.Y, deltaX * rotateAngle);
			
		} else if (button == translateButton) {
			camera.translate(tmpV1.set(camera.direction).crs(camera.up).nor().scl(-deltaX * translateUnits));
			camera.translate(tmpV2.set(camera.up).scl(-deltaY * translateUnits));
			if (translateTarget) target.add(tmpV1).add(tmpV2);
		} else if (button == forwardButton) {
			camera.translate(tmpV1.set(camera.direction).scl(deltaY * translateUnits));
			if (forwardTarget) target.add(tmpV1);
		}
		if (autoUpdate) camera.update();
		return true;
	}
}
