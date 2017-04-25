package modeselection.vision;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.function.BiFunction;
import java.util.function.Function;

import modeselection.util.Util;

public class SceneFlagger<C extends Enum<C>,I,V extends Comparable<V>> extends BaseSubFlagger<C,V> {
	private I[] images;
	private BiFunction<I,I,V> distFunc;
	private Function<AdaptedYUYVImage,I> transform;
	
	private I[] loadAll(String[] filenames, Function<String,I> fromString) throws FileNotFoundException {
		@SuppressWarnings("unchecked")
		I[] result = (I[])new Object[filenames.length];
		for (int i = 0; i < filenames.length; i++) {
			result[i] = Util.fileToObject(new File(filenames[i]), s -> fromString.apply(s));
		}
		return result;
	}
	
	public SceneFlagger(BiFunction<I,I,V> distFunc, Function<AdaptedYUYVImage,I> transform, Function<String,I> fromString, String... imageFiles) throws FileNotFoundException {
		Util.assertArgument(imageFiles.length > 0, "No images provided");
		this.transform = transform;
		this.distFunc = distFunc;
		this.images = loadAll(imageFiles, fromString);
	}

	@Override
	public V getSample(AdaptedYUYVImage img) {
		I transformed = transform.apply(img);
		V best = distFunc.apply(images[0], transformed);
		for (int i = 1; i < images.length; i++) {
			V current = distFunc.apply(images[i], transformed);
			if (current.compareTo(best) < 0) {
				best = current;
			}
		}
		return best;
	}
}
