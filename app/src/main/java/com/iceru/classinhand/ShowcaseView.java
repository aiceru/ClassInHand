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
    private Point   screen;

    private RadialGradient rGrad;
    private PorterDuffXfermode mode;

    public ShowcaseView(Context context) {
        super(context);
        context = context;
        paint = new Paint();
        screen = getScreenSize();
        rGrad = new RadialGradient(200, 200, screen.y / 2, Color.WHITE, Color.BLACK, Shader.TileMode.CLAMP);
        mode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);
    }

    public ShowcaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        context = context;
        paint = new Paint();
        screen = getScreenSize();
        rGrad = new RadialGradient(200, 200, screen.y / 2, Color.WHITE, Color.BLACK, Shader.TileMode.CLAMP);
        mode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);
    }

    public ShowcaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        context = context;
        paint = new Paint();
        screen = getScreenSize();
        rGrad = new RadialGradient(200, 200, screen.y / 2, Color.WHITE, Color.BLACK, Shader.TileMode.CLAMP);
        mode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);
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


        paint.setAlpha(100);
        paint.setShader(rGrad);
        canvas.drawCircle(200, 200, screen.y, paint);

        paint.setXfermode(mode);
        canvas.drawCircle(200, 200, 300, paint);
    }

    private Point getScreenSize() {
        int x = getContext().getResources().getDisplayMetrics().widthPixels;
        int y = getContext().getResources().getDisplayMetrics().heightPixels;
        return new Point(x, y);
    }
}
