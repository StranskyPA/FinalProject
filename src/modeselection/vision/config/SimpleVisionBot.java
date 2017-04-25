package modeselection.vision.config;

import modeselection.vision.AdaptedYUYVImage;
import modeselection.vision.BitImage;

public class SimpleVisionBot extends VisionBot {

	public static void main(String[] args) {
		new SimpleVisionBot().run();
	}
	
	@Override
	public BitImage processImage(AdaptedYUYVImage img) {
		return BitImage.intensityView(img);
	}

	@Override
	public void displayFinalInfo() {
		displayFrameRate(2);
	}
}
