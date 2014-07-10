/**
 * Copyright (c) 2014 Lewis Chun
 * Android game in which the user controls a ball to get a high score.
 *
 * This file is part of Roll Ball Roll.
 *
 * Roll Ball Roll is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.zwac035.finalprogram;

import java.util.logging.Level;
import java.util.logging.LogManager;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.WindowManager;

import com.jme3.app.AndroidHarness;
import com.jme3.input.event.TouchEvent;
import com.jme3.system.android.AndroidConfigChooser.ConfigType;

/**
 * Android activity that launches the game.
 * @author Lewis Chun
 */
public class MainActivity extends AndroidHarness implements SensorEventListener {

	private SensorManager sm;

	public MainActivity() {
		// Set the application class to run
		appClass = "com.zwac035.finalprogram.Main";
		// Try ConfigType.FASTEST; or ConfigType.LEGACY if you have problems
		eglConfigType = ConfigType.BEST;
		// Exit Dialog title & message
		exitDialogTitle = "Exit?";
		exitDialogMessage = "Press Yes";
		// Choose screen orientation
		screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
		// Enable MouseEvents being generated from TouchEvents (default = true)
		mouseEventsEnabled = true;
		// Set the default logging level (default=Level.INFO, Level.ALL=All
		// Debug Info)
		LogManager.getLogManager().getLogger("").setLevel(Level.INFO);
	}

	@Override
	public void onCreate(Bundle instanceState) {
		super.onCreate(instanceState);
		// Create a SensorManager for the accelerometer readings.
		sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		// Stop the screen from sleeping
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// Send data to jMonkey
		boolean showTut = getIntent().getBooleanExtra("tutorial?", false);
		String skinName = getIntent().getStringExtra("Skin");
		if (skinName == null)
			skinName = "Red";
		((Main) getJmeApplication()).showTutorial(showTut);
		((Main) getJmeApplication()).setSkin(skinName);
	}

	@Override
	public void finish() {
		Intent i = new Intent();
		Main app = (Main) getJmeApplication();
		i.putExtra("Score", app.getScore());
		i.putExtra("Triangles", app.getCollectedTriangles());
		setResult(RESULT_OK, i);
		super.finish();
	}

	@Override
	public void onResume() {
		super.onResume();
		sm.registerListener(this,
				sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	public void onPause() {
		super.onPause();
		((Main) getJmeApplication()).pause();
	}

	@Override
	public void onStop() {
		super.onStop();
		// if(isFinishing()){
		sm.unregisterListener(this);
		// }
	}

	private boolean firstReading = true;
	private float tiltY = 0;

	public void onSensorChanged(SensorEvent event) {
		if (getJmeApplication() != null) {
			// Get the y value of how the user is holding the phone. This
			// way the phone doesn't always have to lie flat.
			if (firstReading) {
				tiltY = event.values[1];
				firstReading = false;
			}
			float[] adjustedValues = { event.values[0], event.values[1] - tiltY };
			// Send the values to main application.
			((Main) getJmeApplication()).setSensorValues(adjustedValues);
		}
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onTouch(String name, TouchEvent evt, float tpf) {
		// Do nothing
		// The android harness superclass uses this method to display
		// and exit dialog when the back button is pressed. By overriding
		// the method, we stop the exit dialog from appearing.
	}

}
