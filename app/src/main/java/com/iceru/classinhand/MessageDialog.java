package com.iceru.classinhand;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by iceru on 15. 5. 10..
 */
public class MessageDialog extends Dialog {
    private static final String TAG = MessageDialog.class.getName();

    private int    mRecipeNums;
    private String mDestinations;
    private String mMessageBody;

    private TextView tv_destination;
    private EditText et_message;
    private ImageButton ib_attach;
    private ImageButton ib_cancel;
    private ImageButton ib_send;

    // default constructor
    public MessageDialog(Context context, String dest, int recipeNums) {
        super(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
        this.mDestinations = dest;
        this.mRecipeNums = recipeNums;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        lpWindow.width = getContext().getResources().getDisplayMetrics().widthPixels * 8 / 10;
        lpWindow.height = getContext().getResources().getDisplayMetrics().heightPixels * 8 / 10;
        getWindow().setAttributes(lpWindow);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        setContentView(R.layout.dialog_message);

        initView();
    }

    private void initView() {
        tv_destination = (TextView)findViewById(R.id.textview_destination);
        et_message = (EditText)findViewById(R.id.edittext_message);
        ib_attach = (ImageButton)findViewById(R.id.imagebutton_message_attach);
        ib_cancel = (ImageButton)findViewById(R.id.imagebutton_message_cancel);
        ib_send = (ImageButton)findViewById(R.id.imagebutton_message_send);

        setDestTextView();

        ib_attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "파일 첨부 : 추후 지원 예정입니다.", Toast.LENGTH_SHORT).show();
            }
        });
        ib_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(R.string.confirm_cancel_message);
                builder.setPositiveButton(R.string.yes, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
                builder.setNegativeButton(R.string.no, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
                builder.create().show();
            }
        });
        ib_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(R.string.confirm_send_message);
                builder.setPositiveButton(R.string.yes, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendMessage();
                        dismiss();
                    }
                });
                builder.setNegativeButton(R.string.no, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
                builder.create().show();
            }
        });
    }

    private void setDestTextView() {
        StringBuilder builder = new StringBuilder();
        builder.append("To: ").append(mRecipeNums).append("명 (").append(mDestinations).append(")");
        tv_destination.setText(builder.toString());
    }

    private void sendMessage() {
        SmsManager smsManager = SmsManager.getDefault();
        String SMS_SENT = "SMS_SENT";
        String SMS_DELIVERED = "SMS_DELIVERED";

        mMessageBody = et_message.getText().toString();
        if(mMessageBody.length() == 0) {
            Toast.makeText(getContext(), R.string.warning_no_message, Toast.LENGTH_SHORT).show();
            return;
        }

        String destArray[] = mDestinations.split(", *");

        ArrayList<String> smsBodyParts = smsManager.divideMessage(mMessageBody);
        ArrayList<PendingIntent> sentPendingIntents = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<PendingIntent>();

        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(getContext(), 0, new Intent(SMS_SENT), 0);
        PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(getContext(), 0, new Intent(SMS_DELIVERED), 0);

        for(int i = 0; i < smsBodyParts.size(); i++) {
            sentPendingIntents.add(sentPendingIntent);
            deliveredPendingIntents.add(deliveredPendingIntent);
        }

        // For when the SMS has been sent
        getContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        //Toast.makeText(context, "SMS sent successfully", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, "Generic failure cause", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, "Service is currently unavailable", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "No pdu provided", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, "Radio was explicitly turned off", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SMS_SENT));

        // For when the SMS has been delivered
        getContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS delivered", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(context, "SMS not delivered", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SMS_DELIVERED));

        // Send a text based SMS
        for(String number : destArray) {
            smsManager.sendMultipartTextMessage(number, null, smsBodyParts, sentPendingIntents, deliveredPendingIntents);
        }

    }
}
