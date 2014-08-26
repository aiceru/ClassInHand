package com.iceru.teacherschores;

/**
 * Created by iceru on 14. 8. 23.
 */
public final class ClassDBContract {
	public ClassDBContract() {}

	public static abstract class StudentInfo {
		public static final String TABLE_NAME = "student_info";
		public static final String COLUMN_NAME_STUDENT_ID = "student_id";
		public static final String COLUMN_NAME_STUDENT_NAME = "name";
		public static final String COLUMN_NAME_STUDENT_GENDER = "gender";
	}

	public static abstract class SeatHistory {
		public static final String TABLE_NAME = "seat_history";
		public static final String COLUMN_NAME_SEAT_ID = "seat_id";
		public static final String COLUMN_NAME_STUDENT_ID = "student_id";
		public static final String COLUMN_NAME_DATE = "date";

		public static final String COLUMN_NAME_NULLABLE =
				SeatHistory.COLUMN_NAME_STUDENT_ID;
	}

	private static final String TEXT_TYPE = " TEXT";
	private static final String INTEGER_TYPE = " INTEGER";
	private static final String COMMA_SEP = ",";
	private static final String NOT_NULL = " NOT NULL";

	public static final String SQL_CREATE_TABLE_STUDENTINFO =
			"CREATE TABLE " + StudentInfo.TABLE_NAME + " (" +
					StudentInfo.COLUMN_NAME_STUDENT_ID + INTEGER_TYPE + " PRIMARY KEY," +
					StudentInfo.COLUMN_NAME_STUDENT_NAME + TEXT_TYPE + COMMA_SEP +
					StudentInfo.COLUMN_NAME_STUDENT_GENDER + INTEGER_TYPE +
			" )";

	public static final String SQL_CREATE_TABLE_SEATHISTORY =
			"CREATE TABLE " + SeatHistory.TABLE_NAME + " (" +
					SeatHistory.COLUMN_NAME_SEAT_ID + INTEGER_TYPE + COMMA_SEP +
					SeatHistory.COLUMN_NAME_STUDENT_ID + INTEGER_TYPE + COMMA_SEP +
					SeatHistory.COLUMN_NAME_DATE + INTEGER_TYPE +
			" )";

	public static final String SQL_DELETE_TABLE_STUDENTINFO =
			"DROP TABLE IF EXISTS " + StudentInfo.TABLE_NAME;

	public static final String SQL_DELETE_TABLE_SEATHISTORY =
			"DROP TABLE IF EXISTS " + SeatHistory.TABLE_NAME;
}
