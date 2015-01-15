package com.iceru.classinhand;

import android.app.Application;
import android.database.Cursor;

import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

import java.util.ArrayList;
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

    /* Public Constants */
    public static final int MAX_STUDENTS = 99;
    public static final int SEATED_NOT = 0;
    public static final int SEATED_LEFT = 1;
    public static final int SEATED_RIGHT = 2;
    public static final int SEATED_BOTH = 3;
    public static int       NEXT_ID;

    private static ClassInHandApplication appInstance;

    private TreeMap <Integer, Integer> mIdMap;          // ID-AttendNum pairs of "Current" students
    private TreeMap <Integer, Student> mStudents;       // "Current" students, KEY : Attend Num
    private TreeMap <Integer, Student> mPastStudents;   // "Past" students, KEY : ID

    private ArrayList<Seatplan>         mSeatplans;

    private ClassDBHelper   dbHelper;

    private Comparator<Seatplan> mSeatplanComparator;

    @Override
    public final void onCreate() {
        super.onCreate();
        //ACRA.init(this);

        appInstance = this;
        dbHelper = new ClassDBHelper(this);

        mIdMap = new TreeMap<>();
        mStudents = new TreeMap<>();
        mPastStudents = new TreeMap<>();

        mSeatplanComparator = new Comparator<Seatplan>() {
            @Override
            public int compare(Seatplan lhs, Seatplan rhs) {
                long diff = lhs.getmApplyDate().getTimeInMillis() - rhs.getmApplyDate().getTimeInMillis();
                return diff > 0 ? -1 : 1;
            }
        };

        Cursor c = dbHelper.getStudentsList();
        while (c.moveToNext()) {
            int id = c.getInt(c.getColumnIndexOrThrow(ClassDBContract.StudentInfo.COLUMN_NAME_ID));
            int attendNum = c.getInt(c.getColumnIndexOrThrow(ClassDBContract.StudentInfo.COLUMN_NAME_ATTEND_NUM));
            String name = c.getString(c.getColumnIndexOrThrow(ClassDBContract.StudentInfo.COLUMN_NAME_NAME));
            boolean isBoy = (c.getInt(c.getColumnIndexOrThrow(ClassDBContract.StudentInfo.COLUMN_NAME_GENDER)) == 1);
            long inDate = c.getInt(c.getColumnIndexOrThrow(ClassDBContract.StudentInfo.COLUMN_NAME_IN_DATE));
            long outDate = c.getInt(c.getColumnIndexOrThrow(ClassDBContract.StudentInfo.ColUMN_NAME_OUT_DATE));

            Student s = new Student(id, attendNum, name, isBoy, inDate, outDate);

            mIdMap.put(id, attendNum);
            mStudents.put(attendNum, s);

            NEXT_ID = s.getId() + 1;
        }
        c.close();

        mSeatplans = new ArrayList<>();
        c = dbHelper.getSavedDateList();
        while(c.moveToNext()) {
            long date = c.getLong(c.getColumnIndexOrThrow(ClassDBContract.SeatHistory.COLUMN_NAME_APPLY_DATE));
            Cursor cursorForDate = dbHelper.getSeatplan(date);

            ArrayList<Seat> aSeats = new ArrayList<>();
            while (cursorForDate.moveToNext()) {
                int seatId = cursorForDate.getInt(cursorForDate.getColumnIndexOrThrow(ClassDBContract.SeatHistory.COLUMN_NAME_ID));
                int studentId = cursorForDate.getInt(cursorForDate.getColumnIndexOrThrow(ClassDBContract.SeatHistory.COLUMN_NAME_STUDENT_ID));
                aSeats.add(new Seat(seatId, mStudents.get(mIdMap.get(studentId))));
            }

            GregorianCalendar cal = new GregorianCalendar();
            cal.setTimeInMillis(date);
            mSeatplans.add(new Seatplan(cal, aSeats));

            cursorForDate.close();
        }
        c.close();
        /*ArrayList<Seat> temparray = new ArrayList<>();
        ArrayList<Seat> temparray2 = new ArrayList<>();
        int i = 0;
        for(Map.Entry<Integer, Student> entry : mStudents.entrySet()) {
            Seat s = new Seat(i++, entry.getValue());
            temparray.add(s);
            temparray2.add(s);
        }
        Seatplan sp = new Seatplan(new GregorianCalendar(2014, 12, 12), temparray);
        Seatplan sp2 = new Seatplan(new GregorianCalendar(2014, 12, 25), temparray2);
        mSeatplans.add(sp);
        mSeatplans.add(sp2);*/
    }

    public static ClassInHandApplication getInstance() {
        return appInstance;
    }

    public TreeMap<Integer, Student> getmStudents() {
        return mStudents;
    }

    public ArrayList<Seatplan> getmSeatplans() {
        return mSeatplans;
    }

    public ClassDBHelper getDbHelper() {
        return dbHelper;
    }

    public void addStudentAll(TreeMap<Integer, Student> addingStudents) {
        for(Map.Entry<Integer, Student> entry : addingStudents.entrySet()) {
            Student student = entry.getValue();
            addStudent(student);
        }
    }

    public boolean addStudent(Student student) {
        boolean exist = null != mStudents.get(student.getAttendNum());
        if(!exist) {
            mIdMap.put(student.getId(), student.getAttendNum());
            mStudents.put(student.getAttendNum(), student);
            dbHelper.insert(student);
        }
        return !exist;
    }

    public Student findStudent(int Id) {
        if(mIdMap.containsKey(Id)) return mStudents.get(mIdMap.get(Id));
        else return mPastStudents.get(Id);
    }

    public boolean removeStudent(Student student) {
        boolean success = null != mStudents.remove(student.getAttendNum());
        if(success) {
            mIdMap.remove(student.getId());
            dbHelper.delete(student);
        }
        return success;
    }

    public void removeAllStudents() {
        mIdMap.clear();
        mStudents.clear();
        dbHelper.deleteAllStudents();
    }

    public void addSeatplan(Seatplan plan) {
        mSeatplans.add(plan);
        Collections.sort(mSeatplans, mSeatplanComparator);
        dbHelper.insert(plan);
    }

    public void removeSeatplan(Seatplan plan) {
        mSeatplans.remove(plan);
        Collections.sort(mSeatplans, mSeatplanComparator);
        dbHelper.deleteSeatPlan(plan.getmApplyDate().getTimeInMillis());
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

    /* For Test only... create test dummy data */
    public void createTestData() {
        addStudent(new Student(0, 1, "연우진", true, 9999L, 9999L));
        addStudent(new Student(1, 2, "이종석", true, 9999L, 9999L));
        addStudent(new Student(2, 3, "송중기", true, 9999L, 9999L));
        addStudent(new Student(3, 4, "여진구", true, 9999L, 9999L));
        addStudent(new Student(4, 5, "유승호", true, 9999L, 9999L));
        addStudent(new Student(5, 6, "하정우", true, 9999L, 9999L));
        addStudent(new Student(6, 7, "유재석", true, 9999L, 9999L));
        addStudent(new Student(7, 8, "김우빈", true, 9999L, 9999L));
        addStudent(new Student(8, 9, "정우", true, 9999L, 9999L));
        addStudent(new Student(9, 10, "유연석", true, 9999L, 9999L));
        addStudent(new Student(10, 11, "임시완", true, 9999L, 9999L));
        addStudent(new Student(11, 12, "신하균", true, 9999L, 9999L));
        addStudent(new Student(12, 13, "이민호", true, 9999L, 9999L));
        addStudent(new Student(13, 14, "정우성", true, 9999L, 9999L));
        addStudent(new Student(14, 15, "원빈", true, 9999L, 9999L));
        addStudent(new Student(15, 16, "강동원", true, 9999L, 9999L));
        addStudent(new Student(16, 17, "이정재", true, 9999L, 9999L));
        addStudent(new Student(17, 18, "설현", false, 9999L, 9999L));
        addStudent(new Student(18, 19, "문채원", false, 9999L, 9999L));
        addStudent(new Student(19, 20, "아이유", false, 9999L, 9999L));
        addStudent(new Student(20, 21, "김태희", false, 9999L, 9999L));
        addStudent(new Student(21, 22, "송혜교", false, 9999L, 9999L));
        addStudent(new Student(22, 23, "한지민", false, 9999L, 9999L));
        addStudent(new Student(23, 24, "손예진", false, 9999L, 9999L));
        addStudent(new Student(24, 25, "이나영", false, 9999L, 9999L));
        addStudent(new Student(25, 26, "신민아", false, 9999L, 9999L));
        addStudent(new Student(26, 27, "이민정", false, 9999L, 9999L));
        addStudent(new Student(27, 28, "신소율", false, 9999L, 9999L));
        addStudent(new Student(28, 29, "한효주", false, 9999L, 9999L));
        addStudent(new Student(29, 30, "수지", false, 9999L, 9999L));
        addStudent(new Student(30, 31, "송지효", false, 9999L, 9999L));
        addStudent(new Student(31, 32, "김소은", false, 9999L, 9999L));
        addStudent(new Student(32, 33, "이정현", false, 9999L, 9999L));
        addStudent(new Student(33, 34, "유진", false, 9999L, 9999L));
        addStudent(new Student(34, 35, "한가인", false, 9999L, 9999L));
        addStudent(new Student(35, 36, "박한별", false, 9999L, 9999L));
        addStudent(new Student(36, 37, "심은경", false, 9999L, 9999L));
    }
}
