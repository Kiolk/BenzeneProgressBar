package com.github.kiolk.benzenebar;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.util.Pair;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BenzeneBar extends View {

    private static final int MIN_BAR_SIZE = 100;

    private int mViewHeight;
    private List<Pair<Pair<Float, Float>, Pair<Float, Float>>> radicalPoints;

    private Paint mFigurePaint;
    private Path mBenzenePath;
    private ObjectAnimator mAnimator;
    private TimerTask mTimerTask;
    private Timer mTimer;
    private Handler mHandler;

    private int mRadicalIndex = 5;
    private boolean isNeedChangeShape;
    private int lastValue;

    public BenzeneBar(Context context) {
        super(context);
        init(null);
    }

    public BenzeneBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public BenzeneBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BenzeneBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private int getRadicalIndex() {
        return mRadicalIndex;
    }

    private void setRadicalIndex(int pRadicalIndex) {
        isNeedChangeShape= true;
        lastValue = mRadicalIndex;
        if (mRadicalIndex == 0) {
            mRadicalIndex = 5;
            invalidate();
        } else {
            --mRadicalIndex;
            invalidate();
        }


//        if(lastValue != pRadicalIndex) {
//            lastValue = pRadicalIndex;
//                isNeedChangeShape = true;
//
//                if (mRadicalIndex == 5) {
//                    mRadicalIndex = 0;
//                    invalidate();
//                } else {
//                    ++mRadicalIndex;
//                    invalidate();
//                }
//            }
    }

    private void init(AttributeSet pAttributeSet) {
        mFigurePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFigurePaint.setStyle(Paint.Style.STROKE);
        mFigurePaint.setColor(Color.BLACK);
        mFigurePaint.setStrokeWidth(2);
        mHandler = new Handler();

//        initDrawComponents();

//        mAnimator = ObjectAnimator.ofInt(this, "RadicalIndex", getRadicalIndex(), 300);
//        mAnimator.setDuration(100000);
//        mAnimator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTimer.cancel();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mTimerTask = new TimerTask() {

            @Override
            public void run() {
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        setRadicalIndex(0);
                    }
                });
            }
        };

        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(mTimerTask, 0, 300);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        int selectedWidth;

        switch (MeasureSpec.getMode(widthMeasureSpec)) {
            case MeasureSpec.AT_MOST:
                selectedWidth = MIN_BAR_SIZE;
                break;
            case MeasureSpec.EXACTLY:
                selectedWidth = MIN_BAR_SIZE;
                break;
            case MeasureSpec.UNSPECIFIED:
                selectedWidth = MIN_BAR_SIZE;
                break;
            default:
                selectedWidth = MIN_BAR_SIZE;
                break;
        }

        mViewHeight = selectedWidth;
        initDrawComponents();
        setMeasuredDimension(selectedWidth, selectedWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPath(mBenzenePath, mFigurePaint);
//        for(int index = 1; index <= 7; ++ index) {
        if (isNeedChangeShape) {
            Pair<Pair<Float, Float>, Pair<Float, Float>> line = radicalPoints.get(mRadicalIndex);
            canvas.drawLine(line.first.first, line.first.second, line.second.first, line.second.second, mFigurePaint);
            isNeedChangeShape = false;
        }
//        }
    }

    private Path initDrawComponents() {
        radicalPoints = new ArrayList<>(6);
        List tmpList = new ArrayList();
        Path resultPath = new Path();
        double angle = 2.0 * Math.PI / 6;
        float currentAngle = 0;
        int radius = mViewHeight / 4;
        int radiusOuthside = mViewHeight / 2;
        float centerX = mViewHeight / 2;
        float centerY = mViewHeight / 2;
        resultPath.moveTo(centerX + radius * (float) Math.sin(0), centerY + radius * (float) Math.cos(0));
        for (int sideCounter = 1; sideCounter <= 6; ++sideCounter) {
            float y = (float) (centerX + radius * Math.cos(angle * sideCounter));
            float x = (float) (centerY + radius * Math.sin(angle * sideCounter));
            resultPath.lineTo(x, y);
            Pair<Float, Float> startPoint = new Pair(x, y);
            y = (float) (centerX + radiusOuthside * Math.cos(angle * sideCounter));
            x = (float) (centerY + radiusOuthside * Math.sin(angle * sideCounter));
            Pair<Float, Float> endPoint = new Pair(x, y);
            radicalPoints.add( new Pair<Pair<Float, Float>, Pair<Float, Float>>(startPoint, endPoint));
        }

        tmpList = radicalPoints;
        for(int index = 0; index > tmpList.size(); ++index){
            tmpList.set(5-index, radicalPoints.get(index));
        }
        radicalPoints = tmpList;

        resultPath.moveTo(centerX + radius * (float) Math.cos(centerX), centerY + radius * (float) Math.sin(currentAngle));
        resultPath.close();
        mBenzenePath = resultPath;
        return resultPath;
    }
}
