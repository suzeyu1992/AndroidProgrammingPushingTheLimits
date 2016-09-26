package com.szysky.note.multitouch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Author :  suzeyu
 * Time   :  2016-09-26  下午3:29
 * Blog   :  http://szysky.com
 * GitHub :  https://github.com/suzeyu1992
 *
 * ClassDescription :  跟随手势显示多点触控自定义View
 */
public class MultiTouchView extends View{

    /**
     * 当前View支持的同一时间内最大触控点个数
     */
    public static final int MAX_FINGERS = 5;

    /**
     *  用于在多点触控移动的时候 (包括down之后和up之前的事件), 记录的所有滑动轨迹Path
     *  数组的下标就是触摸事件的唯一标识id pointerID
     */
    private Path[] mFingerPaths = new Path[MAX_FINGERS];
    private RectF mPathBounds = new RectF();

    /**
     *  在up事件以后, 已经完成的一个完整的手势轨迹记录
     */
    private ArrayList<Path> mCompletedPaths;

    /**
     *  手势的画笔
     */
    private Paint mFingerPaint;

    private final String TAG = MultiTouchView.class.getSimpleName();

    public MultiTouchView(Context context) {
        super(context);
    }

    public MultiTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiTouchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mCompletedPaths = new ArrayList<>();

        // 初始化画笔并设置属性
        mFingerPaint = new Paint();
        mFingerPaint.setAntiAlias(true);
        mFingerPaint.setColor(Color.BLACK);
        mFingerPaint.setStyle(Paint.Style.STROKE);
        mFingerPaint.setStrokeWidth(6);
        mFingerPaint.setStrokeCap(Paint.Cap.BUTT);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        // 绘制已经完成过的手势事件的所有path路径
        // 如果不循环绘制, 那么当一个触控点到达了up事件之后, 那么这个触控点从down到up的事件的轨迹将会消失.
        for (Path completedPath : mCompletedPaths) {
            canvas.drawPath(completedPath, mFingerPaint);
        }

        // 绘制当前触摸屏上的触摸事件正在进行的轨迹 (down ~ up)
        for (Path fingerPath : mFingerPaths) {
            if (fingerPath != null){
                canvas.drawPath(fingerPath, mFingerPaint);
            }
        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e(TAG, "----------------------------------------------事件开始----------------------------------------------" );
        // 获得当前触摸屏上的触摸个数
        int pointerCount = event.getPointerCount();
        // 支持的最大记录数为 MAX_FINGERS所指定数值
        int cappedPointerCount = pointerCount > MAX_FINGERS ? MAX_FINGERS : pointerCount;
        Log.d(TAG, "pointerCount:"+pointerCount);


        // 获得当前触摸事件对象所在系统保存队列中的位置
        int actionIndex = event.getActionIndex();
        // 获得当前触摸事件的动作类型,  这里包括 MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_POINTER_UP 多点触控按下和多点触控抬起(对应数值5, 6)
        int action = event.getActionMasked();
        // 此值返回的是从down开始一直到up结束一个完整事件, 这个生命周期的一个标志id.
        int pointerId = event.getPointerId(actionIndex);
        Log.d(TAG, "actionIndex:"+actionIndex
                +"\r\naction:"+action
                +"\r\npointerId:"+pointerId);


        // 检查是否收到按下或者抬起的动作
        if ((action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) && pointerId < MAX_FINGERS ){
            Log.d(TAG, "进入到down事件中: action:"+action+"  ");
            // 当有新的触摸事件开始的时候, 就使用这个事件的标识id来当下标创建一个Path对象存储起来
            mFingerPaths[pointerId] = new Path();

            // 注意虽然在down事件中, actionIndex和pointerId会保持一致, 但最好使用触摸事件所在的队列位置来获取坐标
            mFingerPaths[pointerId].moveTo(event.getX(actionIndex), event.getY(actionIndex));
            Log.e("haha", "down事件 --> actionIndex:"+actionIndex+"     pointerId" + pointerId );

        }else if ((action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) && pointerId < MAX_FINGERS){
            Log.d(TAG, "进入到up事件中: action:"+action+"  ");
            Log.d("haha", "up事件 --> actionIndex:"+actionIndex+"     pointerId" + pointerId );
            // 这里的pointerId和actionIndex不一定保持一致!! 所以一定要正确使用这个两个来获取
            mFingerPaths[pointerId].setLastPoint(event.getX(actionIndex), event.getY(actionIndex));
            mCompletedPaths.add(mFingerPaths[pointerId]);

            // 计算出一个区域, 并对View进行刷新
            mFingerPaths[pointerId].computeBounds(mPathBounds, true);
            invalidate((int) mPathBounds.left, (int) mPathBounds.top, (int) mPathBounds.right, (int) mPathBounds.bottom);
            mFingerPaths[pointerId] = null;

        }

        int tempIndex = 0;
        // 这里主要是move事件时候进行的动态绘制实现
        for (int i = 0; i < cappedPointerCount; i++) {
            // 保证滑动事件不丢失
            while(mFingerPaths[tempIndex] == null){
                tempIndex++;
                if (tempIndex > 4){
                    return true;
                }
            }
            if (mFingerPaths[tempIndex] != null){
                // 通过数组的下标也就是触摸事件的唯一标识id获得队列中的位置
                int index = event.findPointerIndex(tempIndex);
                // 对从down事件或者上一次移动事件的Path进行lineTo移动.
                mFingerPaths[tempIndex].lineTo(event.getX(index), event.getY(index));
                mFingerPaths[tempIndex].computeBounds(mPathBounds, true);
                invalidate((int) mPathBounds.left, (int) mPathBounds.top, (int) mPathBounds.right, (int) mPathBounds.bottom);
                tempIndex++;
            }

        }
        Log.i(TAG, "----------------------------------------------事件结束----------------------------------------------\r\n" );
        return true;
    }
}
