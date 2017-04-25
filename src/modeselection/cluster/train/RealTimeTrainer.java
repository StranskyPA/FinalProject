package modeselection.cluster.train;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import modeselection.util.CycleTimer;
import modeselection.util.Util;
import modeselection.vision.AdaptedYUYVImage;
import modeselection.vision.BitImage;
import modeselection.vision.ShrinkingLabeledBSOC;
import modeselection.vision.config.VisionBot;

public class RealTimeTrainer<E extends Enum<E> & RobotKey> extends VisionBot {
	private ShrinkingLabeledBSOC<E> bsoc;
	private Class<E> enumClass;
	private String filename;
	private boolean learning;
	private CycleTimer trainRate, applyRate;
	
	public RealTimeTrainer(Class<E> enumClass, int nodes, int shrink, E unclassified, String filename) {
		this(enumClass, filename, new ShrinkingLabeledBSOC<>(enumClass, nodes, unclassified, shrink));
		
	}
	
	/**
	 * Loads a previously trained learner for further training.
	 * @param enumClass
	 * @param filename
	 * @throws FileNotFoundException
	 */
	public RealTimeTrainer(Class<E> enumClass, String filename) throws FileNotFoundException {
		this(enumClass, filename, Util.fileToObject(new File(filename), s -> new ShrinkingLabeledBSOC<>(enumClass, s)));
	}
	
	private RealTimeTrainer(Class<E> enumClass, String filename, ShrinkingLabeledBSOC<E> bsoc) {
		this.bsoc = bsoc;
		this.enumClass = enumClass;
		this.filename = filename;
		learning = true;
		trainRate = new CycleTimer();
		applyRate = new CycleTimer();
	}

	@Override
	public BitImage processImage(AdaptedYUYVImage img) {
		pickAction(img);
		return BitImage.intensityView(img);
	}
	
	private void pickAction(AdaptedYUYVImage img) {
		for (E candidate: enumClass.getEnumConstants()) {
			if (candidate.getKey().isDown()) {
				setLearningMode(true);
				trainRate.start();
				bsoc.train(img, candidate);
				candidate.act();	
				trainRate.bumpCycle();
				return;
			}
		}
		
		if (Util.isClicked(Button.ENTER)) {
			setLearningMode(!learning);
		}
		
		if (learning) {
			Util.stopAllMotors();
		} else {
			applyRate.start();
			bsoc.bestMatchFor(img).act();
			applyRate.bumpCycle();
		}
	}
	
	@Override
	public void displayFinalInfo() {
		Util.stopAllMotors();
		String learnStr = String.format("learn: %4.2f hz", trainRate.cyclesPerSecond());
		LCD.drawString(learnStr, 0, 1);
		String applyStr = String.format("apply: %4.2f hz", applyRate.cyclesPerSecond());
		LCD.drawString(applyStr, 0, 2);
		try {
			Util.objectToFile(new File(filename), bsoc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setLearningMode(boolean isLearningNow) {
		learning = isLearningNow;
		if (learning) {
			trainRate.start();
		} else {
			applyRate.start();
		}
	}
}
