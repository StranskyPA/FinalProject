package modeselection.vision;

import java.io.FileNotFoundException;

public class BitSceneFlagger<C extends Enum<C>> extends SceneFlagger<C,BitImage,Integer> {	
	public BitSceneFlagger(String... filenames) throws FileNotFoundException {
		super((img1, img2) -> img1.xored(img2).size(),
			  img -> BitImage.intensityView(img),
			  s -> BitImage.from(s),
			  filenames);
	}
}
