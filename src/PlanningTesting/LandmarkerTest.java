package PlanningTesting;

import java.io.File;
import java.io.IOException;
import PlanningTesting.Mode;
import PlanningTesting.Condition;
import lejos.hardware.motor.Motor;
import modeselection.ModeSelector;
import modeselection.MotorFlagger;
import modeselection.Transitions;
import modeselection.vision.CameraFlagger;
import modeselection.vision.landmarks.LandmarkFlagger;
import lejos.hardware.Sound;


public class LandmarkerTest {
	public static void main(String[] args) throws IOException {
		File testSong = new File("test1.wav");
		File testSong2 = new File("dumb.wav");
		
		MotorFlagger<Condition> backupFlag = new MotorFlagger<>(Motor.D);
		backupFlag.add2(Condition.IN_TURN, Condition.TURN_DONE, i -> Math.abs(i) < Math.abs(800));
		
		CameraFlagger<Condition> camera = new CameraFlagger<>();
		LandmarkFlagger<Condition> landmarker = new LandmarkFlagger<>("secondTrainer.txt");
		//change these values
		landmarker.add(Condition.NO_OBJECT, 0, 2, 4, 3, 1, 7)
		  .add(Condition.OBJECT_ONE, 6)
		  .add(Condition.OBJECT_TWO, 5);
		camera.addSub(landmarker);
		
		Transitions<Condition,Mode>  lookingToRap = new Transitions<>();
		lookingToRap
		.add(Condition.OBJECT_ONE, Mode.PLAY_1)
		.add(Condition.OBJECT_TWO, Mode.PLAY_2)
		.add(Condition.NO_OBJECT, Mode.FORWARD);
		
		Transitions<Condition,Mode>  songOne = new Transitions<>();
		songOne
		.add(Condition.TURN_DONE, Mode.FORWARD)
		.add(Condition.IN_TURN, Mode.PLAY_1);
		
		Transitions<Condition,Mode>  songTwo = new Transitions<>();
		songOne
		.add(Condition.TURN_DONE, Mode.FORWARD)
		.add(Condition.IN_TURN, Mode.PLAY_2);
		 
		ModeSelector<Condition,Mode> controller =
				new ModeSelector<>(Condition.class, Mode.class, Mode.FORWARD)
				.flagger(camera)
				.flagger(backupFlag)
				.mode(Mode.FORWARD, lookingToRap, () -> {
					Motor.D.resetTachoCount();
					Motor.A.setSpeed(360);
					Motor.D.setSpeed(360);
					Motor.A.forward();
					Motor.D.forward();
				})
				.mode(Mode.PLAY_1, songOne, () -> {
					new Thread(() -> Sound.playSample(testSong, 100)).start();
					Motor.D.resetTachoCount();
					Motor.A.setSpeed(180);
					Motor.D.setSpeed(180);
					Motor.A.stop();
					Motor.D.forward();
				})
				.mode(Mode.PLAY_2, songTwo, () -> {
					new Thread(() -> Sound.playSample(testSong2, 100)).start();
					Motor.D.resetTachoCount();
					Motor.A.setSpeed(180);
					Motor.D.setSpeed(180);
					Motor.A.stop();
					Motor.D.forward();
				});
		
		controller.control();
		
	}

}
