package com.iceru.classinhand;

import android.app.Application;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by iceru on 14. 10. 19..
 */

@ReportsCrashes(
        formKey = "",
        formUri = "http://aiceru.iriscouch.com/acra-classinhand/_design/acra-storage/_update/report",
        httpMethod = HttpSender.Method.PUT,
        reportType = HttpSender.Type.JSON,
        formUriBasicAuthLogin = "tester_classinhand",
        formUriBasicAuthPassword = "classinhandTester",
        mode = ReportingInteractionMode.DIALOG,
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = R.drawable.ic_error_custom_24px, //optional. default is a warning sign
        resDialogTitle = R.string.crash_dialog_title, // optional. default is your application name
        //resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. when defined, adds a user text field input with this text resource as a label
        resDialogOkToast = R.string.crash_dialog_ok_toast // optional. displays a Toast message when the user accepts to send a report.
)
public class ClassInHandApplication extends Application {

    /* Global Constants */
    public static final int MAX_STUDENTS = 99;
    public static final byte SEATED_LEFT = 0x01;     // bit flag : 0000 0001
    public static final byte SEATED_RIGHT = 0x02;    // bit flag : 0000 0010

    public static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    public static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    public static final String SEATPLAN_SELECTED_POSITION = "com.iceru.classinhand.SEATPLAN_SELECTED_POSITION";
    public static final String STUDENT_SELECTED_ID = "com.iceru.classinhand.STUDENT_SELECTED_ID";
    public static final String SEATPLAN_EDIT_NEWDATE = "com.iceru.classinhand.SEATPLAN_EDIT_NEWDATE";
    public static final String SEATPLAN_EDIT_OLDDATE = "com.iceru.classinhand.SEATPLAN_EDIT_OLDDATE";

    /* Global Variables */
    public GlobalProperties globalProperties;
    public static int       NEXT_ID;

    private static ClassInHandApplication appInstance;

    private ArrayList<Student>          mStudents;
    private ArrayList<Seatplan>         mSeatplans;

    private ClassDBHelper   dbHelper;

    private Comparator<Student>     mStudentComparator;
    private Comparator<Seatplan>    mSeatplanComparator;
    private GregorianCalendar       mCalToday;

    @Override
    public final void onCreate() {
        super.onCreate();
        ACRA.init(this);

        mCalToday = new GregorianCalendar();
        clearTime(mCalToday);

        appInstance = this;
        dbHelper = new ClassDBHelper(this);

        mStudents = new ArrayList<>();
        mSeatplans = new ArrayList<>();

        mStudentComparator = new Comparator<Student>() {
            @Override
            public int compare(Student lhs, Student rhs) {
                if(lhs.isInClass() == rhs.isInClass()) {
                    return lhs.getAttendNum() - rhs.getAttendNum();
                }
                else if(lhs.isInClass()) {
                    return -1;
                }
                else {
                    return 1;
                }
            }
        };
        mSeatplanComparator = new Comparator<Seatplan>() {
            @Override
            public int compare(Seatplan lhs, Seatplan rhs) {
                long diff = lhs.getmApplyDate().getTimeInMillis() - rhs.getmApplyDate().getTimeInMillis();
                return diff > 0 ? -1 : 1;
            }
        };

        globalProperties = new GlobalProperties();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        globalProperties.num_histories = Integer.parseInt(prefs.getString(getString(R.string.sharedpref_key_num_histories), "3"));
        globalProperties.columns = Integer.parseInt(prefs.getString(getString(R.string.sharedpref_key_columns), "6"));
        globalProperties.rows = Integer.parseInt(prefs.getString(getString(R.string.sharedpref_key_rows), "5"));
        globalProperties.isBoyRight = Boolean.parseBoolean(prefs.getString(getString(R.string.sharedpref_key_is_boy_right), "true"));

        rebuildAllData();
    }

    public static ClassInHandApplication getInstance() {
        return appInstance;
    }

    public ArrayList<Student> getmStudents() {
        return mStudents;
    }

    public ArrayList<Seatplan> getmSeatplans() {
        return mSeatplans;
    }

    public ClassDBHelper getDbHelper() {
        return dbHelper;
    }

    public GregorianCalendar getValueOfTodayCalendar() {
        GregorianCalendar c = new GregorianCalendar();
        c.setTimeInMillis(mCalToday.getTimeInMillis());
        return c;
    }

    public boolean addStudent(Student student) {
        boolean exist = 0 <= mStudents.indexOf(student);
        if(!exist) {
            mStudents.add(student);
            Collections.sort(mStudents, mStudentComparator);
            dbHelper.insert(student);
        }
        return !exist;
    }

    public void addStudentAll(TreeMap<Integer, Student> addingStudents) {
        for(Map.Entry<Integer, Student> entry : addingStudents.entrySet()) {
            Student student = entry.getValue();
            addStudent(student);
        }
    }

    public Student findStudentById(int id) {
        //return mStudents.get(id);
        for(Student s : mStudents) {
            if(s.getId() == id) return s;
        }
        return null;
    }

    public Student findStudentByAttendNum(int attendNum) {
        //return mCurrentStudents.get(attendNum);
        for(Student s : mStudents) {
            if(s.getAttendNum() == attendNum) return s;
        }
        return null;
    }

    public boolean removeStudent(Student student) {
        //boolean success = null != mStudents.remove(student.getId());
        boolean success = mStudents.remove(student);
        if(success) {
            //mCurrentStudents.remove(student.getAttendNum());
            dbHelper.delete(student);
        }
        return success;
    }

    public void removeAllStudents() {
        mStudents.clear();
        dbHelper.deleteAllStudents();
    }

    public void addSeatplan(Seatplan plan) {
        mSeatplans.add(plan);
        Collections.sort(mSeatplans, mSeatplanComparator);
        dbHelper.insert(plan);
        initHistories();
    }

    public void removeSeatplan(Seatplan plan) {
        mSeatplans.remove(plan);
        Collections.sort(mSeatplans, mSeatplanComparator);
        dbHelper.deleteSeatPlan(plan.getmApplyDate().getTimeInMillis());
        initHistories();
    }

    public void removeSeatplan(GregorianCalendar cal) {
        for(Seatplan plan : mSeatplans) {
            if(plan.getmApplyDate().equals(cal))  {
                removeSeatplan(plan);
                break;
            }
        }
    }

    public void removeAllSeatplans() {
        mSeatplans.clear();
        dbHelper.deleteAllSeatplans();
    }

    public Seatplan findSeatplan(GregorianCalendar cal) {
        for(Seatplan plan : mSeatplans) {
            if(plan.getmApplyDate().equals(cal)) return plan;
        }
        return null;
    }

    /**
     * DB에서 학생 리스트를 읽어 mStudents 트리맵을 구성한다.
     */
    private void initStudentList() {
        int id, attendNum;
        String name, phone;
        boolean isBoy;
        long inDate, outDate, today;

        mStudents.clear();

        today = mCalToday.getTimeInMillis();
        Cursor studentCursor = dbHelper.getStudentsList();
        while (studentCursor.moveToNext()) {
            id = studentCursor.getInt(studentCursor.getColumnIndexOrThrow(ClassDBContract.StudentInfo.COLUMN_NAME_ID));
            attendNum = studentCursor.getInt(studentCursor.getColumnIndexOrThrow(ClassDBContract.StudentInfo.COLUMN_NAME_ATTEND_NUM));
            name = studentCursor.getString(studentCursor.getColumnIndexOrThrow(ClassDBContract.StudentInfo.COLUMN_NAME_NAME));
            isBoy = (studentCursor.getInt(studentCursor.getColumnIndexOrThrow(ClassDBContract.StudentInfo.COLUMN_NAME_GENDER)) == 1);
            phone = studentCursor.getString(studentCursor.getColumnIndexOrThrow(ClassDBContract.StudentInfo.COLUMN_NAME_PHONE));
            inDate = studentCursor.getLong(studentCursor.getColumnIndexOrThrow(ClassDBContract.StudentInfo.COLUMN_NAME_IN_DATE));
            outDate = studentCursor.getLong(studentCursor.getColumnIndexOrThrow(ClassDBContract.StudentInfo.ColUMN_NAME_OUT_DATE));

            Student s = new Student(id, attendNum, name, isBoy, phone,
                    (inDate <= today && outDate > today), inDate, outDate);
            mStudents.add(s);

            NEXT_ID = id + 1;
        }
        studentCursor.close();
        Collections.sort(mStudents, mStudentComparator);
    }

    /**
     * DB에서 Seatplan history를 읽어 mSeatplans 리스트를 구성한다.
     */
    private void initSeatplanList() {
        mSeatplans.clear();

        Cursor c = dbHelper.getSavedDateList();
        while(c.moveToNext()) {
            long date = c.getLong(c.getColumnIndexOrThrow(ClassDBContract.SeatHistory.COLUMN_NAME_APPLY_DATE));

            Cursor cursorForDate = dbHelper.getSeatplanInfo(date);
            cursorForDate.moveToFirst();
            int columnsInThisPlan = cursorForDate.getInt(
                    cursorForDate.getColumnIndexOrThrow(ClassDBContract.SeatplanInfo.COLUMN_NAME_COLUMNS));
            int rowsInThisPlan = cursorForDate.getInt(
                    cursorForDate.getColumnIndexOrThrow(ClassDBContract.SeatplanInfo.COLUMN_NAME_ROWS));
            boolean isBoyRightInThisPlan = (cursorForDate.getInt(
                    cursorForDate.getColumnIndexOrThrow(ClassDBContract.SeatplanInfo.COLUMN_NAME_IS_BOY_RIGHT)) == 1);
            cursorForDate.close();

            cursorForDate = dbHelper.getSeatplan(date);

            ArrayList<Seat> aSeats = new ArrayList<>();
            while (cursorForDate.moveToNext()) {
                int seatId = cursorForDate.getInt(cursorForDate.getColumnIndexOrThrow(ClassDBContract.SeatHistory.COLUMN_NAME_ID));
                int studentId = cursorForDate.getInt(cursorForDate.getColumnIndexOrThrow(ClassDBContract.SeatHistory.COLUMN_NAME_STUDENT_ID));
                aSeats.add(new Seat(seatId, findStudentById(studentId)));
                if(cursorForDate.getInt(cursorForDate.getColumnIndexOrThrow(ClassDBContract.SeatHistory.COLUMN_NAME_FIXED)) == 1) {
                    aSeats.get(seatId).setFixed(true);
                }
            }

            if(aSeats.size() != columnsInThisPlan * rowsInThisPlan) {
                Log.e(getString(R.string.log_error_tag),
                        "DB consistency error: seatplan total number of seats does not match");
            }

            GregorianCalendar cal = new GregorianCalendar();
            cal.setTimeInMillis(date);
            mSeatplans.add(new Seatplan(cal, aSeats, columnsInThisPlan, rowsInThisPlan, isBoyRightInThisPlan));

            cursorForDate.close();
        }
        c.close();
    }

    /**
     * 각 Student의 과거 앉았던 자리 List 를 구성한다.
     */
    private void initHistories() {
        Cursor historyCursor;
        GregorianCalendar cal;
        int studentId, seatId, pairId;
        long date;

        for(Student student : mStudents) {
            student.getHistories().clear();

            studentId = student.getId();
            historyCursor = dbHelper.getHistory(studentId);

            while(historyCursor.moveToNext()) {
                cal = new GregorianCalendar();
                date = historyCursor.getLong(historyCursor.getColumnIndexOrThrow(
                        ClassDBContract.SeatHistory.COLUMN_NAME_APPLY_DATE));
                cal.setTimeInMillis(date);
                seatId = historyCursor.getInt(historyCursor.getColumnIndexOrThrow(
                        ClassDBContract.SeatHistory.COLUMN_NAME_ID));
                pairId = dbHelper.getSeatedStudentId(seatId % 2 == 0 ? seatId + 1 : seatId - 1, date);
                student.getHistories().add(new PersonalHistory(cal, seatId, pairId));
            }
            historyCursor.close();
        }
    }

    public void rebuildAllData() {
        initStudentList();
        initSeatplanList();
        initHistories();
    }

    public TreeMap<Integer, Student> getDatedStudentsTreeMapKeybyAttendNum(long date) {
        TreeMap<Integer, Student> map = new TreeMap<>();
        for(Student s : mStudents) {
            if(s.getInDate() <= date && s.getOutDate() > date) {
                map.put(s.getAttendNum(), s);
            }
        }
        return map;
    }

    public void updateStudent(Student student) {
        dbHelper.update(student);
        Collections.sort(mStudents, mStudentComparator);
    }

    public void clearTime(GregorianCalendar c) {
        c.clear(Calendar.AM_PM);
        c.clear(Calendar.HOUR);
        c.clear(Calendar.HOUR_OF_DAY);
        c.clear(Calendar.MINUTE);
        c.clear(Calendar.SECOND);
        c.clear(Calendar.MILLISECOND);
    }

    public String getDateString(GregorianCalendar cal) {
        return (new StringBuilder().append(cal.get(Calendar.YEAR))
                .append(getString(R.string.year_string)).append(" ").append(cal.get(Calendar.MONTH) + 1)
                .append(getString(R.string.month_string)).append(" ").append(cal.get(Calendar.DAY_OF_MONTH))
                .append(getString(R.string.day_string)).append(", ")
                .append(getResources().getStringArray(R.array.dayofweek_array)[cal.get(Calendar.DAY_OF_WEEK) - 1])
                .toString());
    }

    /* For Test only... create test dummy data */
    public void createTestData() {
        long indate = new GregorianCalendar(2015, 2, 1).getTimeInMillis();
        addStudent(new Student(1000,   1, "연우진", true, "01000000000",true,  indate, Long.MAX_VALUE));
        addStudent(new Student(1001,   2, "이종석", true, "01000000000",true,  indate, Long.MAX_VALUE));
        addStudent(new Student(1002,   3, "송중기", true, "01000000000",true,  indate, Long.MAX_VALUE));
        addStudent(new Student(1003,   4, "여진구", true, "01000000000",true,  indate, Long.MAX_VALUE));
        addStudent(new Student(1004,   5, "유승호", true, "01000000000",true,  indate, Long.MAX_VALUE));
        addStudent(new Student(1005,   6, "하정우", true, "01000000000",true,  indate, Long.MAX_VALUE));
        addStudent(new Student(1006,   7, "유재석", true, "01000000000",true,  indate, Long.MAX_VALUE));
        addStudent(new Student(1007,   8, "김우빈", true, "01000000000",true,  indate, Long.MAX_VALUE));
        addStudent(new Student(1008,   9, "정우",  true, "01000000000",true,  indate, Long.MAX_VALUE));
        addStudent(new Student(1009,  10, "유연석", true, "01000000000",true,  indate, Long.MAX_VALUE));
        addStudent(new Student(1010, 11, "임시완", true, "01000000000", true, indate, Long.MAX_VALUE));
        addStudent(new Student(1011, 12, "신하균", true, "01000000000", true, indate, Long.MAX_VALUE));
        addStudent(new Student(1012, 13, "이민호", true, "01000000000", true, indate, Long.MAX_VALUE));
        addStudent(new Student(1013, 14, "정우성", true, "01000000000", true, indate, Long.MAX_VALUE));
        addStudent(new Student(1014, 15, "원빈",  true, "01000000000", true, indate, Long.MAX_VALUE));
        addStudent(new Student(1015, 16, "강동원", true, "01000000000", true, indate, Long.MAX_VALUE));
        addStudent(new Student(1016, 17, "이정재", true, "01000000000", true, indate, Long.MAX_VALUE));
        addStudent(new Student(1017, 18, "설현",  false,"01000000000", true, indate, Long.MAX_VALUE));
        addStudent(new Student(1018, 19, "문채원", false,"01000000000", true, indate, Long.MAX_VALUE));
        addStudent(new Student(1019, 20, "아이유", false,"01000000000", true, indate, Long.MAX_VALUE));
        addStudent(new Student(1020, 21, "김태희", false,"01000000000", true, indate, Long.MAX_VALUE));
        addStudent(new Student(1021, 22, "송혜교", false,"01000000000", true, indate, Long.MAX_VALUE));
        addStudent(new Student(1022, 23, "한지민", false,"01000000000", true, indate, Long.MAX_VALUE));
        addStudent(new Student(1023, 24, "손예진", false,"01000000000", true, indate, Long.MAX_VALUE));
        addStudent(new Student(1024, 25, "이나영", false,"01000000000", true, indate, Long.MAX_VALUE));
        addStudent(new Student(1025, 26, "신민아", false,"01000000000", true, indate, Long.MAX_VALUE));
        addStudent(new Student(1026, 27, "이민정", false,"01000000000", true, indate, Long.MAX_VALUE));
        addStudent(new Student(1027, 28, "신소율", false,"01000000000", true, indate, Long.MAX_VALUE));
        addStudent(new Student(1028, 29, "한효주", false,"01000000000", true, indate, Long.MAX_VALUE));
        addStudent(new Student(1029, 30, "수지",  false,"01000000000", true, indate, Long.MAX_VALUE));
        addStudent(new Student(1030, 31, "송지효", false,"01000000000", true, indate, Long.MAX_VALUE));
        addStudent(new Student(1031, 32, "김소은", false,"01000000000", true, indate, Long.MAX_VALUE));
        addStudent(new Student(1032, 33, "이정현", false,"01000000000", true, indate, Long.MAX_VALUE));
        addStudent(new Student(1033, 34, "유진",  false,"01000000000", true, indate, Long.MAX_VALUE));
        addStudent(new Student(1034, 35, "한가인", false,"01000000000", true, indate, Long.MAX_VALUE));
        addStudent(new Student(1035, 36, "박한별", false,"01000000000", true, indate, Long.MAX_VALUE));
        addStudent(new Student(1036, 37, "심은경", false,"01000000000", true, indate, Long.MAX_VALUE));
    }

    static public void acraTest() {
        String str = null;
        str.charAt(0);
    }
}
