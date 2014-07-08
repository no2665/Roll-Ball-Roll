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

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Class that creates and sets up the sqlite database that stores the high
 * scores, skins, and collected triangles
 */
public class Database extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "Roll Ball Roll";
	private static final int DATABASE_VERSION = 1;

	/**
	 * A table to store the high scores in.
	 */
	public static class HighScoreTable implements BaseColumns {
		public static final String TABLE_NAME = "high_score";
		public static final String SCORE = "score";
		public static final String NAME = "name";
	}

	/**
	 * A table to store the last players name in
	 */
	public static class LastNameTable implements BaseColumns {
		public static final String TABLE_NAME = "last_name_entered";
		public static final String NAME = "name";
	}

	/**
	 * Table for the collected triangles
	 */
	public static class TrianglesTable implements BaseColumns {
		public static final String TABLE_NAME = "triangles";
		public static final String COLLECTED = "amount_collected";
	}

	/**
	 * Table for the skins
	 */
	public static class SkinsTable implements BaseColumns {
		public static final String TABLE_NAME = "skins";
		public static final String SKIN_NAME = "name";
		public static final String BOUGHT = "bought";
		public static final String SELECTED = "selected";
		public static final String PRICE = "price";
	}

	public Database(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Create the high score table
		db.execSQL("CREATE TABLE " + HighScoreTable.TABLE_NAME + " ("
				+ HighScoreTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ HighScoreTable.NAME + " TEXT, " + HighScoreTable.SCORE
				+ " INTEGER " + ");");
		// Create the last name entered table
		db.execSQL("CREATE TABLE " + LastNameTable.TABLE_NAME + " ("
				+ LastNameTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ LastNameTable.NAME + " TEXT" + ");");
		ContentValues c = new ContentValues();
		c.put(LastNameTable.NAME, "");
		db.insert(LastNameTable.TABLE_NAME, null, c);
		// Create the triangles table
		db.execSQL("CREATE TABLE " + TrianglesTable.TABLE_NAME + " ("
				+ TrianglesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ TrianglesTable.COLLECTED + " INTEGER " + ");");
		c.clear();
		// Set the collected triangles to 0
		c.put(TrianglesTable.COLLECTED, 0);
		db.insert(TrianglesTable.TABLE_NAME, "null", c);
		// Create the skins table
		db.execSQL("CREATE TABLE " + SkinsTable.TABLE_NAME + " ("
				+ SkinsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ SkinsTable.SKIN_NAME + " TEXT," + SkinsTable.BOUGHT
				+ " BOOLEAN, " + SkinsTable.SELECTED + " BOOLEAN,"
				+ SkinsTable.PRICE + " INTEGER " + ");");
		c.clear();
		// Put in the red skin
		c.put(SkinsTable.SKIN_NAME, "Red");
		c.put(SkinsTable.BOUGHT, 1);
		c.put(SkinsTable.SELECTED, 1);
		c.put(SkinsTable.PRICE, 0);
		db.insert(SkinsTable.TABLE_NAME, "null", c);

		c.clear();
		// Put in the blue skin
		c.put(SkinsTable.SKIN_NAME, "Blue");
		c.put(SkinsTable.BOUGHT, 1);
		c.put(SkinsTable.SELECTED, 0);
		c.put(SkinsTable.PRICE, 0);
		db.insert(SkinsTable.TABLE_NAME, "null", c);

		c.clear();
		// Put in the green skin
		c.put(SkinsTable.SKIN_NAME, "Green");
		c.put(SkinsTable.BOUGHT, 0);
		c.put(SkinsTable.SELECTED, 0);
		c.put(SkinsTable.PRICE, 50);
		db.insert(SkinsTable.TABLE_NAME, "null", c);

		c.clear();
		// Put in the basket ball skin
		c.put(SkinsTable.SKIN_NAME, "Basket");
		c.put(SkinsTable.BOUGHT, 0);
		c.put(SkinsTable.SELECTED, 0);
		c.put(SkinsTable.PRICE, 100);
		db.insert(SkinsTable.TABLE_NAME, "null", c);

		c.clear();
		// Put in the beach ball skin
		c.put(SkinsTable.SKIN_NAME, "Beach");
		c.put(SkinsTable.BOUGHT, 0);
		c.put(SkinsTable.SELECTED, 0);
		c.put(SkinsTable.PRICE, 100);
		db.insert(SkinsTable.TABLE_NAME, "null", c);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
