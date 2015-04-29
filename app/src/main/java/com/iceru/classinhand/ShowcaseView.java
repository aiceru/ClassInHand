package com.iceru.classinhand;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.util.jar.Attributes;

/**
 * Created by iceru on 15. 4. 29..
 */
public class ShowcaseView extends View {
    private static final String TAG = ShowcaseView.class.getName();

    private Paint   paint;
    private View    target;
    private int     msgId;
    private Context context;

    public ShowcaseView(Context context) {
        super(context);
        context = context;
        paint = new Paint();
    }

    public ShowcaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        context = context;
        paint = new Paint();
    }

    public ShowcaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        context = context;
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
        paint.setAntiAlias(true);
        Point screen = getScreenSize();

        /*paint.setARGB(200, 100, 100, 100);
        canvas.drawRect(0, 0, screen.x, screen.y, paint);*/


        paint.setAlpha(100);
        paint.setShader(new RadialGradient(200, 200, screen.y, Color.WHITE, Color.GRAY, Shader.TileMode.CLAMP));
        canvas.drawCircle(200, 200, screen.y, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawCircle(200, 200, 300, paint);
    }

    private Point getScreenSize() {
        int x = getContext().getResources().getDisplayMetrics().widthPixels;
        int y = getContext().getResources().getDisplayMetrics().heightPixels;
        return new Point(x, y);
    }
}
