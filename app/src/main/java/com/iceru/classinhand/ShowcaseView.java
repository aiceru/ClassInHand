package com.iceru.classinhand;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.jar.Attributes;

/**
 * Created by iceru on 15. 4. 29..
 */
public class ShowcaseView extends View {
    private static final String TAG = ShowcaseView.class.getName();

    private Paint   paint;
    private View    target;
    private int     msgId;

    public ShowcaseView(Context context) {
        super(context);
        paint = new Paint();
    }

    public ShowcaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
    }

    public ShowcaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
    }

    public void setTarget(View v) {
        this.target = v;
    }

    public void setMessageId(int msgId) {
        this.msgId = msgId;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(target == null || msgId == 0) {
            Log.d(TAG, "target or msg not set");
        }

    }
}
