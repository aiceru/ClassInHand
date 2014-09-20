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
        wDB = getWritableDatabase();
        rDB = getReadableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
        db.execSQL(ClassDBContract.SQL_CREATE_TABLE_STUDENTINFO);
        db.execSQL(ClassDBContract.SQL_CREATE_TABLE_SEATHISTORY);
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

	public long insert(Seat seat, int pairedStudentId, long date) {
		ContentValues values = new ContentValues();
		values.put(ClassDBContract.SeatHistory.COLUMN_NAME_SEAT_ID, seat.getId());
        values.put(ClassDBContract.SeatHistory.COLUMN_NAME_DATE, date);
        if(seat.getItsStudent() != null) {
            values.put(ClassDBContract.SeatHistory.COLUMN_NAME_STUDENT_ID, seat.getItsStudent().getNum());
            values.put(ClassDBContract.SeatHistory.COLUMN_NAME_PAIR_STUDENT, pairedStudentId);
        }

		return wDB.insert(
				ClassDBContract.SeatHistory.TABLE_NAME,
				null,
				values
		);
	}

    public int update(Seat seat, int pairStudentId, long date) {
        int seatid = seat.getId();
        ContentValues values = new ContentValues();

        String whereClause =
                ClassDBContract.SeatHistory.COLUMN_NAME_SEAT_ID + " = ? AND " +
                ClassDBContract.SeatHistory.COLUMN_NAME_DATE + " = ?";
        String[] whereArgs = {
                String.valueOf(seatid),
                String.valueOf(date)
        };

        if(seat.getItsStudent() != null) {
            values.put(ClassDBContract.SeatHistory.COLUMN_NAME_STUDENT_ID, seat.getItsStudent().getNum());
            values.put(ClassDBContract.SeatHistory.COLUMN_NAME_PAIR_STUDENT, pairStudentId);
        }
        else {
            values.putNull(ClassDBContract.SeatHistory.COLUMN_NAME_STUDENT_ID);
            values.putNull(ClassDBContract.SeatHistory.COLUMN_NAME_PAIR_STUDENT);
        }

        return wDB.update(
                ClassDBContract.SeatHistory.TABLE_NAME,
                values,
                whereClause,
                whereArgs
        );
    }

	public Cursor getStudentsList() {
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

    public Cursor getStudent(int id) {
        String[] projection = {
                ClassDBContract.StudentInfo.COLUMN_NAME_STUDENT_NAME,
                ClassDBContract.StudentInfo.COLUMN_NAME_STUDENT_GENDER
        };
        String[] selectArg = {
                String.valueOf(id)
        };
        return rDB.query(
        /* TABLE        */  ClassDBContract.StudentInfo.TABLE_NAME,
        /* COLUMNS      */  projection,
        /* SELECTION    */  ClassDBContract.StudentInfo.COLUMN_NAME_STUDENT_ID + " = ?",
        /* SELECTARGS   */  selectArg,
        /* GROUP BY     */  null,
        /* HAVING       */  null,
        /* ORDER BY     */  null
        );
    }

    public Cursor getSavedDateList() {
        String[] projection = {
                ClassDBContract.SeatHistory.COLUMN_NAME_DATE
        };
        return rDB.query(
        /* TABLE        */  ClassDBContract.SeatHistory.TABLE_NAME,
        /* COLUMNS      */  projection,
        /* SELECTION    */  null,
        /* SELECTARGS   */  null,
        /* GROUP BY     */  ClassDBContract.SeatHistory.COLUMN_NAME_DATE,
        /* HAVING       */  null,
        /* ORDER BY     */  ClassDBContract.SeatHistory.COLUMN_NAME_DATE + " DESC"
        );
    }

	public Cursor getRecentSeatPlan() {
		String query = "SELECT * FROM " + ClassDBContract.SeatHistory.TABLE_NAME
				+ " WHERE " + ClassDBContract.SeatHistory.COLUMN_NAME_DATE
				+ " = (SELECT MAX(" + ClassDBContract.SeatHistory.COLUMN_NAME_DATE
				+ ") FROM " + ClassDBContract.SeatHistory.TABLE_NAME + ");";
		return rDB.rawQuery(query, null);
	}

    public Cursor getSeatPlan(long date) {
        String query = "SELECT * FROM " + ClassDBContract.SeatHistory.TABLE_NAME
                + " WHERE " + ClassDBContract.SeatHistory.COLUMN_NAME_DATE
                + " = " + String.valueOf(date) + ";";
        return rDB.rawQuery(query, null);
    }

    public Cursor getHistory(int studentId, long date) {
        String[] projection = {
                ClassDBContract.SeatHistory.COLUMN_NAME_SEAT_ID,
                ClassDBContract.SeatHistory.COLUMN_NAME_PAIR_STUDENT
        };
        String selection = ClassDBContract.SeatHistory.COLUMN_NAME_STUDENT_ID + " = ? AND " +
                           ClassDBContract.SeatHistory.COLUMN_NAME_DATE + " = ?";
        String[] selectionArgs = {
                String.valueOf(studentId),
                String.valueOf(date)
        };
        return rDB.query(
        /* TABLE        */  ClassDBContract.SeatHistory.TABLE_NAME,
        /* COLUMNS      */  projection,
        /* SELECTION    */  selection,
        /* SELECTARGS   */  selectionArgs,
        /* GROUP BY     */  null,
        /* HAVING       */  null,
        /* ORDER BY     */  null
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

    public int deleteSeatPlan(long date) {
        String selection = ClassDBContract.SeatHistory.COLUMN_NAME_DATE + " LIKE ?";
        String[] selectionArgs = {
                String.valueOf(date)
        };
        return wDB.delete(
                ClassDBContract.SeatHistory.TABLE_NAME,
                selection,
                selectionArgs
        );
    }

	public int deleteAllStudents() {
		return wDB.delete(
				ClassDBContract.StudentInfo.TABLE_NAME,
				null,
				null
		);
	}

	public int deleteAllSeats() {
		return wDB.delete(
				ClassDBContract.SeatHistory.TABLE_NAME,
				null,
				null
		);
	}
}
