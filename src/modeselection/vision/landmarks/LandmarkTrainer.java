package modeselection.vision.landmarks;

import java.io.File;
import java.io.IOException;

import lejos.hardware.motor.Motor;
import modeselection.cluster.ShrinkingImageBSOC;
import modeselection.util.ButtonDriver;
import modeselection.util.CycleTimer;
import modeselection.util.Util;
import modeselection.vision.AdaptedYUYVImage;
import modeselection.vision.BitImage;
import modeselection.vision.config.VisionBot;

public class LandmarkTrainer extends VisionBot {	
	private ShrinkingImageBSOC bsoc;
	private ButtonDriver driver = new ButtonDriver(Motor.A, Motor.D);
	private CycleTimer trainRate = new CycleTimer();
	private String filename;
	
	public LandmarkTrainer(String filename, int nodes, int shrink) {
		bsoc = new ShrinkingImageBSOC(nodes, shrink);
		this.filename = filename;
	}
	
	public LandmarkTrainer(String src) {
		bsoc = new ShrinkingImageBSOC(src);
		this.filename = src;
	}
	
	@Override
	public void displayFinalInfo() {
		trainRate.display(2, "rate");
		try {
			Util.objectToFile(new File(filename), bsoc);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException(e.getMessage());
		}
	}

	@Override
	public BitImage processImage(AdaptedYUYVImage img) {
		driver.drive(() -> {
			trainRate.start();
			bsoc.train(img);
			trainRate.bumpCycle();
		});
		return BitImage.intensityView(img);
	}
}
