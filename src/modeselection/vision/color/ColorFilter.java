package modeselection.vision.color;

import lejos.hardware.video.YUYVImage;
import modeselection.vision.BitImage;

public class ColorFilter {
	private int uLo, uHi, vLo, vHi;
	
	public ColorFilter() {
		this(255, 0, 255, 0);
	}
	
	public ColorFilter(int uLo, int uHi, int vLo, int vHi) {
		this.uLo = uLo;
		this.vLo = vLo;
		this.uHi = uHi;
		this.vHi = vHi;
	}
	
	public void add(YUYVImage img, int x, int y) {
		uLo = Math.min(uLo, 0xFF & img.getU(x, y));
		uHi = Math.max(uHi, 0xFF & img.getU(x, y));
		vLo = Math.min(vLo, 0xFF & img.getV(x, y));
		vHi = Math.max(vHi, 0xFF & img.getV(x, y));
	}
	
	public boolean defined() {
		return uLo <= uHi;
	}

	public boolean contains(YUYVImage img, int x, int y) {
		return check(img.getU(x, y) & 0xFF, img.getV(x, y) & 0xFF);
	}
	
	private boolean check(int u, int v) {
		return uLo <= u && u <= uHi && vLo <= v && v <= vHi;
	}
	
	public BitImage filtered(YUYVImage src) {
		return BitImage.colorView(src, (u, v) -> check(u, v));
	}
	
	public int getMinU() {return uLo;}
	public int getMaxU() {return uHi;}
	public int getMinV() {return vLo;}
	public int getMaxV() {return vHi;}
}
