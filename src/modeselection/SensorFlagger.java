package modeselection;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.Function;

import lejos.hardware.sensor.BaseSensor;
import lejos.robotics.SampleProvider;

public class SensorFlagger<C extends Enum<C>> extends BaseFlagger<C,Float> implements Flagger<C>, Closeable {
	private BaseSensor sensor;
	private SampleProvider fetcher;
	private float[] values;
	
	public SensorFlagger(BaseSensor sensor) {
		this(sensor, s -> s);
	}
	
	public <S extends BaseSensor> SensorFlagger(S sensor, Function<S,SampleProvider> invocation) {
		this.sensor = sensor;
		this.fetcher = invocation.apply(sensor);
		values = new float[sensor.sampleSize()];
	}
	
	public SensorFlagger<C> addValue(C valueFlag) {
		addValue(valueFlag, d -> (double)d);
		return this;
	}
	
	@Override
	public void update(SensedValues<C> conditions) {
		fetcher.fetchSample(values, 0);
		update(values[0], conditions);
	}
	
	@Override
	public void close() throws IOException {
		sensor.close();
	}

	@Override
	protected String getLogMsg() {
		return String.format("%s: %5.2f", sensor.getName(), values[0]);
	}
}
