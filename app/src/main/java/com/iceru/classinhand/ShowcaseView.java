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
    private int     msgId;
    private Context context;
    private Point   screen;
    private Point   target;

    private RadialGradient rGrad;
    private PorterDuffXfermode mode;

    public ShowcaseView(Context context) {
        super(context);
        init(context);
    }

    public ShowcaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShowcaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        context = context;
        paint = new Paint();
        screen = getScreenSize();
        target = new Point();
        rGrad = new RadialGradient(200, 200, screen.y / 2, Color.WHITE, Color.BLACK, Shader.TileMode.CLAMP);
        mode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);
    }

    public void setTargetPosition(int x, int y) {
        target.x = x;
        target.y = y;
        invalidate();
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
        canvas.drawCircle(target.x, target.y, screen.y, paint);

        paint.setXfermode(mode);
        canvas.drawCircle(target.x, target.y, 200, paint);
    }

    private Point getScreenSize() {
        int x = getContext().getResources().getDisplayMetrics().widthPixels;
        int y = getContext().getResources().getDisplayMetrics().heightPixels;
        return new Point(x, y);
    }
}
