package com.szysky.note.multitouch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Author :  suzeyu
 * Time   :  2016-09-27  上午9:55
 * Blog   :  http://szysky.com
 * GitHub :  https://github.com/suzeyu1992
 *
 * ClassDescription : 自定义旋转View
 */

public class RotateView extends View{
    public static final String TAG = RotateView.class.getSimpleName();
    private static final double MAX_ANGLE = 1e-1;
    private Paint mPaint;

    /**
     *  根据双指实现的角度
     */
    private float mRotation;
    private Float mPreviousAngle;

    public RotateView(Context context) {
        super(context);
    }

    public RotateView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RotateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);
        mPaint.setAntiAlias(true);

        mPreviousAngle = null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();

        int radius = (int) (width > height ? height * 0.666f : width * 0.666f)/2;
        canvas.drawCircle(width/2, height/2, radius, mPaint);
        canvas.save();
        canvas.rotate(mRotation, width/2, height/2);
        canvas.drawLine(width/2, height*0.1f, width/2, height*0.9f, mPaint);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getPointerCount() == 2){
            // 得到当前形成的角度
            float currentAngle = (float) angle(event);
            if (mPreviousAngle != null){
                // 由于Canvas的旋转使用的是度数, 所以使用Math.toDegrees()把角度转换成度数.
                mRotation -= Math.toDegrees(clamp(mPreviousAngle - currentAngle, -MAX_ANGLE, MAX_ANGLE));
                invalidate();
            }else{
                mPreviousAngle = currentAngle;
            }
        }else{
            mPreviousAngle = null;
        }

        return true;
    }

    /**
     *  计算当前角度
     */
    private static double angle(MotionEvent event) {

        double deltaX = (event.getX(0) - event.getX(1));
        double deltaY = (event.getY(0) - event.getY(1));

        // 使用Math.atan2()实现两个参数的反正切函数, 反正切函数用于计算有两个参数限定的坐标和水平面上正向x轴之间的夹角
        return Math.atan2(deltaY, deltaX);
    }

    /**
     *  让value值至于最小和最大之间
     */
    private static double clamp(double value, double min, double max){
        if (value < min){
            return min;
        }

        if (value > max){
            return max;
        }

        return value;
    }

}
