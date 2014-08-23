package com.iceru.teacherschores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by iceru on 14. 8. 23.
 */
public class ClassDBHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "Class.db";
	private SQLiteDatabase wDB;
	private SQLiteDatabase rDB;

	public ClassDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(ClassDBContract.SQL_CREATE_TABLE_STUDENTINFO);
		db.execSQL(ClassDBContract.SQL_CREATE_TABLE_SEATHISTORY);
		wDB = this.getWritableDatabase();
		rDB = this.getReadableDatabase();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public long insert(Student student) {
		ContentValues values = new ContentValues();
		values.put(ClassDBContract.StudentInfo.COLUMN_NAME_STUDENT_ID, student.getNum());
		values.put(ClassDBContract.StudentInfo.COLUMN_NAME_STUDENT_NAME, student.getName());
		values.put(ClassDBContract.StudentInfo.COLUMN_NAME_STUDENT_GENDER, student.isBoy()? 1 : 2);

		return wDB.insert(ClassDBContract.StudentInfo.TABLE_NAME, null, values);
	}

	public long insert(Seat seat, int month) {
		ContentValues values = new ContentValues();
		values.put(ClassDBContract.SeatHistory.COLUMN_NAME_SEAT_ID, seat.getId());
		values.put(ClassDBContract.SeatHistory.COLUMN_NAME_MONTH, month);
		if(seat.getItsStudent() != null) {
			values.put(ClassDBContract.SeatHistory.COLUMN_NAME_STUDENT_ID, seat.getItsStudent().getNum());
		}

		return wDB.insert(
				ClassDBContract.SeatHistory.TABLE_NAME,
				ClassDBContract.SeatHistory.COLUMN_NAME_NULLABLE,
				values
		);
	}

	public Cursor getStudents() {
		String[] projection = {
				ClassDBContract.StudentInfo.COLUMN_NAME_STUDENT_ID,
				ClassDBContract.StudentInfo.COLUMN_NAME_STUDENT_NAME,
				ClassDBContract.StudentInfo.COLUMN_NAME_STUDENT_GENDER
		};
		String sortOrder =
				ClassDBContract.StudentInfo.COLUMN_NAME_STUDENT_ID + " ASC";

		return rDB.query(
				ClassDBContract.StudentInfo.TABLE_NAME,
				projection,
				null, null, null, null,
				sortOrder
		);
	}

	public Cursor getSeats() {
		String[] projection = {
				ClassDBContract.SeatHistory.COLUMN_NAME_SEAT_ID,
				ClassDBContract.SeatHistory.COLUMN_NAME_STUDENT_ID,
				ClassDBContract.SeatHistory.COLUMN_NAME_MONTH
		};
		String sortOrder =
				ClassDBContract.SeatHistory.COLUMN_NAME_SEAT_ID + " ASC";

		return rDB.query(
				ClassDBContract.SeatHistory.TABLE_NAME,
				projection,
				null, null, null, null,
				sortOrder
		);
	}

	public int delete(Student student) {
		String selection = ClassDBContract.StudentInfo.COLUMN_NAME_STUDENT_ID + " LIKE ?";
		String[] selectionArgs = {
				String.valueOf(student.getNum())
		};

		return wDB.delete(
				ClassDBContract.StudentInfo.TABLE_NAME,
				selection,
				selectionArgs
		);
	}

	public int delete(Seat seat) {
		String selection = ClassDBContract.SeatHistory.COLUMN_NAME_SEAT_ID + " LIKE ?";
		String[] selectionArgs = {
				String.valueOf(seat.getId())
		};

		return wDB.delete(
				ClassDBContract.SeatHistory.TABLE_NAME,
				selection,
				selectionArgs
		);
	}
}