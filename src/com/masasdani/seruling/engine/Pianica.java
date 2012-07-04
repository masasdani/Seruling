package com.masasdani.seruling.engine;

public class Pianica {

	public void pianica(short[] buffer, float frequency, int sampleRate) {
		float angular_frequency = (float) (2*Math.PI) * frequency / sampleRate;
		float angle = 0;
		for (int i = 0; i < buffer.length; i++){
			buffer[i] = (short)(Short.MAX_VALUE * ((float) Math.sin(angle)));
			angle += angular_frequency;
		}
	}
}
