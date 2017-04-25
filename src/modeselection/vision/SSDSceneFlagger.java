package modeselection.vision;

import java.io.FileNotFoundException;

public class SSDSceneFlagger<C extends Enum<C>> extends SceneFlagger<C,AdaptedYUYVImage,Long> {
	
	private static final int SHRINK = 2;
	
	public static AdaptedYUYVImage adapt(AdaptedYUYVImage src) {
		return src.shrunken(SHRINK);
	}
	
	public SSDSceneFlagger(String... filenames) throws FileNotFoundException {
		super((img1, img2) -> img1.distanceTo(img2), 
				img -> adapt(img), 
				s -> adapt(AdaptedYUYVImage.fromString(s)), 
				filenames);
	}
}
