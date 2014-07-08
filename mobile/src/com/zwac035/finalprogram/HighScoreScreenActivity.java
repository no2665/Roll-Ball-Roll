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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import com.zwac035.finalprogram.Database.HighScoreTable;

/**
 * Displays the high scores
 */
public class HighScoreScreenActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_high_score_screen);
		// Open the database
		Database db = new Database(this);
		SQLiteDatabase sqlDB = db.getReadableDatabase();

		// Get the high scores
		String[] select = { HighScoreTable.NAME, HighScoreTable.SCORE };
		String orderBy = HighScoreTable.SCORE + " DESC";
		Cursor c = sqlDB.query(HighScoreTable.TABLE_NAME, select, null, null,
				null, null, orderBy);

		c.moveToFirst();
		int nameIndex = c.getColumnIndex(HighScoreTable.NAME);
		int scoreIndex = c.getColumnIndex(HighScoreTable.SCORE);
		TextView names = (TextView) findViewById(R.id.txtName);
		TextView scores = (TextView) findViewById(R.id.txtScore);
		int i = 0;
		// Loop through the rows. Display a maximum of 10 scores
		while (!c.isAfterLast() && i++ < 15) {
			names.setText(names.getText() + "\n" + c.getString(nameIndex));
			scores.setText(scores.getText() + "\n" + c.getString(scoreIndex));
			c.moveToNext();
		}
		db.close();
	}

}
