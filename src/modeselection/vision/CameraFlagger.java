package modeselection.vision;

import java.io.IOException;
import java.util.ArrayList;

import lejos.hardware.BrickFinder;
import lejos.hardware.video.Video;
import modeselection.ConditionCounted;
import modeselection.Flagger;
import modeselection.SensedValues;
import modeselection.util.Logger;

public class CameraFlagger<C extends Enum<C>> implements Flagger<C> {

	public static final int WIDTH = 160, HEIGHT = 120;
	
	private Video camera;
	private byte[] frame;
	private ArrayList<SubFlagger<C>> subs = new ArrayList<>();
	
	public CameraFlagger() throws IOException {
		camera = BrickFinder.getDefault().getVideo();
		camera.open(WIDTH, HEIGHT);
		frame = camera.createFrame();
	}
	
	public void addSub(SubFlagger<C> flagger) {
		subs.add(flagger);
	}
	
	@Override
	public void update(SensedValues<C> conditions) {
		try {
			camera.grabFrame(frame);
			AdaptedYUYVImage img = new AdaptedYUYVImage(frame, WIDTH, HEIGHT);
			for (SubFlagger<C> sub: subs) {
				sub.update(img, conditions);
			}
			conditions.setRawImage(img);
		} catch (IOException e) {
			throw new IllegalStateException("Camera trouble");
		}		
	}
	
	@Override
	public void log(Logger logger) {
		for (SubFlagger<C> sub: subs) {
			sub.log(logger);
		}
	}

	@Override
	public int numConditions() {
		return ConditionCounted.numCombos(subs);
	}
}
