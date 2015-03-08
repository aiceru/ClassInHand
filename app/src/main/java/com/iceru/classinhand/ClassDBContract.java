package com.iceru.classinhand;

/**
 * Created by iceru on 14. 8. 23.
 */
public final class ClassDBContract {
	public ClassDBContract() {}

	public static abstract class StudentInfo {
		public static final String TABLE_NAME = "student_info";
		public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_ATTEND_NUM = "attend_num";
		public static final String COLUMN_NAME_NAME = "name";
		public static final String COLUMN_NAME_GENDER = "gender";
        public static final String COLUMN_NAME_IN_DATE = "in_date";
        public static final String ColUMN_NAME_OUT_DATE = "out_date";
	}

	public static abstract class SeatHistory {
		public static final String TABLE_NAME = "seat_history";
		public static final String COLUMN_NAME_ID = "id";
		public static final String COLUMN_NAME_STUDENT_ID = "student_id";
		public static final String COLUMN_NAME_APPLY_DATE = "apply_date";
	}

    public static abstract class SeatplanInfo {
        public static final String TABLE_NAME = "seatplan_info";
        public static final String COLUMN_NAME_APPLY_DATE = "apply_date";
        public static final String COLUMN_NAME_COLUMNS = "columns";
    }

	private static final String TEXT_TYPE = " TEXT";
	private static final String INTEGER_TYPE = " INTEGER";
	private static final String COMMA_SEP = ",";
	private static final String NOT_NULL = " NOT NULL";

	public static final String SQL_CREATE_TABLE_STUDENTINFO =
			"CREATE TABLE " + StudentInfo.TABLE_NAME + " (" +
					StudentInfo.COLUMN_NAME_ID + INTEGER_TYPE + " PRIMARY KEY ASC" + COMMA_SEP +
                    StudentInfo.COLUMN_NAME_ATTEND_NUM + INTEGER_TYPE + COMMA_SEP +
					StudentInfo.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
					StudentInfo.COLUMN_NAME_GENDER + INTEGER_TYPE + COMMA_SEP +
                    StudentInfo.COLUMN_NAME_IN_DATE + INTEGER_TYPE + COMMA_SEP +
                    StudentInfo.ColUMN_NAME_OUT_DATE + INTEGER_TYPE +
			" )";

	public static final String SQL_CREATE_TABLE_SEATHISTORY =
			"CREATE TABLE " + SeatHistory.TABLE_NAME + " (" +
					SeatHistory.COLUMN_NAME_ID + INTEGER_TYPE + COMMA_SEP +
					SeatHistory.COLUMN_NAME_STUDENT_ID + INTEGER_TYPE + COMMA_SEP +
					SeatHistory.COLUMN_NAME_APPLY_DATE + INTEGER_TYPE +
			" )";

    public static final String SQL_CREATE_TABLE_SEAPLANINFO =
            "CREATE TABLE " + SeatplanInfo.TABLE_NAME + " (" +
                    SeatplanInfo.COLUMN_NAME_APPLY_DATE + INTEGER_TYPE + COMMA_SEP +
                    SeatplanInfo.COLUMN_NAME_COLUMNS + INTEGER_TYPE +
            " )";

	public static final String SQL_DELETE_TABLE_STUDENTINFO =
			"DROP TABLE IF EXISTS " + StudentInfo.TABLE_NAME;

	public static final String SQL_DELETE_TABLE_SEATHISTORY =
			"DROP TABLE IF EXISTS " + SeatHistory.TABLE_NAME;

    public static final String SQL_DELETE_TABLE_SEATPLANINFO =
            "DROP TABLE IF EXISTS " + SeatplanInfo.TABLE_NAME;
}
