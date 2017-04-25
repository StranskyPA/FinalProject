package modeselection.vision.config;

import java.io.File;
import java.io.FileNotFoundException;

import lejos.hardware.lcd.LCD;
import modeselection.util.Util;
import modeselection.vision.AdaptedYUYVImage;
import modeselection.vision.SSDSceneFlagger;

public class SSDSceneChecker extends BasicVisionBot {
	public static final String FILENAME = "ssd1.txt";

	private AdaptedYUYVImage ref;
	private long total, min, max;
	
	public static void main(String[] args) throws FileNotFoundException {
		SSDSceneChecker checker = new SSDSceneChecker(FILENAME);
		checker.run();
	}
	
	public SSDSceneChecker(String filename) throws FileNotFoundException {
		ref = Util.fileToObject(new File(filename), s -> SSDSceneFlagger.adapt(AdaptedYUYVImage.fromString(s)));
		min = Long.MAX_VALUE;
		max = Long.MIN_VALUE;
		total = 0;
	}
	
	@Override
	public void grabImage(AdaptedYUYVImage img) {
		long dist = ref.distanceTo(SSDSceneFlagger.adapt(img));
		if (dist < min) {min = dist;}
		if (dist > max) {max = dist;}
		total += dist;
		Util.rightJustifyLong(dist, 3);
	}
	
	@Override
	public void displayFinalInfo() {
		displayFrameRate(2);
		LCD.drawString(String.format("min: %d", min), 0, 3);
		LCD.drawString(String.format("max: %d", max), 0, 4);
		LCD.drawString(String.format("mean:%d", total/getCycles()), 0, 5);		
	}
}
