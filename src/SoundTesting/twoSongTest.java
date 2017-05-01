package SoundTesting;

import java.io.File;
import java.io.IOException;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import modeselection.util.Logger;

public class twoSongTest {
	public static void main(String[] args) throws IOException {
		File songOne = new File("/home/lejos/programs/test1.wav");
		File songTwo = new File("/home/lejos/programs/dumb.wav");
		
		new Thread(() -> {
			while (!Button.ESCAPE.isDown()) {
				LCD.drawString("SongOne", 1, 1);
				Logger.EV3Log.format("File:%s", songOne.toString());
				Sound.playSample(songOne, 50);
				Logger.EV3Log.log(String.valueOf(Sound.getTime()));
				Sound.pause(3);
				Logger.EV3Log.log("Song over");
				LCD.drawString("SongTwo", 1, 1);
				Logger.EV3Log.format("File:%s", songTwo.toString());
				Sound.playSample(songTwo, 50);
				Logger.EV3Log.log(String.valueOf(Sound.getTime()));
				Sound.pause(3);
				Logger.EV3Log.log("Song over");
			}
		}).start();
	}

}
