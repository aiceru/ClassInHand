package com.iceru.classinhand;

import android.app.Application;

import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

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

    private static ClassInHandApplication appInstance;
    private TreeMap <Integer, Student> mStudents;

    @Override
    public final void onCreate() {
        super.onCreate();
        appInstance = this;
        //ACRA.init(this);

        mStudents = new TreeMap<Integer, Student>();

        Student s;
        int i;
        for(i = 0; i < 10; i++) {
            s = new Student(i, i+1, "남자 " + String.valueOf(i+1), true);
            mStudents.put(i, s);
        }
        for(; i < 20; i++) {
            s = new Student(i, i+1, "여자 " + String.valueOf(i-9), false);
            mStudents.put(i, s);
        }
    }

    public static ClassInHandApplication getInstance() {
        return appInstance;
    }

    public TreeMap<Integer, Student> getmStudents() {
        return mStudents;
    }

    public void setmStudents(TreeMap<Integer, Student> newStudents) {
        this.mStudents = newStudents;
    }

    public boolean addStudent(Student student) {
        boolean exist = null != mStudents.get(student.getAttendNum());
        if(!exist) {
            mStudents.put(student.getAttendNum(), student);
            /*if(student.isBoy()) num_boys++;
            else num_girls++;*/
            //dbHelper.insert(student);
        }
        return !exist;
    }

    public boolean removeStudent(Student student) {
        boolean success = null != mStudents.remove(student.getAttendNum());
        if(success) {
            /*if(student.isBoy()) num_boys--;
            else num_girls--;*/
            //dbHelper.delete(student);
        }
        return success;
    }

    public void removeAllStudents() {
        mStudents.clear();
        /*num_boys = 0;
        num_girls = 0;*/
        //dbHelper.deleteAllStudents();
    }
}
