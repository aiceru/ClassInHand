package com.iceru.classinhand;

import android.app.Application;
import android.database.Cursor;

import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

import java.util.Comparator;
import java.util.HashMap;
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
        resDialogIcon = R.drawable.ic_action_warning, //optional. default is a warning sign
        resDialogTitle = R.string.crash_dialog_title, // optional. default is your application name
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. when defined, adds a user text field input with this text resource as a label
        resDialogOkToast = R.string.crash_dialog_ok_toast // optional. displays a Toast message when the user accepts to send a report.
)
public class ClassInHandApplication extends Application {

    /* Public Constants */
    public static final int MAX_STUDENTS = 99;
    public static int       NEXT_ID;

    private static ClassInHandApplication appInstance;

    private TreeMap <Integer, Integer> mIdMap;          // ID-AttendNum pairs of "Current" students
    private TreeMap <Integer, Student> mStudents;       // "Current" students, KEY : Attend Num
    private TreeMap <Integer, Student> mPastStudents;   // "Past" students, KEY : ID

    private ClassDBHelper   dbHelper;

    @Override
    public final void onCreate() {
        super.onCreate();
        //ACRA.init(this);

        appInstance = this;
        dbHelper = new ClassDBHelper(this);

        mIdMap = new TreeMap<Integer, Integer>();
        mStudents = new TreeMap<Integer, Student>();

        Cursor c = dbHelper.getStudentsList();
        while(c.moveToNext()) {
            int id = c.getInt(c.getColumnIndexOrThrow(ClassDBContract.StudentInfo.COLUMN_NAME_ID));
            int attendNum = c.getInt(c.getColumnIndexOrThrow(ClassDBContract.StudentInfo.COLUMN_NAME_ATTEND_NUM));
            String name = c.getString(c.getColumnIndexOrThrow(ClassDBContract.StudentInfo.COLUMN_NAME_NAME));
            boolean isBoy = (c.getInt(c.getColumnIndexOrThrow(ClassDBContract.StudentInfo.COLUMN_NAME_GENDER)) == 1);
            long inDate = c.getInt(c.getColumnIndexOrThrow(ClassDBContract.StudentInfo.COLUMN_NAME_IN_DATE));
            long outDate = c.getInt(c.getColumnIndexOrThrow(ClassDBContract.StudentInfo.ColUMN_NAME_OUT_DATE));

            Student s = new Student(id, attendNum, name, isBoy, inDate, outDate);

            mIdMap.put(id, attendNum);
            mStudents.put(attendNum, s);

            NEXT_ID = s.getId()+1;
        }
        c.close();;
    }

    public static ClassInHandApplication getInstance() {
        return appInstance;
    }

    public TreeMap<Integer, Student> getmStudents() {
        return mStudents;
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
}
