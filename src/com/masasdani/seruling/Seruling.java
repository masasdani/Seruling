package com.masasdani.seruling;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import com.masasdani.seruling.engine.Clarinet;
import com.masasdani.seruling.engine.Flute;
import com.masasdani.seruling.engine.MicrophoneInput;
import com.masasdani.seruling.engine.MicrophoneInputListener;
import com.masasdani.seruling.engine.Pianica;
import com.masasdani.seruling.util.FileWriter;
import com.masasdani.seruling.util.Frequency;
import com.masasdani.seruling.util.Preference;
import com.masasdani.seruling.view.BarLevelDrawable;
import com.masasdani.seruling.view.ButtonSeruling;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ToggleButton;

public class Seruling extends Activity implements OnTouchListener, MicrophoneInputListener, OnClickListener {

	private MicrophoneInput micInput;
	private int width;
	private int height;
	private float xc_5;
	private float yc_5;
	private String text;
	private boolean touch = false;
	private boolean started = false;
	private Play play;
	
	private ButtonSeruling buttonSeruling;
	private BarLevelDrawable barLevelDrawable;
	private ToggleButton buttonStartStop;
	private Button buttonSimpan;
	private Button button;
	
	private AudioTrack audioTrack;
	private MediaPlayer mediaPlayer;
	private int bufferSize;
	private int minSize;
	private final int SAMPLE_RATE = 44100;
	
	private short[] buffer;
	private Flute flute;
	private Clarinet clarinet;
	private Pianica pianica;
	private float averagePower  = 1.0f;
	private float frequency = 440.0f;

	private static Map<String, Float> soundMap;
	
	private double mOffsetdB = 10;  
	private double mGain = 2500.0 / Math.pow(10.0, 90.0 / 20.0);
	  
	private int gainIncrement = 0;
	private String instrument;
	
	private double mRmsSmoothed; 
	private double mAlpha = 0.9; 
	  
	private volatile boolean mDrawing;
	private String filename = "";
	private String tempFilename = "";
	private FileOutputStream os;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getPreferenceData();
		filename = FileWriter.getFilename();
		tempFilename = FileWriter.getTempFilename();
		micInput = new MicrophoneInput(this);
		
		initView();
		
		soundMap = Frequency.getFrequencyMap();
		
		bufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
			AudioFormat.CHANNEL_CONFIGURATION_MONO,
			AudioFormat.ENCODING_PCM_16BIT);

		minSize = bufferSize / 4;
		
		audioTrack = new AudioTrack(
			AudioManager.STREAM_MUSIC, SAMPLE_RATE,
			AudioFormat.CHANNEL_CONFIGURATION_MONO,
			AudioFormat.ENCODING_PCM_16BIT,
			bufferSize,
			AudioTrack.MODE_STREAM);
		
		buffer = new short[minSize];
		flute = new Flute(minSize);
		clarinet = new Clarinet(minSize);
		pianica = new Pianica();
	}
	
	private void getPreferenceData() {
		gainIncrement = Preference.getCorrection(this);
		instrument = Preference.getInstrument(this);

		mGain = 2500.0 / Math.pow(10.0, 90.0 / 20.0);
		mGain *= Math.pow(10, -gainIncrement / 20.0);

		Log.d("increment", String.valueOf(gainIncrement));
		Log.d("instrument", instrument);
	}

	private void initView() {
		setContentView(R.layout.seruling);
		
		barLevelDrawable = (BarLevelDrawable) findViewById(R.id.bar_level_drawable_view);
		buttonSeruling = (ButtonSeruling) findViewById(R.id.buttonSeruling);
		
		buttonStartStop = (ToggleButton) findViewById(R.id.start_stop);
		buttonStartStop.setOnClickListener(this);
		
		buttonSimpan = (Button) findViewById(R.id.playButton);
		buttonSimpan.setOnClickListener(this);
		
		button = (Button) findViewById(R.id.menu);
		button.setOnClickListener(this);
		
		buttonSeruling.setOnTouchListener(this);
	}
	
	private void startMic() {
	      micInput.start();
	}
	
	private void stopMic() {
		micInput.stop();
	}
	
	private void startMediaPlayer(){
		mediaPlayer = new MediaPlayer();
		try {
			if(!filename.isEmpty()){
				mediaPlayer.setDataSource(filename);
				mediaPlayer.prepare();
				mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					
					public void onCompletion(MediaPlayer mp) {
						stopMediaPlayer();
					}
				});
				mediaPlayer.start();
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void stopMediaPlayer(){
		mediaPlayer.stop();
		mediaPlayer = null;
	}
	
	private void simpan(){
		FileWriter.copyWaveFile(tempFilename, filename, minSize);
	}

	@Override
	protected void onResume() {
		touch = false;
		getPreferenceData();
		stopMic();
		buttonStartStop.setChecked(false);
		started=false;
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		stopMic();
		buttonStartStop.setChecked(false);
		touch = false;
		play = null;
		super.onPause();
	}
	
	@Override
	public void finish() {
		stopMic();
		touch = false;
		play = null;
		super.finish();
	}

	public boolean onTouch(View arg0, MotionEvent event) {
		float x=event.getX();
		float y=event.getY();
		
		width 	= buttonSeruling.getWidth();
		height	= buttonSeruling.getHeight();
		xc_5	= width/6;
		yc_5 	= height/10;
		
		if(x > 0 && x < xc_5*2 && y < yc_5*2 && y > 0){
			touch = true;
			text = "C#5";
		}else if(x > xc_5*2 && x < xc_5*4 && y < yc_5*2 && y > 0){
			touch = true;
			text = "C5";
		}else if(x > xc_5*4 && x < xc_5*6 && y < yc_5*2 && y > 0){
			touch = true;
			text = "B4";
		}else if(x > 0 && x < xc_5*2 && y < yc_5*4 && y > yc_5*2){
			touch = true;
			text = "A#4";
		}else if(x > xc_5*2 && x < xc_5*4 && y < yc_5*4 && y > yc_5*2){
			touch = true;
			text = "A4";
		}else if(x > xc_5*4 && x < xc_5*6 && y < yc_5*4 && y > yc_5*2){
			touch = true;
			text = "G#4";
		}else if(x > 0 && x < xc_5*2 && y < yc_5*6 && y > yc_5*4){
			touch = true;
			text = "G4";
		}else if(x > xc_5*2 && x < xc_5*4 && y < yc_5*6 && y > yc_5*4){
			touch = true;
			text = "F#4";
		}else if(x > xc_5*4 && x < xc_5*6 && y < yc_5*6 && y > yc_5*4){
			touch = true;
			text = "F4";
		}else if(x > 0 && x < xc_5*2 && y < yc_5*8 && y > yc_5*6){
			touch = true;
			text = "E4";
		}else if(x > xc_5*2 && x < xc_5*4 && y < yc_5*8 && y > yc_5*6){
			touch = true;
			text = "D#4";
		}else if(x > xc_5*4 && x < xc_5*6 && y < yc_5*8 && y > yc_5*6){
			touch = true;
			text = "D4";
		}else if(x > 0 && x < xc_5*2 && y < yc_5*10 && y > yc_5*8){
			touch = true;
			text = "C#4";
		}else if(x > xc_5*2 && x < xc_5*4 && y < yc_5*10 && y > yc_5*8){
			touch = true;
			text = "C4";
		}else if(x > xc_5*4 && x < xc_5*6 && y < yc_5*10 && y > yc_5*8){
			touch = true;
			text = "B3";
		}
		frequency = soundMap.get(text);
		if(event.getAction()==MotionEvent.ACTION_DOWN){
			touch = true;
		}
		if(event.getAction() == MotionEvent.ACTION_UP){
			touch = false;
		}
		return true;
	}
	
	
	class Play extends Thread{
		
		@Override
		public void run() {
			audioTrack.play();
			try {
				os = new FileOutputStream(tempFilename);
				while (started) {
					if(touch){
						if(instrument.equalsIgnoreCase("clarinet")){
							clarinet.clarinet(buffer, minSize, averagePower, frequency, SAMPLE_RATE);
						}else if(instrument.equalsIgnoreCase("pianika")){
							pianica.pianica(buffer, frequency, SAMPLE_RATE);
						}else{
							flute.flute(buffer, minSize, averagePower, frequency, SAMPLE_RATE);
						}
					}else{
						buffer = new short[minSize];
					}
					
					byte[] fileBuffer = new byte[minSize*2];
				    
					for (int i = 0; i < buffer.length; i++){
				    	fileBuffer[i*2] = (byte)(buffer[i]);
			            fileBuffer[i*2 + 1] = (byte)(buffer[i] >> 8);
			        }
				    
					try {
						os.write(fileBuffer);
					}catch (IOException e){
						e.printStackTrace();
					}
				
					
					audioTrack.setStereoVolume(averagePower, averagePower);
					audioTrack.write(buffer, 0, minSize);
				    
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}	
		}
	}

	public void processAudioFrame(short[] audioFrame) {
		if (!mDrawing) {
		      mDrawing = true;
		      
		      double rms = 0;
		      for (int i = 0; i < audioFrame.length; i++) {
		        rms += audioFrame[i]*audioFrame[i];
		      }
		      rms = Math.sqrt(rms/audioFrame.length);
		
		      mRmsSmoothed = mRmsSmoothed * mAlpha + (1 - mAlpha) * rms;
		      final double rmsdB = 20.0 * Math.log10(mGain * mRmsSmoothed);
		      
		      barLevelDrawable.post(new Runnable() {
		        
		    	  public void run() {
		    		  barLevelDrawable.setLevel((mOffsetdB + rmsdB) / 60);
		    		  averagePower = (float) barLevelDrawable.getLevel();
			          mDrawing = false;
		    	  }
		      });
		      
		} else {
			
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.playButton:
			simpan();
			startMediaPlayer();
			break;

		case R.id.start_stop:
			if(buttonStartStop.isChecked()){
				startMic();
				started = true;
				play = new Play();
				play.start();
			}else{
				stopMic();
				started = false;
				play = null;
			}
			break;

		case R.id.menu:
			startActivity(new Intent(Seruling.this, Setting.class));
			break;
			
		default:
			break;
		}
	}
	
}
