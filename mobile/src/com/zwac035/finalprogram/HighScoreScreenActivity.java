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
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
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
		TableLayout highScoreTable = (TableLayout) findViewById(R.id.highScoreTable);
		int i = 0;
		// Loop through the rows. Display a maximum of 25 scores
		while (!c.isAfterLast() && i++ < 25) {

			TextView name = new TextView(this);
			name.setText(c.getString(nameIndex));
			name.setTextAppearance(this, android.R.attr.textAppearanceMedium);
			name.setTextColor(Color.BLACK);

			TextView score = new TextView(this);
			score.setText(c.getString(scoreIndex));
			score.setTextAppearance(this, android.R.attr.textAppearanceMedium);
			score.setTextColor(Color.BLACK);

			TableRow newRow = new TableRow(this);
			newRow.addView(name);
			newRow.addView(score);

			highScoreTable.addView(newRow);

			c.moveToNext();
		}
		db.close();
	}
}
