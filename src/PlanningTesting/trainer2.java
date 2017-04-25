package PlanningTesting;

import modeselection.vision.landmarks.LandmarkTrainer;

public class trainer2 {
	public static void main(String[] args) {
		new LandmarkTrainer("secondTrainer.txt", 8, 8).run();
	}
}
