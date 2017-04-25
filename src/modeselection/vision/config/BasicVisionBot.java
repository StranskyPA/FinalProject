package modeselection.vision.config;

import java.io.IOException;

import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.video.Video;
import modeselection.util.CycleTimer;
import modeselection.vision.AdaptedYUYVImage;
import modeselection.vision.Pos;

abstract public class BasicVisionBot implements Runnable {
	public static final int WIDTH = 160, HEIGHT = 120;
	public static final int CHAR_ROWS = 8, CHAR_COLS = 18;

	abstract public void grabImage(AdaptedYUYVImage img);
	
	private CycleTimer timer = new CycleTimer();
	
	public void displayFinalInfo() {
		displayFrameRate(2);
	}
	
	public long getCycles() {return timer.getCycles();}
	
	public long getDuration() {return timer.getDuration();}
	
	public long getLastCycleTime() {return timer.getLastDuration();}
	
	public void run() {
		try {
			Video wc = setupVideo();
			timer.start();
			while (Button.ESCAPE.isUp()) {
				wc.grabFrame(frame);
				
				AdaptedYUYVImage img = new AdaptedYUYVImage(frame, WIDTH, HEIGHT);
				grabImage(img);
				timer.bumpCycle();
			}
			
			finish();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	private byte[] frame;
	
	private Video setupVideo() throws IOException {
		Video wc = BrickFinder.getDefault().getVideo();
		wc.open(WIDTH, HEIGHT);
		frame = wc.createFrame();
		return wc;
	}
	
	private void finish() throws InterruptedException {
		LCD.clear();
		displayFinalInfo();
		Thread.sleep(1500);
		while (Button.ESCAPE.isUp());
	}
	
	public long frameRate() {
		return 1000 * getCycles() / getDuration();
	}
	
	public void displayFrameRate(int row) {
		String rate = String.format("rate: %4.2f hz", timer.cyclesPerSecond());
		LCD.drawString(rate, 0, row);
	}
	
	public static boolean inBounds(Pos p) {
		return 0 <= p.getX() && p.getX() < WIDTH && 0 <= p.getY() && p.getY() < HEIGHT;
	}
}
