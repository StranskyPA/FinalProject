package modeselection.vision.color;

import lejos.hardware.video.YUYVImage;
import modeselection.vision.config.VisionBot;

public class ColorCount {
	private int[] counts;
	private int total;
	
	public ColorCount(ColorFilter color, YUYVImage img) {
		total = 0;
		counts = new int[VisionBot.WIDTH / 2];
		for (int y = 0; y < VisionBot.HEIGHT; y++) {
			for (int x = 0; x < VisionBot.WIDTH; x += 2) {
				if (color.contains(img, x, y)) {
					counts[x/2] += 1;
					total += 1;
				}
			}
		}
	}
	
	@Override
	public String toString() {
		return String.format("ColorCount: count: %d densest(): %d", getTotal(), densest());
	}
	
	public int size() {
		return counts.length;
	}
	
	public int getTotal() {return total;}
	
	public int get(int x) {
		return counts[x];
	}
	
	public int densest() {
		return densest(1);
	}
	
	public int densest(int minCount) {
		int longStart = -1;
		int longEnd = -2;
		int currentStart = 0;
		int currentEnd = 0;
		for (int i = 0; i <= size(); i++) {
			if (i == size() || counts[i] < minCount) {
				currentEnd = i - 1;
				if (currentEnd - currentStart > longEnd - longStart) {
					longStart = currentStart;
					longEnd = currentEnd;
				}
				currentStart = i;
			}
		}
		// No need to divide by 2.
		// Remember that there are half as many buckets
		// as x coordinates.
		return longStart + longEnd;
	}
}
