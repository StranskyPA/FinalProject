package PlanningTesting;

import java.io.FileNotFoundException;
import modeselection.vision.landmarks.LandmarkViewer;

public class viewer2 {
	public static void main(String[] args) throws FileNotFoundException {
		new LandmarkViewer("secondTrainer.txt").run();
	}
}

