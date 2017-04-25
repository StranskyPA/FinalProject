package PlanningTesting;

import modeselection.vision.landmarks.LandmarkTrainer;

public class testTrainer {
public static final String FILENAME = "testingTrainer.txt";
	
	public static void main(String[] args) {
		new LandmarkTrainer(FILENAME, 8, 8).run();
	}
}
