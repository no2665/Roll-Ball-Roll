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
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.zwac035.finalprogram.Database.HighScoreTable;
import com.zwac035.finalprogram.Database.LastNameTable;

/**
 * Screen that is shown when the game is over that asks the player to enter
 * their name for the high score table
 */
public class EnterNameActivity extends Activity implements
		OnEditorActionListener {

	private float score = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_name);
		// Get the game score
		score = getIntent().getFloatExtra("Score", 0);

		// Get the last name entered
		Database db = new Database(this);
		SQLiteDatabase sqlDB = db.getWritableDatabase();
		String[] select = { LastNameTable.NAME };
		Cursor c = sqlDB.query(LastNameTable.TABLE_NAME, select, null, null,
				null, null, null);
		if (c.getCount() != 0) {
			c.moveToFirst();
			int nameInd = c.getColumnIndex(LastNameTable.NAME);
			String name = c.getString(nameInd);
			// Put the name into the textview
			EditText nameView = (EditText) findViewById(R.id.txtEnterName);
			nameView.setText(name);
			nameView.selectAll();
		}

		db.close();

		// Display the score
		TextView yourScore = (TextView) findViewById(R.id.txtYourScoreWas);
		yourScore.setText(yourScore.getText() + "\n" + score);
		// Set the keyboard to display Done in the bottom right corner
		EditText nameView = (EditText) findViewById(R.id.txtEnterName);
		nameView.setImeActionLabel("Done", KeyEvent.KEYCODE_ENTER);
		nameView.setOnEditorActionListener(this);
	}

	public void enterName(View v) {
		String name = ((TextView) findViewById(R.id.txtEnterName)).getText()
				.toString();
		if (name.equals("")) {
			// TODO Tell the user to enter a name
		} else {
			// Put the users name and score into the database
			Database db = new Database(this);
			SQLiteDatabase sqlDB = db.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(HighScoreTable.NAME, name);
			values.put(HighScoreTable.SCORE, score);
			sqlDB.insert(HighScoreTable.TABLE_NAME, null, values);

			// Put the name into the last name table
			sqlDB.execSQL("UPDATE " + LastNameTable.TABLE_NAME + " SET "
					+ LastNameTable.NAME + " = ? WHERE " + LastNameTable._ID
					+ " = 1", new String[] { name });
			db.close();

			// Start the high score table activity
			Intent i = new Intent(this, HighScoreScreenActivity.class);
			startActivity(i);
		}
	}

	/**
	 * Called when the done button is pressed
	 */
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		// this might not be necessary, but just in case
		runOnUiThread(new Runnable() {
			public void run() {
				enterName(null);
			}
		});
		return true;
	}

}
