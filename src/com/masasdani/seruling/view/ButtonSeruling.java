package com.masasdani.seruling.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public final class ButtonSeruling extends View {

	private int width;
	private int height;
	private volatile boolean touch=false;
	private float x;
	private float y;
	private String text;
	
	/* Note key available _ for # */
	private float xc_5;
	private float yc_5;
	private float xc5;
	private float yc5;
	private float xb4;
	private float yb4;
	private float xa_4;
	private float ya_4;
	private float xa4;
	private float ya4;
	private float xg_4;
	private float yg_4;
	private float xg4;
	private float yg4;
	private float xf_4;
	private float yf_4;
	private float xf4;
	private float yf4;
	private float xe4;
	private float ye4;
	private float xd_4;
	private float yd_4;
	private float xd4;
	private float yd4;
	private float xc_4;
	private float yc_4;
	private float xc4;
	private float yc4;
	private float xb3;
	private float yb3;

	Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	int r;
	
	public ButtonSeruling(Context context) {
		super(context);
		width=this.getWidth();
		height=this.getHeight();
	}
	
	public ButtonSeruling(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		width=this.getWidth();
		height=this.getHeight();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		width=this.getWidth();
		height=this.getHeight();
		
		//mPaint.setColor(0xaa655e28);
		mPaint.setColor(0xaa6f511e);
		xc_5	= width/6;
		yc_5 	= height/10;
		xc5 	= (width/6)*3;
		yc5 	= height/10;
		xb4 	= (width/6)*5;
		yb4 	= height/10;
		xa_4 	= width/6;
		ya_4 	= (height/10)*3f;
		xa4 	= (width/6)*3;
		ya4 	= (height/10)*3f;
		xg_4 	= (width/6)*5;
		yg_4 	= (height/10)*3f;
		xg4 	= width/6;
		yg4 	= (height/10)*5f;
		xf_4 	= (width/6)*3;
		yf_4 	= (height/10)*5f;
		xf4 	= (width/6)*5;
		yf4 	= (height/10)*5f;
		xe4 	= width/6;
		ye4 	= (height/10)*7f;
		xd_4 	= (width/6)*3;
		yd_4 	= (height/10)*7f;
		xd4 	= (width/6)*5;
		yd4 	= (height/10)*7f;
		xc_4 	= width/6;
		yc_4 	= (height/10)*9f;
		xc4 	= (width/6)*3;
		yc4 	= (height/10)*9f;
		xb3 	= (width/6)*5;
		yb3 	= (height/10)*9;
		
		int r = Math.round(xc_5*0.7f);
		this.r = r;
		canvas.drawCircle(xc_5, yc_5, r, mPaint);
		canvas.drawCircle(xc5, yc5, r, mPaint);
		canvas.drawCircle(xb4, yb4, r, mPaint);
		canvas.drawCircle(xa_4, ya_4, r, mPaint);
		canvas.drawCircle(xa4, ya4, r, mPaint);
		canvas.drawCircle(xg_4, yg_4, r, mPaint);
		canvas.drawCircle(xg4, yg4, r, mPaint);
		canvas.drawCircle(xf_4, yf_4, r, mPaint);
		canvas.drawCircle(xf4, yf4, r, mPaint);
		canvas.drawCircle(xe4, ye4, r, mPaint);
		canvas.drawCircle(xd_4, yd_4, r, mPaint);
		canvas.drawCircle(xd4, yd4, r, mPaint);
		canvas.drawCircle(xc_4, yc_4, r, mPaint);
		canvas.drawCircle(xc4, yc4, r, mPaint);
		canvas.drawCircle(xb3, yb3, r, mPaint);
		
		mPaint.setColor(Color.WHITE);
		mPaint.setTextSize(r/2);
		mPaint.setTextAlign(Align.CENTER);
		canvas.drawText("C#5", xc_5, yc_5, mPaint);
		canvas.drawText("C5", xc5, yc5, mPaint);
		canvas.drawText("B4", xb4, yb4, mPaint);
		canvas.drawText("A#4", xa_4, ya_4, mPaint);
		canvas.drawText("A4", xa4, ya4, mPaint);
		canvas.drawText("G#4", xg_4, yg_4, mPaint);
		canvas.drawText("G4", xg4, yg4, mPaint);
		canvas.drawText("F#4", xf_4, yf_4, mPaint);
		canvas.drawText("F4", xf4, yf4, mPaint);
		canvas.drawText("E4", xe4, ye4, mPaint);
		canvas.drawText("D#4", xd_4, yd_4, mPaint);
		canvas.drawText("D4", xd4, yd4, mPaint);
		canvas.drawText("C#4", xc_4, yc_4, mPaint);
		canvas.drawText("C4", xc4, yc4, mPaint);
		canvas.drawText("B3", xb3, yb3, mPaint);
		
		if(touch){
			mPaint.setColor(Color.BLUE);
			canvas.drawText(text, x-50 < 0 ? x-50 : x+50 , y, mPaint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		x=event.getX();
		y=event.getY();
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
		if(event.getAction() == MotionEvent.ACTION_UP){
			touch = false;
		}
		invalidate();
		return true;
	}
	
}
