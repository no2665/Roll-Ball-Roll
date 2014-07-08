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

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import com.zwac035.finalprogram.Database.SkinsTable;
import com.zwac035.finalprogram.Database.TrianglesTable;

/**
 * The main menu of the application
 */
public class StartScreenActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_screen);
	}

	/**
	 * Called when the game finishes
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Intent i = new Intent(this, EnterNameActivity.class);
			i.putExtra("Score", data.getFloatExtra("Score", 0));
			// Save the collected triangles to the database
			Database db = new Database(this);
			SQLiteDatabase sqlDB = db.getWritableDatabase();

			int collected = data.getIntExtra("Triangles", 0);

			sqlDB.execSQL("UPDATE " + TrianglesTable.TABLE_NAME + " SET "
					+ TrianglesTable.COLLECTED + " = " + collected + " + "
					+ "(SELECT " + TrianglesTable.COLLECTED + " FROM "
					+ TrianglesTable.TABLE_NAME + ");");
			db.close();

			// Start the enter name activity
			startActivity(i);
		}
	}

	/**
	 * Called when the start button is pressed
	 */
	public void startGame(View v) {
		Database db = new Database(this);
		SQLiteDatabase sqlDb = db.getReadableDatabase();
		// Get the selected skin
		String[] select = { SkinsTable.SKIN_NAME };
		String where = SkinsTable.SELECTED + " = 1";
		Cursor c = sqlDb.query(SkinsTable.TABLE_NAME, select, where, null,
				null, null, null);
		c.moveToFirst();
		int index = c.getColumnIndex(SkinsTable.SKIN_NAME);
		String name = c.getString(index);
		Intent i = new Intent(this, MainActivity.class);
		// Send the selected skin to the game
		i.putExtra("Skin", name);
		db.close();
		// Start the game
		startActivityForResult(i, 100);
	}

	/**
	 * Called when the high score button is pressed
	 */
	public void viewScoreTable(View v) {
		startActivity(new Intent(this, HighScoreScreenActivity.class));
	}

	/**
	 * Called when the info button is pressed
	 */
	public void viewInfo(View v) {
		startActivity(new Intent(this, InfoScreenActivity.class));
	}

	/**
	 * Called when the tutorial button is pressed
	 */
	public void launchTutorial(View v) {
		Intent i = new Intent(this, MainActivity.class);
		// Tell the game to display the tutorial
		i.putExtra("tutorial?", true);
		startActivityForResult(i, 100);
	}

	/**
	 * Called when the shop button is pressed
	 */
	public void viewShop(View v) {
		startActivity(new Intent(this, ShopActivity.class));
	}

}
