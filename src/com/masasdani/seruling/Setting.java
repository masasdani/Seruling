// Copyright 2011 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.masasdani.seruling;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.masasdani.seruling.engine.*;
import com.masasdani.seruling.util.Preference;
import com.masasdani.seruling.view.*;

import java.text.DecimalFormat;

public class Setting extends Activity implements MicrophoneInputListener {

	  private MicrophoneInput micInput;  
	  private TextView mdBTextView;
	  private TextView mdBFractionTextView;
	  private BarLevelDrawable mBarLevel;
	  private TextView mGainTextView;
	  private Spinner spinner;
	
	  private double mOffsetdB = 10;  
	  private double mGain = 2500.0 / Math.pow(10.0, 90.0 / 20.0);
	  
	  private double mDifferenceFromNominal = 0.0;
	  private double mRmsSmoothed; 
	  private double mAlpha = 0.9;
	  
	  private String instrument;
	  private int correction;
	  
	  private volatile boolean mDrawing;
	
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
		
		  micInput = new MicrophoneInput(this);
		  setContentView(R.layout.setting);
		
		  initview();
		  initPreference();
		  startMic();
	  }
	  
	  private void initPreference(){
		  instrument = Preference.getInstrument(this);
		  correction = Preference.getCorrection(this);
		  mDifferenceFromNominal = correction;
	      DecimalFormat df = new DecimalFormat("##.# dB");
	      mGainTextView.setText(df.format(mDifferenceFromNominal));
	      mGain = 2500.0 / Math.pow(10.0, 90.0 / 20.0);
	      mGain *= Math.pow(10, -correction / 20.0);
	      
	      if(instrument.equals("seruling")){
	    	  spinner.setSelection(0);
	      }
	      if(instrument.equals("clarinet")){
	    	  spinner.setSelection(1);
	      }
	      if(instrument.equals("pianika")){
	    	  spinner.setSelection(2);
	      }
	  }
	
	  private void initview() {
		  mBarLevel = (BarLevelDrawable)findViewById(R.id.bar_level_drawable_view);
		  mdBTextView = (TextView)findViewById(R.id.dBTextView);
		  mdBFractionTextView = (TextView)findViewById(R.id.dBFractionTextView);
		  mGainTextView = (TextView)findViewById(R.id.gain);
		  spinner = (Spinner) findViewById(R.id.instrument_setting);
		  ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				  this, 
				  R.array.instrument, 
				  android.R.layout.simple_spinner_item);
		  adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		  spinner.setAdapter(adapter);
		  spinner.setOnItemSelectedListener(new SpinnerListener());
		
		  Button minus5dbButton = (Button)findViewById(R.id.minus_5_db_button);
		  DbClickListener minus5dBButtonListener = new DbClickListener(-5.0);
		  minus5dbButton.setOnClickListener(minus5dBButtonListener);
		
		  Button minus1dbButton = (Button)findViewById(R.id.minus_1_db_button);
		  DbClickListener minus1dBButtonListener = new DbClickListener(-1.0);
		  minus1dbButton.setOnClickListener(minus1dBButtonListener);
		
		  Button plus1dbButton = (Button)findViewById(R.id.plus_1_db_button);       
		  DbClickListener plus1dBButtonListener = new DbClickListener(1.0);
		  plus1dbButton.setOnClickListener(plus1dBButtonListener);
		
		  Button plus5dbButton = (Button)findViewById(R.id.plus_5_db_button);       
		  DbClickListener plus5dBButtonListener = new DbClickListener(5.0);
		  plus5dbButton.setOnClickListener(plus5dBButtonListener);
		
		  Button simpanButton = (Button)findViewById(R.id.simpanButton);
		  Button.OnClickListener saveBtnListener = new Button.OnClickListener() {
		
		    	public void onClick(View v) {
		    		com.masasdani.seruling.util.Preference.save(getApplicationContext(), (int) mDifferenceFromNominal, instrument);
		    		Setting.this.finish();
		    	}
		  };
		  simpanButton.setOnClickListener(saveBtnListener);
		  startMic();
	}
	  
	public class SpinnerListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			instrument = parent.getItemAtPosition(pos).toString();
		}

		public void onNothingSelected(@SuppressWarnings("rawtypes") AdapterView parent) {
			
		}
	}

	private void startMic() {
	      micInput.start();
	}
	
	private class DbClickListener implements Button.OnClickListener {
	    private double gainIncrement;
	
	    public DbClickListener(double gainIncrement) {
	      this.gainIncrement = gainIncrement;
	    }
	
	    public void onClick(View v) {
	      Setting.this.mGain *= Math.pow(10, gainIncrement / 20.0);
	      mDifferenceFromNominal -= gainIncrement;
	      DecimalFormat df = new DecimalFormat("##.# dB");
	      mGainTextView.setText(df.format(mDifferenceFromNominal));
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
	      
		    mBarLevel.post(new Runnable() {
	        
		    	public void run() {
		    		mBarLevel.setLevel((mOffsetdB + rmsdB) / 60);
		    		DecimalFormat df = new DecimalFormat("##");
		    		mdBTextView.setText(df.format(20 + rmsdB));
		
		    		int one_decimal = (int) (Math.round(Math.abs(rmsdB * 10))) % 10;
		    		mdBFractionTextView.setText(Integer.toString(one_decimal));
		    		mDrawing = false;
	    	  }
	      });
	      
	    } else {
	    	
	    }
	}
  

	private void stopMic() {
		micInput.stop();
	}
	
  	@Override
	public void finish() {
  		stopMic();
  		super.finish();
	}

  	@Override
	protected void onDestroy() {
		stopMic();
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
		stopMic();
		super.onPause();
	}
}
