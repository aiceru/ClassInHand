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
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Locale;

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

    private RadialGradient rGradBig;
    private RadialGradient rGradSmall;
    private PorterDuffXfermode mode;

    private static final String COLOR_BACKGROUND = "#AA000000";

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
        rGradBig = new RadialGradient(drawPos.x, drawPos.y, 500, R.color.primary, Color.parseColor(COLOR_BACKGROUND), Shader.TileMode.CLAMP);
        rGradSmall = new RadialGradient(drawPos.x, drawPos.y, 120, R.color.primary_dark, R.color.primary, Shader.TileMode.CLAMP);
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

        paint.reset();
        paint.setAntiAlias(true);
        paint.setShader(rGradBig);
        canvas.drawCircle(drawPos.x, drawPos.y, screen.y, paint);

        paint.reset();
        paint.setAntiAlias(true);
        paint.setShader(rGradSmall);
        canvas.drawCircle(drawPos.x, drawPos.y, 110, paint);

        paint.reset();
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        canvas.drawCircle(drawPos.x, drawPos.y, 100, paint);

        TextPaint textPaint = new TextPaint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(50);
        StaticLayout textLayout = new StaticLayout(getContext().getString(msgId),
                textPaint, canvas.getWidth() * 8 / 10, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
        canvas.save();

        float textY = drawPos.y > screen.y * 0.5 ? drawPos.y - 200 - textLayout.getHeight() : drawPos.y + 200;
        canvas.translate(screen.x * 0.1f, textY);
        textLayout.draw(canvas);
        canvas.restore();
    }

    private Point getScreenSize() {
        int x = getContext().getResources().getDisplayMetrics().widthPixels;
        int y = getContext().getResources().getDisplayMetrics().heightPixels;
        return new Point(x, y);
    }
}
