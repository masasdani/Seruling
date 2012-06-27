package com.masasdani.seruling.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.masasdani.seruling.engine.Encoder;

import android.os.Environment;

public class FileWriter {
	
	public static void writeAudioDataToFile(byte data[], FileOutputStream os) {
		try{
			os.write(data);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getTempFilename() {
		String filePath=Environment.getExternalStorageDirectory().getPath();
		File file = new File(filePath, AppUtils.AUDIO_RECORDER_FOLDER);
		
		if(!file.exists()){
			file.mkdirs();
		}
		
		File tempFile = new File(filePath, AppUtils.AUDIO_RECORDER_TEMP_FILE);
		if(tempFile.exists()) 
			tempFile.delete();
		
		return(file.getAbsolutePath()+"/"+ AppUtils.AUDIO_RECORDER_TEMP_FILE);
	}
	
	public static void deleteFile(String filename) {
		File file = new File(filename);
		file.delete();
	}

	public static void copyWaveFile(String inFilename, String outfilename, int bufferSize) {
		FileInputStream in = null;
		FileOutputStream out = null;
		long totalAudioLen = 0;
		long totalDataLen = totalAudioLen + 36;
		long longSampleRate = AppUtils.RECORDER_SAMPLERATE;
		int channels = 2;
		long byteRate = AppUtils.RECORDER_BPP * AppUtils.RECORDER_SAMPLERATE * channels/8;
		byte[] data = new byte[bufferSize];
		
		try{
			in = new FileInputStream(inFilename);
			out = new FileOutputStream(outfilename);
			totalAudioLen =in.getChannel().size();
			totalDataLen = totalAudioLen + 36;
			
			Encoder.writeWaveFileHeader(out, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);
			while(in.read(data)!=-1){
				out.write(data);
			}
			in.close();
			out.close();
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getFilename() {
		String filePath = Environment.getExternalStorageDirectory().getPath();
		File file= new File(filePath, AppUtils.AUDIO_RECORDER_FOLDER);
		
		if(!file.exists()){
			file.mkdirs();
		}
		//return (file.getAbsolutePath()+"/"+System.currentTimeMillis()+AppUtils.AUDIO_RECORDER_FILE_EXT);
		return (file.getAbsolutePath()+AppUtils.AUDIO_RECORDER_FILE);
	}
}
