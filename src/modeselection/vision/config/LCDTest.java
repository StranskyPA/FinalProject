package modeselection.vision.config;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

public class LCDTest {
	public static void main(String[] args) {
		LCD.clear();
		for (int i = 0; i < VisionBot.WIDTH; i++) {
			LCD.setPixel(i, VisionBot.HEIGHT/2, 1);
		}
		while (Button.ESCAPE.isUp());
	}
}
