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

public class soundTesting {
	public static void main(String[] args) throws IOException {
		SensorFlagger<Condition> sonarClose = new SensorFlagger<>(new EV3UltrasonicSensor(SensorPort.S2), s -> s.getDistanceMode());
		sonarClose.add2(Condition.CLEAR, Condition.TOO_CLOSE, f -> f > .3);
		
		ArrayBlockingQueue<File> queue = new ArrayBlockingQueue<File>(5);
		
		Transitions<Condition,Mode> transition = new Transitions<>();
		transition.add(Condition.TOO_CLOSE, Mode.PLAY)
		.add(Condition.CLEAR, Mode.FORWARD);
		
		ModeSelector<Condition,Mode> controller = 
				new ModeSelector<>(Condition.class, Mode.class, Mode.FORWARD)
				.sensor(sonarClose)
				.mode(Mode.FORWARD, 
						transition, 
						() -> {
							Motor.A.forward();
							Motor.D.forward();
							})
				.mode(Mode.PLAY, 
						transition, 
						() -> {
							File songOne = new File("/home/lejos/programs/test1.wav");
							queue.add(songOne);
							Motor.A.stop(true);
							Motor.D.stop();
							//new Thread(() -> Sound.playSample(testSong, 100)).start();
						});
		
		controller.control();
		
		new Thread(() -> {
			while (!Button.ESCAPE.isDown()) {
				try {
					File testFile = queue.take();
					Logger.EV3Log.format("File:%s", testFile.toString());
					Sound.playSample(testFile, 100);
					Logger.EV3Log.log("Song over");
					
				} catch (InterruptedException e) {
		            e.printStackTrace();
				}
		}}).start();
		
		System.exit(0);
	}
	
}
