package modeselection.vision.color.config;

import modeselection.vision.AdaptedYUYVImage;
import modeselection.vision.BitImage;
import modeselection.vision.color.ColorFilter;
import modeselection.vision.config.VisionBot;

public class ColorFilterViewer extends VisionBot {
	public static void main(String[] args) {
		//new ColorFilterViewer(new ColorModel(128, 134, 121, 128)).run(); // Banana
		new ColorFilterViewer(new ColorFilter(89, 106, 190, 213)).run(); // Red ball
	}
	
	public ColorFilterViewer(ColorFilter model) {
		this.model = model;
	}
	
	private ColorFilter model;

	@Override
	public BitImage processImage(AdaptedYUYVImage img) {
		return model.filtered(img);
	}

	@Override
	public void displayFinalInfo() {
		displayFrameRate(2);
	}
}
