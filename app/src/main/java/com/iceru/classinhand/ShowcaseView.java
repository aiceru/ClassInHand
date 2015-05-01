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
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by iceru on 15. 4. 29..
 */
public class ShowcaseView extends View {
    private static final String TAG = ShowcaseView.class.getName();

    private Paint   paint;
    private int     msgId;
    private Context context;
    private Point   screen;
    private Point   targetPos;
    private Point   targetSize;
    private Point   drawPos;

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
        targetPos = new Point();
        targetSize = new Point();
        drawPos = new Point();
        rGrad = new RadialGradient(drawPos.x, drawPos.y, screen.y / 2, Color.WHITE, Color.BLACK, Shader.TileMode.CLAMP);
        mode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);
    }

    public void setTargetPosition(int x, int y) {
        targetPos.x = x;
        targetPos.y = y;
    }

    public void setTargetSize(int w, int h) {
        targetSize.x = w;
        targetSize.y = h;
        drawPos.x = targetPos.x + targetSize.x/2;
        drawPos.y = targetPos.y;
        rGrad = new RadialGradient(drawPos.x, drawPos.y, screen.y / 2, Color.BLUE, Color.WHITE, Shader.TileMode.CLAMP);
    }

    public void setMessageId(int msgId) {
        this.msgId = msgId;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(targetPos == null || msgId == 0) {
            Log.d(TAG, "targetPos or msg not set");
        }
        paint.setAntiAlias(true);

        paint.setAlpha(100);
        paint.setShader(rGrad);
        canvas.drawCircle(drawPos.x, drawPos.y, screen.y, paint);

        paint.setXfermode(mode);
        canvas.drawCircle(drawPos.x, drawPos.y, 100, paint);
    }

    private Point getScreenSize() {
        int x = getContext().getResources().getDisplayMetrics().widthPixels;
        int y = getContext().getResources().getDisplayMetrics().heightPixels;
        return new Point(x, y);
    }
}
