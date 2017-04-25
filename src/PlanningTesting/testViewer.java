package PlanningTesting;

import java.io.FileNotFoundException;

import PlanningTesting.testTrainer;
import modeselection.vision.landmarks.LandmarkViewer;

public class testViewer {
	public static void main(String[] args) throws FileNotFoundException {
		new LandmarkViewer(testTrainer.FILENAME).run();
	}
}
