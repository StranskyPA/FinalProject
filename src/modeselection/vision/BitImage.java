package modeselection.vision;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.function.BiConsumer;

import lejos.hardware.lcd.LCD;
import lejos.hardware.video.YUYVImage;
import modeselection.util.DeepCopyable;
import modeselection.util.Util;

public class BitImage implements ImageOutline, DeepCopyable<BitImage> {
	private BitSet pixels;
	private int width, height;
	
	private BitImage(BitSet pix, int w, int h) {
		pixels = pix;
		width = w;
		height = h;
	}
	
	public BitImage(BitImage src) {
		this(src.pixelCopy(), src.width, src.height);
	}
	
	private BitSet pixelCopy() {
		return pixels.get(0, width*height);
	}
	
	public BitImage(int width, int height) {
		pixels = new BitSet(width * height);
		pixels.clear();
		this.width = width;
		this.height = height;
	}
	
	public BitImage(YUYVImage src, TriIntPredicate classifier) {
		this(new BitSet(src.getWidth() * src.getHeight()), src.getWidth(), src.getHeight());
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				set(x, y, 
					classifier.test(src.getY(x, y) & 0xFF,
									src.getU(x, y) & 0xFF,
									src.getV(x, y) & 0xFF));
			}
		}
	}
	
	public static BitImage from(String src) {
		String[] rows = src.split(" ");
		BitImage result = new BitImage(rows[0].length(), rows.length);
		for (int y = 0; y < rows.length; y++) {
			for (int x = 0; x < rows[y].length(); x++) {
				result.set(x, y, rows[y].charAt(x) == '1');
			}
		}
		return result;
	}
	
	public static BitImage intensityView(YUYVImage src) {
		int mean = src.getMeanY();
		BitImage img = new BitImage(src.getWidth(), src.getHeight());
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				img.set(x, y, (src.getY(x, y) & 0xFF) > mean);
			}
		}
		return img;
	}
	
	public static BitImage colorView(YUYVImage src, BiIntPredicate colors) {
		BitImage img = new BitImage(src.getWidth(), src.getHeight());
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x+=2) {
				boolean match = colors.test(src.getU(x, y) & 0xFF, src.getV(x, y) & 0xFF);
				img.set(x, y, match);
				img.set(x+1, y, match);
			}
		}
		return img;
	}
	
	public void applyToSubimage(int x, int y, int width, int height, BiConsumer<Integer,Integer> func) {
		int x1 = Math.max(0, x);
		int x2 = Math.min(getWidth() - 1, x + width - 1);
		int y1 = Math.max(0, y);
		int y2 = Math.min(getHeight() - 1, y + height - 1);
		for (int xi = x1; xi <= x2; xi++) {
			for (int yi = y1; yi <= y2; yi++) {
				if (isSet(xi, yi)) {func.accept(xi, yi);}
			}
		}
	}
	
	public void draw() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				LCD.setPixel(x, y, isSet(x, y) ? 1 : 0);
			}
		}
	}
	
	public void drawVerticalLine(int x) {
		for (int y = 0; y < height; y++) {
			flip(x, y);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				result.append(isSet(x, y) ? "1" : "0");
			}
			result.append("\n");
		}
		return result.toString();
	}
	
	@Override
	public int hashCode() {
		return pixels.hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof BitImage) {
			BitImage that = (BitImage)other;
			return this.width == that.width && this.height == that.height && this.pixels.equals(that.pixels);
		} else {
			return false;
		}
	}
	
	public int getWidth() {return width;}
	public int getHeight() {return height;}
	
	public int size() {return pixels.cardinality();}
	
	public void set(int x, int y, boolean value) {
		pixels.set(index(x, y), value);
	}
	
	public void set(int x, int y) {
		set(x, y, true);
	}
	
	public void clear(int x, int y) {
		set(x, y, false);
	}
	
	public void flip(int x, int y) {
		set(x, y, !isSet(x, y));
	}
	
	public void clearAll() {
		pixels.clear();
	}
	
	public BitImage xored(BitImage other) {
		BitSet xored = this.pixelCopy();
		xored.xor(other.pixels);
		return new BitImage(xored, width, height);
	}
	
	public int distanceTo(BitImage other) {
		return xored(other).size();
	}
	
	public void xDilate(int radius) {
		Util.assertArgument(radius >= 1, "dilation width must be positive");
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				for (int w = Math.max(0, x - radius); w <= Math.min(getWidth() - 1, x + radius); w++) {
					if (!isSet(x, y) && isSet(w, y)) {set(x, y);}
				}
			}
		}
	}
	
	public boolean isSet(int x, int y) {
		return pixels.get(index(x, y));
	}
	
	int index(int x, int y) {
		return y * width + x;
	}
	
	int xPart(int index) {
		return index % width;
	}
	
	int yPart(int index) {
		return index / width;
	}
	
	public ArrayList<Feature> allSet() {
		ArrayList<Feature> result = new ArrayList<>();
		for (int i = pixels.nextSetBit(0); i >= 0; i = pixels.nextSetBit(i+1)) {
			result.add(new Feature(xPart(i), yPart(i)));
		}
		return result;
	}

	@Override
	public BitImage deepCopy() {
		return new BitImage(this);
	}
	
	public static void visitNeighbors(BitImage img1, BitImage img2, int searchBound, BiConsumer<Feature,Feature> neighborFunc) {
		for (Feature f1: img1.allSet()) {
			img2.applyToSubimage(f1.X() - searchBound/2, f1.Y() - searchBound/2, searchBound, searchBound, (x,y) -> {
				neighborFunc.accept(f1, new Feature(x, y));
			});
		}
	}
}
