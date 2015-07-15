package com.iceru.classinhand;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.apache.http.protocol.HTTP;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;


public class StudentDetailActivity extends ActionBarActivity {
    private Student mStudent;
    private ClassInHandApplication application;

    private ImageView       mImageViewPicture;
    private ImageView       mImageViewCamera;
    private EditText        mEdittextAttendNum;
    private EditText        mEdittextName;
    private ToggleButton    mTglbtnGender;
    private EditText        mEdittextPhone;
    private Spinner         mSpinnerCallorSms;
    private GregorianCalendar   mIndate;
    private TextView        mTextviewIndate;
    private GregorianCalendar   mOutdate;
    private TextView        mTextviewOutdate;

    private Menu            menu;

    private class spinnerAdapter extends BaseAdapter {
        private class ViewHolder {
            ImageView iv_content;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder vh;

            if(convertView == null) {
                view = getLayoutInflater().inflate(R.layout.spinner_row_callorsms, parent, false);
                vh = new ViewHolder();
                vh.iv_content = (ImageView)view.findViewById(R.id.imageview_call_or_sms);
                view.setTag(vh);
            }
            else {
                vh = (ViewHolder)view.getTag();
            }

            switch(position) {
                case 0:
                    vh.iv_content.setImageResource(R.drawable.ic_call_indigo_500_24dp);
                    break;
                case 1:
                    vh.iv_content.setImageResource(R.drawable.ic_sms_indigo_500_24dp);
                    break;
                default:
                    break;

            }
            return view;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = ClassInHandApplication.getInstance();

        Intent intent = getIntent();
        mStudent = application.findStudentById(intent.getIntExtra(ClassInHandApplication.STUDENT_SELECTED_ID, -1));
        if(mStudent == null) finish();

        setContentView(R.layout.activity_student_common);
        initviews();
    }

    private void initviews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mImageViewPicture = (ImageView)findViewById(R.id.imageview_picture);
        mImageViewCamera = (ImageView)findViewById(R.id.imageview_camera);
        mTglbtnGender = (ToggleButton)findViewById(R.id.tglbtn_gender);
        mEdittextAttendNum = (EditText)findViewById(R.id.edittext_attendnum);
        mEdittextName = (EditText)findViewById(R.id.edittext_name);
        mEdittextPhone = (EditText)findViewById(R.id.edittext_phone);
        mSpinnerCallorSms = (Spinner)findViewById(R.id.spinner_callorsms);
        mTextviewIndate = (TextView)findViewById(R.id.textview_indate);
        mTextviewOutdate = (TextView)findViewById(R.id.textview_outdate);

        setEditEnable(false);

        FrameLayout outdateFrame = (FrameLayout)findViewById(R.id.framelayout_outdate);
        outdateFrame.setVisibility(View.VISIBLE);
        mSpinnerCallorSms.setVisibility(View.VISIBLE);

        mTglbtnGender.setChecked(mStudent.isBoy());
        mEdittextAttendNum.setText(String.valueOf(mStudent.getAttendNum()));
        mEdittextName.setText(mStudent.getName());
        mEdittextPhone.setText(mStudent.getPhone());

        mSpinnerCallorSms.setAdapter(new spinnerAdapter());
        mSpinnerCallorSms.setSelection(0, false);
        mSpinnerCallorSms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0:
                        makeCall();
                        break;
                    case 1:
                        sendSms();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mIndate = new GregorianCalendar();
        mIndate.clear();
        mIndate.setTimeInMillis(mStudent.getInDate());
        mTextviewIndate.setText(getDateString(mIndate));

        mOutdate = new GregorianCalendar();
        mOutdate.clear();
        mOutdate.setTimeInMillis(mStudent.getOutDate());
        if(mOutdate.getTimeInMillis() != Long.MAX_VALUE) {
            mTextviewOutdate.setText(getDateString(mOutdate));
        } else {
            mTextviewOutdate.setText(R.string.hint_textview_outdate);
        }

    }

    private String getDateString(GregorianCalendar cal) {
        return (new StringBuilder().append(cal.get(Calendar.YEAR))
                .append(getString(R.string.year_string)).append(" ").append(cal.get(Calendar.MONTH) + 1)
                .append(getString(R.string.month_string)).append(" ").append(cal.get(Calendar.DAY_OF_MONTH))
                .append(getString(R.string.day_string)).append(", ")
                .append(getResources().getStringArray(R.array.dayofweek_array)[cal.get(Calendar.DAY_OF_WEEK) - 1])
                .toString());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_student_detail, menu);
        menu.findItem(R.id.action_done).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id) {
            case R.id.action_student_detail_edit:
                editMe();
                break;
            case R.id.action_done:
                saveMe();
                break;
            case R.id.action_student_detail_delete:
                recommendEdit();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void recommendEdit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialog_style);
        builder.setTitle(R.string.title_dialog_warning);
        builder.setMessage(R.string.contents_dialog_recommend_edit);
        builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                deleteMe();
                finish();
            }
        });
        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog, Do nothing
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteMe() {
        application.removeStudent(mStudent);
        application.rebuildAllData();
    }

    private void editMe() {
        menu.findItem(R.id.action_student_detail_edit).setVisible(false);
        menu.findItem(R.id.action_done).setVisible(true);
        menu.findItem(R.id.action_student_detail_delete).setVisible(false);

        setEditEnable(true);

        mTextviewIndate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickDialog(mIndate, mTextviewIndate);
            }
        });
        mTextviewOutdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickDialog(mOutdate, mTextviewOutdate);
            }
        });
    }

    private void setEditEnable(boolean enable) {
        mImageViewCamera.setVisibility(enable? View.VISIBLE : View.GONE);
        mEdittextAttendNum.setEnabled(enable);
        mEdittextName.setEnabled(enable);
        mTglbtnGender.setClickable(enable);
        mEdittextPhone.setEnabled(enable);
        mSpinnerCallorSms.setEnabled(!enable);
        mTextviewIndate.setClickable(enable);
        mTextviewOutdate.setClickable(enable);
    }

    private void showDatePickDialog(final GregorianCalendar cal, final TextView tv) {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                if(view.isShown()) {
                    cal.clear();
                    cal.set(year, monthOfYear, dayOfMonth);
                    tv.setText(getDateString(cal));
                }
            }
        };
        GregorianCalendar todayCal = application.getValueOfTodayCalendar();
        DatePickerDialog dateDialog = new DatePickerDialog(this, R.style.dialog_style, dateSetListener,
                todayCal.get(Calendar.YEAR), todayCal.get(Calendar.MONTH), todayCal.get(Calendar.DAY_OF_MONTH));
        dateDialog.show();
    }

    private void saveMe() {
        int attendNum = Integer.parseInt(mEdittextAttendNum.getText().toString());
        long today = application.getValueOfTodayCalendar().getTimeInMillis();

        if( (application.findStudentByAttendNum(attendNum) == null) ||
                (mStudent.getAttendNum() == attendNum) ) {
            menu.findItem(R.id.action_student_detail_edit).setVisible(true);
            menu.findItem(R.id.action_done).setVisible(false);
            menu.findItem(R.id.action_student_detail_delete).setVisible(true);

            mStudent.setAttendNum(attendNum);
            mStudent.setName(mEdittextName.getText().toString());
            mStudent.setBoy(mTglbtnGender.isChecked());
            mStudent.setPhone(PhoneNumberUtils.formatNumber(mEdittextPhone.getText().toString(), "KR"));

            mStudent.setInDate(mIndate.getTimeInMillis());
            mStudent.setOutDate(mOutdate.getTimeInMillis());

            // Fix : wooseok, 2015 03 31 수정된 학생정보 리스트에 반영
            mStudent.setInClass(mStudent.getInDate() <= today && mStudent.getOutDate() > today);
            application.updateStudent(mStudent);

            setEditEnable(false);
        }

        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialog_style);
            AlertDialog dialog = builder.setTitle(R.string.title_dialog_warning)
                                        .setMessage(R.string.warning_existing_attendnum)
                                        .setCancelable(false)
                                        .setPositiveButton(R.string.action_confirm, null)
                                        .create();
            dialog.show();
        }
    }

    public void makeCall() {
        String uri = "tel:" + mStudent.getPhone().trim();
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(uri));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void sendSms() {
        String uri = "smsto:" + mStudent.getPhone().trim();
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType(HTTP.PLAIN_TEXT_TYPE);
        intent.setData(Uri.parse(uri));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void notYetSupported(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialog_style);
        builder.setTitle(R.string.title_dialog_notyetsupported);
        builder.setMessage(R.string.contents_dialog_notyetsupported);
        builder.setPositiveButton(R.string.confirm, null);
        builder.create().show();
    }
}
