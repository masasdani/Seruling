package com.masasdani.seruling.engine;

import java.util.HashMap;
import java.util.Map;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;

public class SoundGenerator {

	private static Map<String, Float> soundMap;
	boolean keepGoing = false;
	AudioSynthesisTask audioSynth;
	
	public SoundGenerator() {
		soundMap = new HashMap<String, Float>();
		soundMap.put("C", 261.63f);
		soundMap.put("D", 293.66f);
		soundMap.put("E", 329.63f);
		soundMap.put("F", 349.23f);
		soundMap.put("G", 392.00f);
		soundMap.put("A", 440.00f);
		soundMap.put("B", 493.88f);
		
	}
	
	public void start(String s){
		keepGoing = true;
		audioSynth = new AudioSynthesisTask(soundMap.get(s));
		audioSynth.execute();
	}
	
	public void stop(){
		keepGoing = false;
	}
	
	private class AudioSynthesisTask extends AsyncTask<Void, Void, Void>{
		
		float synth_frequency;
		boolean play = false;
		
		public AudioSynthesisTask(float freq) {
			synth_frequency = freq;
		}
		
		private void execute() {
			play = true;
		}
		
		@Override
		protected void finalize() throws Throwable {
			super.finalize();
			play = false;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			final int SAMPLE_RATE = 11025;
			
			int minSize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
			
			AudioTrack audioTrack = new AudioTrack(
				AudioManager.STREAM_MUSIC, SAMPLE_RATE,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT,
				minSize,
				AudioTrack.MODE_STREAM);
			
			audioTrack.play();

			float angular_frequency = (float) (2*Math.PI) * synth_frequency / SAMPLE_RATE;
			float angle = 0;
			
			short[] buffer = new short[minSize];
			while (play) {
				for (int i = 0; i < buffer.length; i++){
					buffer[i] = (short)(Short.MAX_VALUE * ((float) Math.sin(angle)));
					angle += angular_frequency;
				}
				audioTrack.write(buffer, 0, buffer.length);
			}
			return null;
		}
	}
	
	
}
