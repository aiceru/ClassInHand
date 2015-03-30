package com.iceru.classinhand;

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
        db.execSQL(ClassDBContract.SQL_CREATE_TABLE_SEAPLANINFO);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public long insert(Student student) {
		ContentValues values = new ContentValues();
		values.put(ClassDBContract.StudentInfo.COLUMN_NAME_ID, student.getId());
        values.put(ClassDBContract.StudentInfo.COLUMN_NAME_ATTEND_NUM, student.getAttendNum());
        values.put(ClassDBContract.StudentInfo.COLUMN_NAME_NAME, student.getName());
        values.put(ClassDBContract.StudentInfo.COLUMN_NAME_GENDER, student.isBoy()? 1:2);
        values.put(ClassDBContract.StudentInfo.COLUMN_NAME_PHONE, student.getPhone());
        values.put(ClassDBContract.StudentInfo.COLUMN_NAME_IN_DATE, student.getInDate());
        values.put(ClassDBContract.StudentInfo.ColUMN_NAME_OUT_DATE, student.getOutDate());

		return wDB.insert(ClassDBContract.StudentInfo.TABLE_NAME, null, values);
	}

    public long insert(Seatplan plan) {
        long ret = 0;
        long applyDate = plan.getmApplyDate().getTimeInMillis();
        ContentValues values = new ContentValues();
        values.put(ClassDBContract.SeatplanInfo.COLUMN_NAME_APPLY_DATE, applyDate);
        values.put(ClassDBContract.SeatplanInfo.COLUMN_NAME_COLUMNS, plan.getmColumns());
        values.put(ClassDBContract.SeatplanInfo.COLUMN_NAME_IS_BOY_RIGHT, (plan.isBoyRight()? 1 : 0));
        values.put(ClassDBContract.SeatplanInfo.COLUMN_NAME_TOTAL_SEATS, plan.getmTotalSeats());
        ret |= wDB.insert(ClassDBContract.SeatplanInfo.TABLE_NAME, null, values);
        values.clear();

        for(Seat seat : plan.getmSeats()) {
            values.put(ClassDBContract.SeatHistory.COLUMN_NAME_ID, seat.getId());
            Student s = seat.getItsStudent();
            if(s != null) {
                values.put(ClassDBContract.SeatHistory.COLUMN_NAME_STUDENT_ID, s.getId());
            }
            else {
                values.putNull(ClassDBContract.SeatHistory.COLUMN_NAME_STUDENT_ID);
            }
            values.put(ClassDBContract.SeatHistory.COLUMN_NAME_APPLY_DATE, applyDate);

            ret |= wDB.insert(ClassDBContract.SeatHistory.TABLE_NAME, null, values);
            values.clear();
        }
        return ret;
    }

	/*public long insert(Seat seat, int pairedStudentId, long date) {
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
    */

	public Cursor getStudentsList() {
		String sortOrder =
				ClassDBContract.StudentInfo.COLUMN_NAME_ID + " ASC";

		return rDB.query(
				ClassDBContract.StudentInfo.TABLE_NAME,
				null, null, null, null, null,
				sortOrder
		);
	}
    /*

    public Cursor getStudent(int id) {
        String[] projection = {
                ClassDBContract.StudentInfo.COLUMN_NAME_STUDENT_NAME,
                ClassDBContract.StudentInfo.COLUMN_NAME_STUDENT_GENDER
        };
        String[] selectArg = {
                String.valueOf(id)
        };
        return rDB.query(
        *//* TABLE        *//*  ClassDBContract.StudentInfo.TABLE_NAME,
        *//* COLUMNS      *//*  projection,
        *//* SELECTION    *//*  ClassDBContract.StudentInfo.COLUMN_NAME_STUDENT_ID + " = ?",
        *//* SELECTARGS   *//*  selectArg,
        *//* GROUP BY     *//*  null,
        *//* HAVING       *//*  null,
        *//* ORDER BY     *//*  null
        );
    }
    */

    public Cursor getSavedDateList() {
        String[] projection = {
                ClassDBContract.SeatHistory.COLUMN_NAME_APPLY_DATE
        };
        return rDB.query(
        /* TABLE        */  ClassDBContract.SeatHistory.TABLE_NAME,
        /* COLUMNS      */  projection,
        /* SELECTION    */  null,
        /* SELECTARGS   */  null,
        /* GROUP BY     */  ClassDBContract.SeatHistory.COLUMN_NAME_APPLY_DATE,
        /* HAVING       */  null,
        /* ORDER BY     */  ClassDBContract.SeatHistory.COLUMN_NAME_APPLY_DATE + " DESC"
        );
    }

    /*
	public Cursor getRecentSeatPlan() {
		String query = "SELECT * FROM " + ClassDBContract.SeatHistory.TABLE_NAME
				+ " WHERE " + ClassDBContract.SeatHistory.COLUMN_NAME_DATE
				+ " = (SELECT MAX(" + ClassDBContract.SeatHistory.COLUMN_NAME_DATE
				+ ") FROM " + ClassDBContract.SeatHistory.TABLE_NAME + ");";
		return rDB.rawQuery(query, null);
	}
	*/

    public Cursor getSeatplan(long date) {
        String query = "SELECT * FROM " + ClassDBContract.SeatHistory.TABLE_NAME
                + " WHERE " + ClassDBContract.SeatHistory.COLUMN_NAME_APPLY_DATE
                + " = " + String.valueOf(date) + ";";
        return rDB.rawQuery(query, null);
    }

    public Cursor getSeatplanInfo(long date) {
        String query = "SELECT * FROM " + ClassDBContract.SeatplanInfo.TABLE_NAME
                + " WHERE " + ClassDBContract.SeatHistory.COLUMN_NAME_APPLY_DATE
                + " = " + String.valueOf(date) + ";";
        return rDB.rawQuery(query, null);
    }

    public Cursor getHistory(int studentId) {
        String[] projection = {
                ClassDBContract.SeatHistory.COLUMN_NAME_APPLY_DATE,
                ClassDBContract.SeatHistory.COLUMN_NAME_ID
        };
        String selection =
                ClassDBContract.SeatHistory.COLUMN_NAME_STUDENT_ID + " = ?";
        String[] selectionArgs = {
                String.valueOf(studentId)
        };

        return rDB.query(
        /* TABLE        */  ClassDBContract.SeatHistory.TABLE_NAME,
        /* COLUMNS      */  projection,
        /* SELECTION    */  selection,
        /* SELECTARGS   */  selectionArgs,
        /* GROUP BY     */  null,
        /* HAVING       */  null,
        /* ORDER BY     */  ClassDBContract.SeatHistory.COLUMN_NAME_APPLY_DATE + " DESC"
        );
    }

    public int getSeatedStudentId(int seatId, long date) {
        String selection =
                ClassDBContract.SeatHistory.COLUMN_NAME_ID + " = ? AND " +
                ClassDBContract.SeatHistory.COLUMN_NAME_APPLY_DATE + " = ?";
        String[] selectionArgs = {
                String.valueOf(seatId),
                String.valueOf(date)
        };
        Cursor c = rDB.query(
                ClassDBContract.SeatHistory.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        // TODO : c.getcount != 1 일 경우 예외 throw
        if(c.moveToFirst())
            return c.getInt(c.getColumnIndexOrThrow(ClassDBContract.SeatHistory.COLUMN_NAME_STUDENT_ID));
        else
            return -1;
    }

    /*
    public Cursor getHistory(int studentId, long date) {
        String[] projection = {
                ClassDBContract.SeatHistory.COLUMN_NAME_SEAT_ID,
                ClassDBContract.SeatHistory.COLUMN_NAME_PAIR_STUDENT,
                ClassDBContract.SeatHistory.COLUMN_NAME_DATE
        };
        String selection =
                ClassDBContract.SeatHistory.COLUMN_NAME_STUDENT_ID + " = ? AND " +
                ClassDBContract.SeatHistory.COLUMN_NAME_DATE + " = ?";
        String[] selectionArgs = {
                String.valueOf(studentId),
                String.valueOf(date)
        };

        return rDB.query(
        *//* TABLE        *//*  ClassDBContract.SeatHistory.TABLE_NAME,
        *//* COLUMNS      *//*  projection,
        *//* SELECTION    *//*  selection,
        *//* SELECTARGS   *//*  selectionArgs,
        *//* GROUP BY     *//*  null,
        *//* HAVING       *//*  null,
        *//* ORDER BY     *//*  null
        );
    }*/

	public void delete(Student student) {
		String selectionFromStudentInfo = ClassDBContract.StudentInfo.COLUMN_NAME_ID + " LIKE ?";
        String selectionFromSeatHistory = ClassDBContract.SeatHistory.COLUMN_NAME_STUDENT_ID + " LIKE ?";
		String[] selectionArgs = {
				String.valueOf(student.getId())
		};

		wDB.delete(
				ClassDBContract.StudentInfo.TABLE_NAME,
				selectionFromStudentInfo,
				selectionArgs
		);

        ContentValues values = new ContentValues();
        values.putNull(ClassDBContract.SeatHistory.COLUMN_NAME_STUDENT_ID);
        wDB.update(
                ClassDBContract.SeatHistory.TABLE_NAME,
                values,
                selectionFromSeatHistory,
                selectionArgs
        );
	}

    public void update(Student student) {
        /* Student's ID should NOT change. */
        String selection = ClassDBContract.StudentInfo.COLUMN_NAME_ID + " LIKE?";
        String[] selectionArgs = {
                String.valueOf(student.getId())
        };
        ContentValues values = new ContentValues();
        values.put(ClassDBContract.StudentInfo.COLUMN_NAME_ATTEND_NUM, student.getAttendNum());
        values.put(ClassDBContract.StudentInfo.COLUMN_NAME_NAME, student.getName());
        values.put(ClassDBContract.StudentInfo.COLUMN_NAME_GENDER, student.isBoy()? 1:2);
        values.put(ClassDBContract.StudentInfo.COLUMN_NAME_PHONE, student.getPhone());
        values.put(ClassDBContract.StudentInfo.COLUMN_NAME_IN_DATE, student.getInDate());
        values.put(ClassDBContract.StudentInfo.ColUMN_NAME_OUT_DATE, student.getOutDate());
        wDB.update(
                ClassDBContract.StudentInfo.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
    }

    /*
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
	*/

    public int deleteSeatPlan(long date) {
        int ret = 0;
        String selection = ClassDBContract.SeatHistory.COLUMN_NAME_APPLY_DATE + " LIKE ?";
        String[] selectionArgs = {
                String.valueOf(date)
        };
        ret |= wDB.delete(
                ClassDBContract.SeatplanInfo.TABLE_NAME,
                selection,
                selectionArgs
        );
        ret |= wDB.delete(
                ClassDBContract.SeatHistory.TABLE_NAME,
                selection,
                selectionArgs
        );
        return ret;
    }

	public int deleteAllStudents() {
		return wDB.delete(
				ClassDBContract.StudentInfo.TABLE_NAME,
				null,
				null
		);
	}

	public int deleteAllSeatplans() {
        int ret = 0;
        ret |= wDB.delete(
                ClassDBContract.SeatplanInfo.TABLE_NAME,
                null,
                null
        );
		ret |= wDB.delete(
				ClassDBContract.SeatHistory.TABLE_NAME,
				null,
				null
		);
        return ret;
	}
}
