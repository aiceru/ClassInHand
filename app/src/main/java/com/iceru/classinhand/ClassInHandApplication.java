package com.iceru.classinhand;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

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
        //resDialogIcon = R.drawable.ic_action_warning, //optional. default is a warning sign
        resDialogTitle = R.string.crash_dialog_title, // optional. default is your application name
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. when defined, adds a user text field input with this text resource as a label
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

    private TreeMap <Integer, Student> mStudents;        // KEY : ID
    private TreeMap <Integer, Student> mCurrentStudents; // KEY : attendNum

    private ArrayList<Seatplan>         mSeatplans;

    private ClassDBHelper   dbHelper;

    private Comparator<Seatplan>    mSeatplanComparator;
    private GregorianCalendar       mCalToday;

    @Override
    public final void onCreate() {
        super.onCreate();
        //ACRA.init(this);

        mCalToday = new GregorianCalendar();
        clearTime(mCalToday);

        appInstance = this;
        dbHelper = new ClassDBHelper(this);

        mStudents = new TreeMap<>();
        mCurrentStudents = new TreeMap<>();
        mSeatplans = new ArrayList<>();

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
        globalProperties.isBoyRight = Boolean.parseBoolean(prefs.getString(getString(R.string.sharedpref_key_is_boy_right), "true"));

        rebuildAllData();
    }

    public static ClassInHandApplication getInstance() {
        return appInstance;
    }

    public TreeMap<Integer, Student> getmStudents() {
        return mStudents;
    }

    public TreeMap<Integer, Student> getmCurrentStudents() { return mCurrentStudents; }

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
        boolean exist = null != mStudents.get(student.getId());
        if(!exist) {
            long today = mCalToday.getTimeInMillis();
            mStudents.put(student.getId(), student);
            if(student.getInDate() <= today && student.getOutDate() > today) {
                mCurrentStudents.put(student.getAttendNum(), student);
            }
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
        return mStudents.get(id);
    }

    public Student findStudentByAttendNum(int attendNum) {
        return mCurrentStudents.get(attendNum);
    }

    public boolean removeStudent(Student student) {
        boolean success = null != mStudents.remove(student.getId());
        if(success) {
            mCurrentStudents.remove(student.getAttendNum());
            dbHelper.delete(student);
        }
        return success;
    }

    public void removeAllStudents() {
        mStudents.clear();
        mCurrentStudents.clear();
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
        String name;
        boolean isBoy;
        long inDate, outDate, today;

        mStudents.clear();
        mCurrentStudents.clear();

        today = mCalToday.getTimeInMillis();
        Cursor studentCursor = dbHelper.getStudentsList();
        while (studentCursor.moveToNext()) {
            id = studentCursor.getInt(studentCursor.getColumnIndexOrThrow(ClassDBContract.StudentInfo.COLUMN_NAME_ID));
            attendNum = studentCursor.getInt(studentCursor.getColumnIndexOrThrow(ClassDBContract.StudentInfo.COLUMN_NAME_ATTEND_NUM));
            name = studentCursor.getString(studentCursor.getColumnIndexOrThrow(ClassDBContract.StudentInfo.COLUMN_NAME_NAME));
            isBoy = (studentCursor.getInt(studentCursor.getColumnIndexOrThrow(ClassDBContract.StudentInfo.COLUMN_NAME_GENDER)) == 1);
            inDate = studentCursor.getLong(studentCursor.getColumnIndexOrThrow(ClassDBContract.StudentInfo.COLUMN_NAME_IN_DATE));
            outDate = studentCursor.getLong(studentCursor.getColumnIndexOrThrow(ClassDBContract.StudentInfo.ColUMN_NAME_OUT_DATE));

            Student s = new Student(id, attendNum, name, isBoy, inDate, outDate);
            if(inDate <= today && outDate > today) {
                mCurrentStudents.put(s.getAttendNum(), s);
            }
            mStudents.put(id, s);

            NEXT_ID = id + 1;
        }
        studentCursor.close();
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
            boolean isBoyRightInThisPlan = (cursorForDate.getInt(
                    cursorForDate.getColumnIndexOrThrow(ClassDBContract.SeatplanInfo.COLUMN_NAME_IS_BOY_RIGHT)) == 1);
            int totalSeatsInThisPlan = cursorForDate.getInt(
                    cursorForDate.getColumnIndexOrThrow(ClassDBContract.SeatplanInfo.COLUMN_NAME_TOTAL_SEATS));
            cursorForDate.close();

            cursorForDate = dbHelper.getSeatplan(date);

            ArrayList<Seat> aSeats = new ArrayList<>();
            while (cursorForDate.moveToNext()) {
                int seatId = cursorForDate.getInt(cursorForDate.getColumnIndexOrThrow(ClassDBContract.SeatHistory.COLUMN_NAME_ID));
                int studentId = cursorForDate.getInt(cursorForDate.getColumnIndexOrThrow(ClassDBContract.SeatHistory.COLUMN_NAME_STUDENT_ID));
                aSeats.add(new Seat(seatId, mStudents.get(studentId)));
            }

            if(aSeats.size() != totalSeatsInThisPlan) {
                Log.e(getString(R.string.log_error_tag),
                        "DB consistency error: seatplan total number of seats does not match");
            }

            GregorianCalendar cal = new GregorianCalendar();
            cal.setTimeInMillis(date);
            mSeatplans.add(new Seatplan(cal, aSeats, columnsInThisPlan, isBoyRightInThisPlan, aSeats.size()));

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
        Student student;
        long date;

        for(TreeMap.Entry<Integer, Student> entry : mStudents.entrySet()) {
            student = entry.getValue();
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
        for(Map.Entry<Integer, Student> e : mStudents.entrySet()) {
            Student s = e.getValue();
            if(s.getInDate() <= date && s.getOutDate() > date) {
                map.put(s.getAttendNum(), s);
            }
        }
        return map;
    }

    public void updateStudent(Student student) {
        dbHelper.update(student);
    }

    public void restart() {
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    public void clearTime(GregorianCalendar c) {
        c.clear(Calendar.AM_PM);
        c.clear(Calendar.HOUR);
        c.clear(Calendar.HOUR_OF_DAY);
        c.clear(Calendar.MINUTE);
        c.clear(Calendar.SECOND);
        c.clear(Calendar.MILLISECOND);
    }

    /* For Test only... create test dummy data */
    public void createTestData() {
        long indate = new GregorianCalendar(2015, 2, 1).getTimeInMillis();
        addStudent(new Student(1000,   1, "연우진", true,  indate, Long.MAX_VALUE));
        addStudent(new Student(1001,   2, "이종석", true,  indate, Long.MAX_VALUE));
        addStudent(new Student(1002,   3, "송중기", true,  indate, Long.MAX_VALUE));
        addStudent(new Student(1003,   4, "여진구", true,  indate, Long.MAX_VALUE));
        addStudent(new Student(1004,   5, "유승호", true,  indate, Long.MAX_VALUE));
        addStudent(new Student(1005,   6, "하정우", true,  indate, Long.MAX_VALUE));
        addStudent(new Student(1006,   7, "유재석", true,  indate, Long.MAX_VALUE));
        addStudent(new Student(1007,   8, "김우빈", true,  indate, Long.MAX_VALUE));
        addStudent(new Student(1008,   9, "정우",  true,  indate, Long.MAX_VALUE));
        addStudent(new Student(1009,  10, "유연석", true,  indate, Long.MAX_VALUE));
        addStudent(new Student(1010, 11, "임시완", true,  indate, Long.MAX_VALUE));
        addStudent(new Student(1011, 12, "신하균", true,  indate, Long.MAX_VALUE));
        addStudent(new Student(1012, 13, "이민호", true,  indate, Long.MAX_VALUE));
        addStudent(new Student(1013, 14, "정우성", true,  indate, Long.MAX_VALUE));
        addStudent(new Student(1014, 15, "원빈",  true,  indate, Long.MAX_VALUE));
        addStudent(new Student(1015, 16, "강동원", true,  indate, Long.MAX_VALUE));
        addStudent(new Student(1016, 17, "이정재", true,  indate, Long.MAX_VALUE));
        addStudent(new Student(1017, 18, "설현",  false, indate, Long.MAX_VALUE));
        addStudent(new Student(1018, 19, "문채원", false, indate, Long.MAX_VALUE));
        addStudent(new Student(1019, 20, "아이유", false, indate, Long.MAX_VALUE));
        addStudent(new Student(1020, 21, "김태희", false, indate, Long.MAX_VALUE));
        addStudent(new Student(1021, 22, "송혜교", false, indate, Long.MAX_VALUE));
        addStudent(new Student(1022, 23, "한지민", false, indate, Long.MAX_VALUE));
        addStudent(new Student(1023, 24, "손예진", false, indate, Long.MAX_VALUE));
        addStudent(new Student(1024, 25, "이나영", false, indate, Long.MAX_VALUE));
        addStudent(new Student(1025, 26, "신민아", false, indate, Long.MAX_VALUE));
        addStudent(new Student(1026, 27, "이민정", false, indate, Long.MAX_VALUE));
        addStudent(new Student(1027, 28, "신소율", false, indate, Long.MAX_VALUE));
        addStudent(new Student(1028, 29, "한효주", false, indate, Long.MAX_VALUE));
        addStudent(new Student(1029, 30, "수지",  false, indate, Long.MAX_VALUE));
        addStudent(new Student(1030, 31, "송지효", false, indate, Long.MAX_VALUE));
        addStudent(new Student(1031, 32, "김소은", false, indate, Long.MAX_VALUE));
        addStudent(new Student(1032, 33, "이정현", false, indate, Long.MAX_VALUE));
        addStudent(new Student(1033, 34, "유진",  false, indate, Long.MAX_VALUE));
        addStudent(new Student(1034, 35, "한가인", false, indate, Long.MAX_VALUE));
        addStudent(new Student(1035, 36, "박한별", false, indate, Long.MAX_VALUE));
        addStudent(new Student(1036, 37, "심은경", false, indate, Long.MAX_VALUE));
    }
}
