//[Vocabulary Trainer]
//Copyright (C) [2012]  [Tobias Endrikat]
//
//This program is free software;
//you can redistribute it and/or modify it under the terms of the
//GNU General Public License as published by the Free Software Foundation;
//either version 3 of the License, or (at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
//or FITNESS FOR A PARTICULAR PURPOSE.
//See the GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program; if not, see <http://www.gnu.org/licenses/>.


package com.tobias.vocabulary_trainer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class DrawCanvas extends View
{
	public DrawCanvas(Context context)
	{
	  super(context);
	  initCanvas();
	}
	
	public DrawCanvas(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initCanvas();
	}
	
	public DrawCanvas(Context context, AttributeSet ats, int defaultStyle)
	{
		super(context, ats, defaultStyle);
		initCanvas();
	}
	
	private Paint markerPaint;
	private Paint textPaint;
	private Paint circlePaint;
	boolean draw = true;
	Paint paintDot = new Paint();
	VocabularyTrainer vocabularyTrainer = new VocabularyTrainer();
	public DotsList dotsList = new DotsList();
	Dot actual;
	boolean loeschen = false;
	
	protected void initCanvas()
	{
	  setFocusable(true);
	  circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	  circlePaint.setColor(R.color.background_color);
	  circlePaint.setStrokeWidth(1);
	  circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
	  Resources r = this.getResources();
	  textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	  textPaint.setColor(r.getColor(R.color.text_color));
	  markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	  markerPaint.setColor(r.getColor(R.color.marker_color)); 
	  paintDot.setColor(Color.BLACK);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	  // The compass is a circle that fills as much space as possible.
	  // Set the measured dimensions by figuring out the shortest boundary,
	  // height or width.
	  int measuredWidth = measure(widthMeasureSpec);
	  int measuredHeight = measure(heightMeasureSpec);
	  int d = Math.min(measuredWidth, measuredHeight);
	  setMeasuredDimension(d, d);
	}
	private int measure(int measureSpec) {
	  int result = 0;
	  // Decode the measurement specifications.
	  int specMode = MeasureSpec.getMode(measureSpec);
	  if (specMode == MeasureSpec.UNSPECIFIED) {
	    // Return a default size of 200 if no bounds are specified.
	    result = 150;
	  } else {
	    // As you want to fill the available space
	    // always return the full available bounds.
	    //result = specSize;
		  result = 150;
	  }
	  return result;
	}

	private float bearing;
	public void setBearing(float _bearing) {
	  bearing = _bearing;
	}
	public float getBearing() {
	  return bearing;
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		int px = getMeasuredWidth() / 2;
		int py = getMeasuredHeight() / 2 ;

		//Test
		//zeichne punkt in die mitte
   		if(loeschen == true)
		canvas.drawCircle(px, py, 20, paintDot);
		actual = dotsList.first;
		while(actual != null)
		{
			canvas.drawCircle(actual.x, actual.y, 1, paintDot);
			if(actual.follower != null && actual.follower.drawLine == true)
			{
				canvas.drawLine(actual.x, actual.y, actual.follower.x, actual.follower.y, paintDot);
			}
			actual = actual.follower;
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
	  // Get the type of action this event represents
	  int actionPerformed = event.getAction();
	  boolean zeichneLinie = false;
	  
	  if(actionPerformed == event.ACTION_MOVE)
	  {
		  zeichneLinie = true;
	  }   
	  dotsList.vorneEinfuegen((int)event.getX(), (int)event.getY(), zeichneLinie);
	  this.invalidate();
	  // Return true if the event was handled.
	  return true;
	}
	
	@Override
	public boolean onKeyDown(int _keyCode, KeyEvent _event)
	{
	  // Perform on key pressed handling, return true if handled
	  // Return true if the event was handled.
	  return true;
	}
}