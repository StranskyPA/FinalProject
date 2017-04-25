package modeselection.vision;

import lejos.hardware.video.YUYVImage;
import modeselection.vision.color.ColorFilter;
import modeselection.vision.config.VisionBot;

public class MovingSquare {
	private Pos corner, size;
	private int width;
	
	public static final int START_SIZE = 20;
	
	public MovingSquare(int width, int height) {
		this.width = width;
		corner = new Pos((width - START_SIZE) / 2, (height - START_SIZE) / 2);
		size = new Pos(START_SIZE, START_SIZE);
	}
	
	public void move(Dir d) {
		Pos old = corner;
		corner = d.move(corner);
		if (!VisionBot.inBounds(upperLeft()) ||
			!VisionBot.inBounds(lowerRight())) {
			corner = old;
		}
	}
	
	public void alter(Dir d) {
		size = d.move(size);
	}
	
	public Pos upperLeft() {return corner;}
	public Pos lowerRight() {return corner.add(size);}
	
	public boolean in(Pos p) {
		Pos lr = lowerRight();
		return p.getX() >= corner.getX() &&
				p.getY() >= corner.getY() &&
				p.getX() <= lr.getX() &&
				p.getY() <= lr.getY();
	}
	
	public void render(BitImage img) {
		Pos lr = lowerRight();
		for (int x = corner.getX(); x <= lr.getX(); x++) {
			for (int y = corner.getY(); y <= lr.getY(); y++) {
				img.flip(x, y);
			}
		}
	}
	
	public void addColors(ColorFilter model, YUYVImage img) {
		Pos lr = lowerRight();
		for (int x = corner.getX(); x <= lr.getX(); x++) {
			for (int y = corner.getY(); y <= lr.getY(); y++) {
				model.add(img, x, y);
			}
		}
	}
	
	int getColumn(int pixel) {
		return (pixel / 2) % width;
	}
	
	int getRow(int pixel) {
		return (pixel / 2) / width;
	}
}
