package com.masasdani.seruling.util;

import android.content.Context;
import android.content.SharedPreferences;

public class Preference {

	public static final String SETTING = "setting.conf";
	public static final String CORRECTION = "correction";
	public static final String INSTRUMENT = "instrument";
	
	public static void save(Context context, Integer correction, String instrument){
		SharedPreferences sharedPreferences = context.getSharedPreferences(SETTING, 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(CORRECTION, correction);
		editor.putString(INSTRUMENT, instrument);
		editor.commit();
	}
	
	public static Integer getCorrection(Context context){
		SharedPreferences sharedPreferences = context.getSharedPreferences(SETTING, 0);
		return sharedPreferences.getInt(CORRECTION, 40);
	}
	
	public static String getInstrument(Context context){
		SharedPreferences sharedPreferences = context.getSharedPreferences(SETTING, 0);
		return sharedPreferences.getString(INSTRUMENT, "seruling");
	}
}
