package PlanningTesting;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

import PlanningTesting.Mode;
import PlanningTesting.Condition;
import PlanningTesting.testTrainer;
import lejos.hardware.motor.Motor;
import modeselection.StateClassifier;
import modeselection.planning.Executor;
import modeselection.planning.GoalPicker;
import modeselection.planning.Planner;
import modeselection.vision.CameraFlagger;
import modeselection.vision.landmarks.LandmarkFlagger;
import lejos.hardware.Button;
import lejos.hardware.Sound;

public class PlanningTest {
	public static void main(String[] args) throws IOException{
		File testSong = new File("test1.wav");
		File testSong2 = new File("dumb.wav");
		Thread threadOne = new Thread(() -> Sound.playSample(testSong, 80));
		Thread threadTwo = new Thread(() -> Sound.playSample(testSong, 80));
		
		ArrayBlockingQueue queue = new ArrayBlockingQueue(5);
		
		CameraFlagger<Condition> camera = new CameraFlagger<>();
		LandmarkFlagger<Condition> landmarker = new LandmarkFlagger<>(testTrainer.FILENAME);
		//change these values
		landmarker.add(Condition.NO_OBJECT, 0, 1, 5, 2)
		  .add(Condition.OBJECT_ONE, 3)
		  .add(Condition.OBJECT_TWO, 6)
		  .add(Condition.VEER_LEFT, 4, 7);
		camera.addSub(landmarker);
		StateClassifier<Condition> sensors = new StateClassifier<>(Condition.class);
		sensors.add(camera);
		
		Planner<Condition,Mode> planner = new Planner<>(Condition.class);
		planner.add(Condition.NO_OBJECT, Mode.FORWARD, Condition.OBJECT_ONE, Condition.OBJECT_TWO)
			   .add(Condition.OBJECT_ONE, Mode.LEFT_1, Condition.NO_OBJECT)
			   .add(Condition.OBJECT_TWO, Mode.LEFT_2, Condition.NO_OBJECT)
			   .add(Condition.VEER_LEFT, Mode.LEFT_1, Condition.NO_OBJECT, Condition.OBJECT_ONE, Condition.OBJECT_TWO);
		
		Executor<Condition,Mode> executor = new Executor<>(Mode.class, Mode.FORWARD);
		executor.mode(Mode.FORWARD, () -> {
					Motor.A.setSpeed(180);
					Motor.D.setSpeed(180);
					Motor.A.forward();
					Motor.D.forward();
				})
				.mode(Mode.LEFT_1, () -> {
					String SongOne = "test1.wav";
					queue.add(SongOne);
					Motor.A.setSpeed(90);
					Motor.D.setSpeed(90);
					Motor.A.backward();
					Motor.D.forward();
				})
				.mode(Mode.LEFT_2, () -> {
					//new Thread(() -> Sound.playSample(testSong2, 80)).start();
					Motor.A.setSpeed(90);
					Motor.D.setSpeed(90);
					Motor.A.backward();
					Motor.D.forward();
				});
		
		GoalPicker<Condition,Mode> controller = new GoalPicker<>(planner, executor, sensors); 
		controller.control();
		System.exit(0);
		
		while (!Button.ESCAPE.isDown()) {
			try {
				String songName = queue.take().toString();
				if (songName != "") {
					File song = new File(songName);
					new Thread(() -> Sound.playSample(song, 80));
				}
			} catch (InterruptedException e) {
	            e.printStackTrace();
			}
		}
	}
}
