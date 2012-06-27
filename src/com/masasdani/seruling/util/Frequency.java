package com.masasdani.seruling.util;

import java.util.HashMap;
import java.util.Map;

public class Frequency {

	public static Map<String, Float> getFrequencyMap() {
		Map<String, Float> map =new HashMap<String, Float>();
		map.put("", 220.0f);
		map.put("C#5", 554.36f);
		map.put("C5", 523.25f);
		map.put("B4",493.88f);
		map.put("A#4", 466.16f);
		map.put("A4", 440.0f);
		map.put("G#4", 415.3f);
		map.put("G4", 391.99f);
		map.put("F#4", 369.99f);
		map.put("F4", 349.22f);
		map.put("E4", 329.62f);
		map.put("D#4", 311.12f);
		map.put("D4", 293.66f);
		map.put("C#4", 277.18f);
		map.put("C4", 261.62f);
		map.put("B3", 246.94f);
		return map;
	}

	
}
