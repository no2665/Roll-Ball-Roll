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
import android.app.AlertDialog;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zwac035.finalprogram.Database.SkinsTable;
import com.zwac035.finalprogram.Database.TrianglesTable;

/**
 * Screen where the user can select and purchase skins for the ball
 */
public class ShopActivity extends Activity {

	private String selectedSkin;
	private Button selectedBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop);
		// Open the database
		Database db = new Database(this);
		SQLiteDatabase sqlDb = db.getWritableDatabase();

		// Get the skins table
		String[] select = new String[] { SkinsTable.SKIN_NAME,
				SkinsTable.BOUGHT, SkinsTable.SELECTED };
		Cursor c = sqlDb.query(SkinsTable.TABLE_NAME, select, null, null, null,
				null, null);
		c.moveToFirst();
		int nameIndex = c.getColumnIndex(SkinsTable.SKIN_NAME);
		int boughtIndex = c.getColumnIndex(SkinsTable.BOUGHT);
		int selectedIndex = c.getColumnIndex(SkinsTable.SELECTED);

		Resources r = getResources();
		// Loop through the skins table
		while (!c.isAfterLast()) {
			String skinName = c.getString(nameIndex);
			// Find the button that is next to the skin
			int id = r.getIdentifier(skinName + "Btn", "id",
					this.getPackageName());
			int bought = c.getInt(boughtIndex);
			int selected = c.getInt(selectedIndex);
			// If the skin has been purchased
			if (bought > 0) {
				((Button) findViewById(id)).setText(R.string.use_skin);
			}
			// If the skin is selected
			if (selected > 0) {
				selectedSkin = skinName;
				selectedBtn = (Button) findViewById(id);
				selectedBtn.setText(R.string.selected_skin);
			}
			c.moveToNext();
		}

		// Get the amount of triangles collected
		select = new String[] { TrianglesTable.COLLECTED };
		c = sqlDb.query(TrianglesTable.TABLE_NAME, select, null, null, null,
				null, null);
		int numTriangles = 0;
		if (c.getCount() > 0) {
			c.moveToFirst();
			numTriangles = c.getInt(c.getColumnIndex(TrianglesTable.COLLECTED));
		}
		// Display the amount of triangles collected
		TextView t = (TextView) findViewById(R.id.numTrianglesTxt);
		t.setText(getString(R.string.num_triangles) + " " + numTriangles);
		db.close();

	}

	/**
	 * Called when a user clicks a button
	 */
	public void useOrBuySkin(View v) {
		Button b = (Button) v;
		String s = (String) b.getText();
		// If the skin hasn't been purchased yet
		if (s.equals(getString(R.string.buy_skin))) {
			// Check that we have enough triangles
			Database db = new Database(this);
			SQLiteDatabase sqlDb = db.getWritableDatabase();
			String[] select = { TrianglesTable.COLLECTED };
			Cursor c = sqlDb.query(TrianglesTable.TABLE_NAME, select, null,
					null, null, null, null);
			// If we don't have enough triangles
			if (c.getCount() < 1) {
				alertNotEnoughTriangles();
				return;
			}
			c.moveToFirst();
			int collectedInd = c.getColumnIndex(TrianglesTable.COLLECTED);

			int collected = c.getInt(collectedInd);

			// Get the price of the skin
			String btnName = getResources().getResourceEntryName(b.getId());
			String skinName = btnName.substring(0, btnName.length() - 3);
			select = new String[] { SkinsTable.PRICE };
			String where = SkinsTable.SKIN_NAME + " = '" + skinName + "'";
			c = sqlDb.query(SkinsTable.TABLE_NAME, select, where, null, null,
					null, null);
			c.moveToFirst();
			int priceInd = c.getColumnIndex(SkinsTable.PRICE);
			int skinPrice = c.getInt(priceInd);
			// If you don't have enough triangles to buy the skin
			if (collected < skinPrice) {
				alertNotEnoughTriangles();
				return;
			}

			// Buy the skin
			sqlDb.execSQL("UPDATE " + SkinsTable.TABLE_NAME + " SET "
					+ SkinsTable.BOUGHT + " = 1 WHERE " + SkinsTable.SKIN_NAME
					+ " = '" + skinName + "';");

			// Deduct the triangles
			int newNumTriangles = collected - skinPrice;
			sqlDb.execSQL("UPDATE " + TrianglesTable.TABLE_NAME + " SET "
					+ TrianglesTable.COLLECTED + " = " + newNumTriangles + ";");

			// Display the new number of collected triangles
			TextView t = (TextView) findViewById(R.id.numTrianglesTxt);
			t.setText(getString(R.string.num_triangles) + " " + newNumTriangles);

			// Select the skin
			selectSkin(b, sqlDb);
			db.close();

			// If the user wants to select the skin
		} else if (s.equals(getString(R.string.use_skin))) {
			// Then we need to select the skin
			Database db = new Database(this);
			SQLiteDatabase sqlDb = db.getWritableDatabase();
			selectSkin(b, sqlDb);
			db.close();
		}
	}

	private void alertNotEnoughTriangles() {
		// Display an alert
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder
				.setMessage(
						"You don't have enough triangles, go and collect some more!")
				.setTitle("").setPositiveButton("Okay", null);
		alertBuilder.show();
	}

	private void selectSkin(Button b, SQLiteDatabase sqlDb) {
		String btnName = getResources().getResourceEntryName(b.getId());
		String skinName = btnName.substring(0, btnName.length() - 3);
		// Select the skin
		sqlDb.execSQL("UPDATE " + SkinsTable.TABLE_NAME + " SET "
				+ SkinsTable.SELECTED + " = 1 " + "WHERE "
				+ SkinsTable.SKIN_NAME + " = '" + skinName + "';");
		b.setText(R.string.selected_skin);
		// And deselect the previous skin
		sqlDb.execSQL("UPDATE " + SkinsTable.TABLE_NAME + " SET "
				+ SkinsTable.SELECTED + " = 0 " + "WHERE "
				+ SkinsTable.SKIN_NAME + " = '" + selectedSkin + "';");
		selectedBtn.setText(R.string.use_skin);
		// Reassign the newly selected buttons.
		selectedBtn = b;
		selectedSkin = skinName;
	}

}
