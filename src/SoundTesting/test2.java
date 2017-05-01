package SoundTesting;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

import SoundTesting.Condition;
import SoundTesting.Mode;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import modeselection.ModeSelector;
import modeselection.SensorFlagger;
import modeselection.Transitions;
import modeselection.util.Logger;

public class test2 {
	public static void main(String[] args) throws IOException {
		
		File songOne = new File("/home/lejos/programs/dumb.wav");
		//File songTwo = new File("/home/lejos/programs/dumb.wav");
		
		SensorFlagger<Condition> sonarClose = new SensorFlagger<>(new EV3UltrasonicSensor(SensorPort.S2), s -> s.getDistanceMode());
		sonarClose.add2(Condition.CLEAR, Condition.TOO_CLOSE, f -> f > .3);
		
		ArrayBlockingQueue<File> queue = new ArrayBlockingQueue<File>(30);
		
		new Thread(() -> {
			while (!Button.ESCAPE.isDown()) {
				try {
					File testFile = queue.take();
					Logger.EV3Log.format("File:%s", testFile.toString());
					Sound.playSample(testFile, 40);
					Sound.pause(0);
					//Logger.EV3Log.log("Song over");
					
				} catch (InterruptedException e) {
					Logger.EV3Log.log("No song played");
		            e.printStackTrace();
				}
		}}).start();
		
		Transitions<Condition,Mode> transition = new Transitions<>();
		transition.add(Condition.TOO_CLOSE, Mode.PLAY)
		.add(Condition.CLEAR, Mode.FORWARD);
		
		ModeSelector<Condition,Mode> controller = 
				new ModeSelector<>(Condition.class, Mode.class, Mode.FORWARD)
				.sensor(sonarClose)
				.mode(Mode.FORWARD, 
						transition, 
						() -> {
							//queue.add(songTwo);
							Motor.A.forward();
							Motor.D.forward();
							})
				.mode(Mode.PLAY, 
						transition, 
						() -> {
							queue.add(songOne);
							Motor.A.stop(true);
							Motor.D.stop();
						});
		
		controller.control();
		
		
		
		System.exit(0);
	}
	
}