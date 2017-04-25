package modeselection.vision.config;

import java.io.File;
import java.io.FileNotFoundException;

import lejos.hardware.lcd.LCD;
import modeselection.util.Util;
import modeselection.vision.AdaptedYUYVImage;
import modeselection.vision.BitImage;

public class BitSceneChecker extends BasicVisionBot {
	public static final String FILENAME = "run2.txt";

	private BitImage ref;
	private int total, min, max;
	
	public static void main(String[] args) throws FileNotFoundException {
		BitSceneChecker checker = new BitSceneChecker(FILENAME);
		checker.run();
	}
	
	public BitSceneChecker(String filename) throws FileNotFoundException {
		ref = Util.fileToObject(new File(filename), s -> BitImage.from(s));
		min = Integer.MAX_VALUE;
		max = Integer.MIN_VALUE;
		total = 0;
	}
	
	@Override
	public void grabImage(AdaptedYUYVImage img) {
		BitImage proc = BitImage.intensityView(img);
		BitImage xored = proc.xored(ref);
		int xor = xored.size();
		if (xor < min) {min = xor;}
		if (xor > max) {max = xor;}
		total += xor;
		Util.rightJustifyLong(xor, 3);
	}
	
	@Override
	public void displayFinalInfo() {
		displayFrameRate(2);
		LCD.drawString(String.format("min: %d", min), 0, 3);
		LCD.drawString(String.format("max: %d", max), 0, 4);
		LCD.drawString(String.format("mean:%d", total/getCycles()), 0, 5);		
	}
}
