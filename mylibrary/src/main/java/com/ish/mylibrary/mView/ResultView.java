package com.ish.mylibrary.mView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Transformation;

import com.ish.mylibrary.R;
import com.ish.mylibrary.mView.utils.DisplayUtil;

/**
 * Created by ish on 2018/3/18.
 */

public class ResultView extends View {
    private boolean ischeck = false;
    //矩形画笔
    private Paint recPaint;
    //圆环画笔
    private Paint ringPaint;
    //白圆画笔
    private Paint whiteCirclePaint;
    //内圆画笔
    private Paint innerCirclePaint;
    //标志画笔
    private Paint signalPaint;
    //圆环粗度
    private float ringWidth;
    //view的大小
    private float mWidth;
    private float mHeight;
    //圆弧经过的角度
    private float sweepAngle;
    //白圆的半径
    private float whiteRadius;
    //内圆的半径
    private float innerRadius;
    //矩形边长
    private float recLength;
    //边距
    private float padding;
    //钩的偏移
    private float offset;
    //是否开始画钩
    private boolean flag = false;
    //类型
    private int type;
    public static final int TYPE_SUCESS = 1;
    public static final int TYPE_FAILD = 2;
    public static final int TYPE_WARN = 3;
    //钩的路径
    private Path mPath;
    //是否设置了颜色
    private boolean ifSetColor;
    private int themeColor;
    private int backColor;
    private int time;
    //
    private float centerX;
    private float centerY;
    private float signalWidth;

    private CircleBarAnim circleBarAnim;
    private SmallerCircleAnim smallerCircleAnim;
    private BiggerCircleAnim biggerCircleAnim;


    /**
     * 动画类
     */
    public class CircleBarAnim extends Animation {
        public CircleBarAnim() {
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            sweepAngle = interpolatedTime * 360;
            postInvalidate();
        }
    }

    public class SmallerCircleAnim extends Animation {
        public SmallerCircleAnim() {
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            //最后+2是最后有一个像素的环
            whiteRadius = (1 - interpolatedTime) * (recLength - ringWidth + 3) / 2;
            postInvalidate();
        }
    }

    public class BiggerCircleAnim extends Animation {
        public BiggerCircleAnim() {
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            //最后+2是最后有一个像素的环
            if (interpolatedTime < 0.5) {
                innerRadius = (float) (1 + interpolatedTime * 0.5) * (recLength - ringWidth) / 2 + 1;
            } else {
                flag = true;
                innerRadius = (float) (1 + (1 - interpolatedTime) * 0.5) * (recLength - ringWidth) / 2 + 1;
            }
            postInvalidate();
        }
    }

    public ResultView(Context context) {
        super(context);
    }

    public ResultView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initAnim();
    }

    public ResultView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        initAnim();
    }

    /**
     * 初始化画笔数据
     */
    public void initAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ResultView);
        themeColor = a.getColor(R.styleable.ResultView_theme_color, -1);
        backColor = a.getColor(R.styleable.ResultView_back_color, getResources().getColor(R.color.white));
        type = a.getInteger(R.styleable.ResultView_mType, TYPE_SUCESS);
        ringWidth = a.getDimension(R.styleable.ResultView_ring_width, dp2px(5f));
        time = a.getInt(R.styleable.ResultView_duration, 1000);
        //根据type调整颜色
        if(themeColor==-1){
            switch (type) {
                case TYPE_SUCESS:
                    themeColor = getResources().getColor(R.color.green);
                    break;
                case TYPE_FAILD:
                    themeColor = getResources().getColor(R.color.red);
                    break;
                case TYPE_WARN:
                    themeColor = getResources().getColor(R.color.oringe);
                    break;
                default:
            }
        }

        a.recycle();

        if (mPath == null) {
            mPath = new Path();
        }

        ifSetColor = false;
        recPaint = new Paint();
        recPaint.setStyle(Paint.Style.FILL);
        recPaint.setColor(backColor);
        //圆环画笔
        ringPaint = new Paint();
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeWidth(ringWidth);
        ringPaint.setColor(themeColor);
        ringPaint.setAntiAlias(true);
        ringPaint.setStrokeCap(Paint.Cap.ROUND);
        //白圆画笔
        whiteCirclePaint = new Paint();
        whiteCirclePaint.setStyle(Paint.Style.FILL);
        whiteCirclePaint.setColor(backColor);
        whiteCirclePaint.setAntiAlias(true);
        //主题色圆画笔
        innerCirclePaint = new Paint();
        innerCirclePaint.setStyle(Paint.Style.FILL);
        innerCirclePaint.setColor(themeColor);
        innerCirclePaint.setAntiAlias(true);
        //标志画笔
        signalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        signalPaint.setStyle(Paint.Style.STROKE);
        signalPaint.setColor(backColor);
        signalPaint.setAntiAlias(true);
        signalPaint.setStrokeWidth(ringWidth);
        signalPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    /**
     * 初始化动画
     */
    public void initAnim() {
        circleBarAnim = new CircleBarAnim();
        smallerCircleAnim = new SmallerCircleAnim();
        biggerCircleAnim = new BiggerCircleAnim();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        padding = mWidth / 12;
        mWidth = mWidth - padding * 2;
        mHeight = MeasureSpec.getSize(heightMeasureSpec) - padding * 2;
        recLength = mWidth - ringWidth;
        offset = ringWidth;
        centerX = (mWidth) / 2 + padding;
        centerY = (mHeight) / 2 + padding;
        signalWidth = recLength / 8;
        switch (type) {
            case TYPE_SUCESS:
                mPath.reset();
                mPath.moveTo(centerX - offset - signalWidth, centerY);
                mPath.lineTo(centerX - offset, centerY + signalWidth);
                mPath.moveTo(centerX - offset, centerY + signalWidth);
                mPath.lineTo(centerX - offset + 2 * signalWidth, centerY - signalWidth);
                break;
            case TYPE_FAILD:
                mPath.reset();
                mPath.moveTo(centerX - signalWidth, centerY - signalWidth);
                mPath.lineTo(centerX + signalWidth, centerY + signalWidth);
                mPath.moveTo(centerX + signalWidth, centerY - signalWidth);
                mPath.lineTo(centerX - signalWidth, centerY + signalWidth);
                break;
            case TYPE_WARN:
                mPath.reset();
                mPath.moveTo(centerX, centerY - 2 * signalWidth);
                mPath.lineTo(centerX, centerY + 0.8f * signalWidth);
                mPath.moveTo(centerX, centerY + 1.8f * signalWidth);
                mPath.lineTo(centerX, centerY + 2f * signalWidth);
                break;
            default:
        }
    }

    private int getMySize(int defaultSize, int measureSpec) {
        int mySize = defaultSize;

        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        switch (mode) {
            case MeasureSpec.UNSPECIFIED: {//如果没有指定大小，就设置为默认大小
                mySize = defaultSize;
                break;
            }
            case MeasureSpec.AT_MOST: {//如果测量模式是最大取值为size
                //我们将大小取最大值,你也可以取其他值
                mySize = size;
                break;
            }
            case MeasureSpec.EXACTLY: {//如果是固定的大小，那就不要去改变它
                mySize = size;
                break;
            }
            default:
        }
        return mySize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //外切矩形
        RectF rec = new RectF(ringWidth / 2 + padding, ringWidth / 2 + padding,
                mWidth - ringWidth / 2 + padding, mHeight - ringWidth / 2 + padding);
        canvas.drawRect(rec, recPaint);
        //圆环
        canvas.drawArc(rec, 0, sweepAngle, false, ringPaint);
        //主题色实心圆，+1是内圆和外环之间有一个像素的环
        canvas.drawCircle((mWidth) / 2 + padding, (mHeight) / 2 + padding, innerRadius, innerCirclePaint);
        //缩小白色圆圈
        canvas.drawCircle((mWidth) / 2 + padding, (mHeight) / 2 + padding, whiteRadius, whiteCirclePaint);
//        float centerX = (mWidth) / 2 + padding;
//        float centerY = (mHeight) / 2 + padding;
//        float signalWidth = recLength / 8;
//        Path path = new Path();
        if (flag) {
            canvas.drawPath(mPath, signalPaint);
//            switch (type) {
//                case TYPE_SUCESS:
//                    canvas.drawLines(new float[]{               // 绘制一组线 每四数字(两个点的坐标)确定一条线
//                            centerX - offset - signalWidth, centerY, centerX - offset, centerY + signalWidth,
//                            centerX - offset, centerY + signalWidth, centerX - offset + 2 * signalWidth, centerY - signalWidth
//                    }, signalPaint);
//                    break;
//                case TYPE_FAILD:
//                    canvas.drawLines(new float[]{               // 绘制一组线 每四数字(两个点的坐标)确定一条线
//                            centerX - signalWidth, centerY - signalWidth, centerX + signalWidth, centerY + signalWidth,
//                            centerX + signalWidth, centerY - signalWidth, centerX - signalWidth, centerY + signalWidth,
//                    }, signalPaint);
//                    break;
//                case TYPE_WARN:
//                    canvas.drawLines(new float[]{               // 绘制一组线 每四数字(两个点的坐标)确定一条线
//                            centerX, centerY - 2*signalWidth, centerX, centerY + 0.8f*signalWidth,
//                            centerX, centerY + 1.8f*signalWidth, centerX, centerY + 2f*signalWidth,
//                    }, signalPaint);
//                    break;
//                default:
//            }
        }
    }


    /**
     * 设置动画的时间
     *
     * @param time
     */
    public void setDuration(int time) {
        this.time = time;
    }

    /**
     * 播放动画
     */
    public void play() {
        int circleBarTime = time / 2;
        int whiteCircleTime = circleBarTime / 2;
        int innerCircleTime = circleBarTime;

        circleBarAnim.setDuration(circleBarTime);
        smallerCircleAnim.setDuration(whiteCircleTime);
        smallerCircleAnim.setStartOffset(circleBarTime);

        biggerCircleAnim.setDuration(innerCircleTime);
        biggerCircleAnim.setStartOffset(circleBarTime + whiteCircleTime);
        //一起开始播放
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(circleBarAnim);
        animationSet.addAnimation(smallerCircleAnim);
        animationSet.addAnimation(biggerCircleAnim);
        this.startAnimation(animationSet);
        flag = false;
    }

    /**
     * 重置动画
     */
    public void reset() {
        sweepAngle = 0;
        whiteRadius = (recLength - ringWidth + 3) / 2;
        innerRadius = (recLength - ringWidth) / 2 + 1;
        flag = false;
        invalidate();
    }

    /**
     * 主题颜色
     *
     * @param resource
     */
    public void setThemeColor(int resource) {
        ifSetColor = true;
        ringPaint.setColor(getResources().getColor(resource));
        innerCirclePaint.setColor(getResources().getColor(resource));
    }

    /**
     * 背景颜色
     *
     * @param resource
     */
    public void setBackColor(int resource) {
        whiteCirclePaint.setColor(getResources().getColor(resource));
        signalPaint.setColor(getResources().getColor(resource));
    }

    /**
     * 设置圆环的粗度。ps:不要设置太大
     *
     * @param dp
     */
    public void setRingWidth(float dp) {
        ringWidth = dp2px(dp);
        ringPaint.setStrokeWidth(ringWidth);
        signalPaint.setStrokeWidth(ringWidth);
    }

    public void setType(int type) {
        this.type = type;
        //改变颜色
        if (!ifSetColor) {
            switch (type) {
                case TYPE_SUCESS:
                    ringPaint.setColor(getResources().getColor(R.color.green));
                    innerCirclePaint.setColor(getResources().getColor(R.color.green));
                    break;
                case TYPE_FAILD:
                    ringPaint.setColor(getResources().getColor(R.color.red));
                    innerCirclePaint.setColor(getResources().getColor(R.color.red));
                    break;
                case TYPE_WARN:
                    ringPaint.setColor(getResources().getColor(R.color.oringe));
                    innerCirclePaint.setColor(getResources().getColor(R.color.oringe));
                    break;
                default:
            }
        }
        //改变画图
        switch (type) {
            case TYPE_SUCESS:
                mPath.reset();
                mPath.moveTo(centerX - offset - signalWidth, centerY);
                mPath.lineTo(centerX - offset, centerY + signalWidth);
                mPath.moveTo(centerX - offset, centerY + signalWidth);
                mPath.lineTo(centerX - offset + 2 * signalWidth, centerY - signalWidth);
                break;
            case TYPE_FAILD:
                mPath.reset();
                mPath.moveTo(centerX - signalWidth, centerY - signalWidth);
                mPath.lineTo(centerX + signalWidth, centerY + signalWidth);
                mPath.moveTo(centerX + signalWidth, centerY - signalWidth);
                mPath.lineTo(centerX - signalWidth, centerY + signalWidth);
                break;
            case TYPE_WARN:
                mPath.reset();
                mPath.moveTo(centerX, centerY - 2 * signalWidth);
                mPath.lineTo(centerX, centerY + 0.8f * signalWidth);
                mPath.moveTo(centerX, centerY + 1.8f * signalWidth);
                mPath.lineTo(centerX, centerY + 2f * signalWidth);
                break;
            default:
        }
    }


    /**
     * dp to px
     *
     * @param dpValue
     * @return
     */
    private int dp2px(float dpValue) {
        return DisplayUtil.dp2px(getContext(), dpValue);
    }

    /**
     * px to dp
     *
     * @param px
     * @return
     */
    public float px2dp(float px) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }
}
