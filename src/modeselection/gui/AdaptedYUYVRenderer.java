package modeselection.gui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import modeselection.vision.AdaptedYUYVImage;

public class AdaptedYUYVRenderer {
	private AdaptedYUYVImage img;
	
	public void draw(Canvas canv) {
		renderLoop(canv.getGraphicsContext2D(), canv.getWidth() / img.getWidth(), canv.getHeight() / img.getHeight());
	}

	
	public AdaptedYUYVRenderer(AdaptedYUYVImage img) {
		this.img = img;
	}

	public static void placeOnCanvas(AdaptedYUYVImage img, Canvas canv) {
		new AdaptedYUYVRenderer(img).draw(canv);
	}

	public void renderLoop(GraphicsContext g, double cellWidth, double cellHeight) {
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				g.setFill(getRGBColor(x, y));
				g.fillRect(x * cellWidth, y * cellHeight, cellWidth, cellHeight);
			}
		}
	}
	
	public Color getRGBColor(int x, int y) {
		int c = img.getY(x, y) - 16;
		int d = img.getU(x, y) - 128;
		int e = img.getV(x, y) - 128;
		int r = clamp((298*c + 409*e + 128) >> 8);
		int g = clamp((298*c - 100*d - 208*e + 128) >> 8);
		int b = clamp((298*c + 516*d + 128) >> 8);
		return new Color(r / 255.0, g / 255.0, b / 255.0, 1.0);		
	}
	
	public static int clamp(int value) {
		return Math.min(255, Math.max(0, value));
	}
}
