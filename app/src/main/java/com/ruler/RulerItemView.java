package com.ruler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.core.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.ruler.bean.ScaleMode;
import com.ruler.bean.TimeSlot;
import com.ruler.utils.CUtils;
import com.ruler.utils.DateUtils;
import com.ruler.utils.TimeScaleData;
import java.util.ArrayList;
import java.util.List;

import com.mnopensdk.demo.R;

import static com.ruler.RulerView.viewHeight;

/**
 * Created by HDL on 2018/2/7.
 *
 * @author HDL
 */

public class RulerItemView extends View {
    /**
     * 刻度配置
     */
    private Paint smallRulerPaint = new Paint();//小刻度画笔
    private int rulerColor = ContextCompat.getColor(getContext(), R.color.dark_555555);//刻度的颜色
    private int rulerWidthSamll = CUtils.dip2px(0.5f);//小刻度的宽度
    private int rulerHeightSamll = CUtils.dip2px(10);//小刻度的高度
    private static final int DEFAULT_RULER_SPACE = CUtils.dip2px(10);//设置item默认间隔
    private int rulerSpace = DEFAULT_RULER_SPACE;//刻度间的间隔
    private static final int MAX_SCALE = CUtils.dip2px(39);//最大缩放值
    private static final int MIN_SCALE = CUtils.dip2px(6);//最小缩放值
    /**
     * 大刻度
     */
    private Paint largeRulerPaint = new Paint();//大刻度画笔
    private int rulerWidthBig = CUtils.dip2px(0.5f);//大刻度的宽度
    private int rulerHeightBig = CUtils.dip2px(20);//大刻度的高度
    /**
     * 上下两条线
     */
    private Paint upAndDownLinePaint = new Paint();//刻度画笔
    private int upAndDownLineWidth = CUtils.dip2px(0.5f);//上下两条线的宽度
    private int upAndDownLineColor = rulerColor;

    /**
     * 文本画笔
     */
    private TextPaint keyTickTextPaint = new TextPaint();
    private int textColor = ContextCompat.getColor(getContext(), R.color.dark_555555);//文本颜色
    private int textSize = CUtils.dip2px(12);//文本大小

    /**
     * 视频区域画笔
     * -1: 普通录像
     * 3: 人脸报警
     * 8: 移动侦测
     */
    private Paint middlePaint, orangePaint, record24Paint;/*, bgColorPaint, moveColorPaint, paceColorPaint*/
    ;
    private RectF vedioAreaRect = new RectF();

    /**
     * 选择时间配置
     */
    private Paint selectAreaPaint = new Paint();//选择时间边框
    private int selectTimeBorderColor = 0xfffabb64;//边框颜色
    private Paint vedioArea = new Paint();//已选时间
    private int selectTimeAreaColor = 0x33fabb64;//已选时间颜色
    private float selectTimeStrokeWidth = CUtils.dip2px(8);

    //除数、刻度精度
    private int divisor;

    private Canvas mCanvas;

    public RulerItemView(Context context) {
        this(context, null);
    }

    public RulerItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RulerItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setDivisor();
        initPaint();
    }

    /**
     * 设置精度
     */
    private void setDivisor() {
        switch (scaleMode) {
            case KEY_HOUSE:
                divisor = 10;
                break;
            case KEY_MINUTE:
            default:
                divisor = 1;
        }
    }

    private ScaleMode scaleMode = ScaleMode.KEY_MINUTE;

    public ScaleMode getScaleMode() {
        return scaleMode;
    }

    public void setScaleMode(ScaleMode scaleMode) {
        this.scaleMode = scaleMode;
        setDivisor();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        smallRulerPaint.setAntiAlias(true);
        smallRulerPaint.setColor(rulerColor);
        smallRulerPaint.setStrokeWidth(rulerWidthSamll);

        largeRulerPaint.setAntiAlias(true);
        largeRulerPaint.setColor(rulerColor);
        largeRulerPaint.setStrokeWidth(rulerWidthBig);

        keyTickTextPaint.setAntiAlias(true);
        keyTickTextPaint.setColor(textColor);
        keyTickTextPaint.setTextSize(textSize);
        keyTickTextPaint.setAlpha(255);
       /* //普通录像颜色
        bgColorPaint = new Paint();
        bgColorPaint.setStrokeWidth(2);//这个地方改线宽,单位是像素
        bgColorPaint.setColor(0xff3280e0);
        bgColorPaint.setAlpha(140);//取值范围为0~255，数值越小越透明

        //移动侦测颜色
        moveColorPaint = new Paint();
        moveColorPaint.setStrokeWidth(2);
        moveColorPaint.setColor(0xff5d03b1);
        moveColorPaint.setAlpha(140);

        // 人脸检测f25e5e
        paceColorPaint = new Paint();
        paceColorPaint.setStrokeWidth(2);
        paceColorPaint.setColor(0xffff0000);
        paceColorPaint.setAlpha(140);*/

        middlePaint = new Paint();
        middlePaint.setStrokeWidth(2);
        middlePaint.setColor(ContextCompat.getColor(getContext(), R.color.blue_49a6f6));
        middlePaint.setAlpha(130);

        orangePaint = new Paint();
        orangePaint.setStrokeWidth(2);
        orangePaint.setColor(ContextCompat.getColor(getContext(), R.color.yellow_ffba57));
        orangePaint.setAlpha(130);

        record24Paint = new Paint();
        record24Paint.setStrokeWidth(2);
        record24Paint.setColor(ContextCompat.getColor(getContext(), R.color.color_green));
        record24Paint.setAlpha(130);

        upAndDownLinePaint.setAntiAlias(true);
        upAndDownLinePaint.setColor(upAndDownLineColor);
        upAndDownLinePaint.setStrokeWidth(upAndDownLineWidth);

        selectAreaPaint.setColor(selectTimeBorderColor);
        selectAreaPaint.setAntiAlias(true);
        selectAreaPaint.setStrokeCap(Paint.Cap.ROUND);
        selectAreaPaint.setStyle(Paint.Style.STROKE);
        selectAreaPaint.setStrokeWidth(selectTimeStrokeWidth);

        vedioArea.setColor(selectTimeAreaColor);
        vedioArea.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCanvas = canvas;
        drawUpAndDownLine(canvas);
        drawVedioArea(canvas);
        drawRuler(canvas);
    }

    /**
     * 画视频区域
     *
     * @param canvas
     */

    private void drawVedioArea(Canvas canvas) {
        alarmTimeSlot.clear();
        for (TimeSlot timeSlot : vedioTimeSlot) {
            if (timeSlot.getType() == -1) {
                //1、首先判断是否全部包含了本时间段
                boolean isContainTime = DateUtils.isContainTime(timeSlot, timeIndex * 60 * 1000, timeIndex * 60 * 1000 + 10 * 60 * 1000);
                boolean isLeftTime = DateUtils.isCurrentTimeArea(timeSlot.getStartTimeMillis(), timeIndex * 60 * 1000, timeIndex * 60 * 1000 + 10 * 60 * 1000);
                boolean isRightTime = DateUtils.isCurrentTimeArea(timeSlot.getEndTimeMillis(), timeIndex * 60 * 1000, timeIndex * 60 * 1000 + 10 * 60 * 1000);
                if (isContainTime) {//包含所有（画整个item）
                    vedioAreaRect.set(0, 0, getWidth(), viewHeight);
                    drawVideoRect(timeSlot.getType(), canvas, vedioAreaRect);
                } else if (isLeftTime && isRightTime) {//两端都在（画左边时刻到右边时刻）
                    float distanceX1 = (timeSlot.getStartTimeMillis() - timeIndex * 60 * 1000) * (getWidth() / (10 * 60 * 1000f));
                    float distanceX2 = (timeSlot.getEndTimeMillis() - timeIndex * 60 * 1000) * (getWidth() / (10 * 60 * 1000f));
                    vedioAreaRect.set(distanceX1, 0, distanceX2, viewHeight);
                    drawVideoRect(timeSlot.getType(), canvas, vedioAreaRect);
                } else if (isLeftTime) {//只有左边在（左边时刻开始到item结束都画）
                    float distanceX = (timeSlot.getStartTimeMillis() - timeIndex * 60 * 1000) * (getWidth() / (10 * 60 * 1000f));
                    vedioAreaRect.set(distanceX, 0, getWidth(), viewHeight);
                    drawVideoRect(timeSlot.getType(), canvas, vedioAreaRect);
                } else if (isRightTime) {//只有右边在（画从头开始到右边时刻）
                    float distanceX = (timeSlot.getEndTimeMillis() - timeIndex * 60 * 1000) * (getWidth() / (10 * 60 * 1000f));
                    vedioAreaRect.set(0, 0, distanceX, viewHeight);
                    drawVideoRect(timeSlot.getType(), canvas, vedioAreaRect);
                }
            } else {
                alarmTimeSlot.add(timeSlot);
            }
        }

        for (TimeSlot timeSlot : alarmTimeSlot) {
            //1、首先判断是否全部包含了本时间段
            boolean isContainTime = DateUtils.isContainTime(timeSlot, timeIndex * 60 * 1000, timeIndex * 60 * 1000 + 10 * 60 * 1000);
            boolean isLeftTime = DateUtils.isCurrentTimeArea(timeSlot.getStartTimeMillis(), timeIndex * 60 * 1000, timeIndex * 60 * 1000 + 10 * 60 * 1000);
            boolean isRightTime = DateUtils.isCurrentTimeArea(timeSlot.getEndTimeMillis(), timeIndex * 60 * 1000, timeIndex * 60 * 1000 + 10 * 60 * 1000);
            if (isContainTime) {//包含所有（画整个item）
                vedioAreaRect.set(0, 0, getWidth(), viewHeight);
                drawVideoRect(timeSlot.getType(), canvas, vedioAreaRect);
            } else if (isLeftTime && isRightTime) {//两端都在（画左边时刻到右边时刻）
                float distanceX1 = (timeSlot.getStartTimeMillis() - timeIndex * 60 * 1000) * (getWidth() / (10 * 60 * 1000f));
                float distanceX2 = (timeSlot.getEndTimeMillis() - timeIndex * 60 * 1000) * (getWidth() / (10 * 60 * 1000f));
                vedioAreaRect.set(distanceX1, 0, distanceX2, viewHeight);
                drawVideoRect(timeSlot.getType(), canvas, vedioAreaRect);
            } else if (isLeftTime) {//只有左边在（左边时刻开始到item结束都画）
                float distanceX = (timeSlot.getStartTimeMillis() - timeIndex * 60 * 1000) * (getWidth() / (10 * 60 * 1000f));
                vedioAreaRect.set(distanceX, 0, getWidth(), viewHeight);
                drawVideoRect(timeSlot.getType(), canvas, vedioAreaRect);
            } else if (isRightTime) {//只有右边在（画从头开始到右边时刻）
                float distanceX = (timeSlot.getEndTimeMillis() - timeIndex * 60 * 1000) * (getWidth() / (10 * 60 * 1000f));
                vedioAreaRect.set(0, 0, distanceX, viewHeight);
                drawVideoRect(timeSlot.getType(), canvas, vedioAreaRect);
            }
        }
    }

    /**
     * @param type
     * @param canvas
     * @param vedioAreaRect 绘制报警对应颜色
     */
    public void drawVideoRect(int type, Canvas canvas, RectF vedioAreaRect) {
      /*  if (type == -1) {      // 全录像
            canvas.drawRect(vedioAreaRect, middlePaint);
        }else if (type == 1){ // 门铃来电报警
            canvas.drawRect(vedioAreaRect, paceColorPaint);
        }else if (type== 3){  // 人脸识别
            canvas.drawRect(vedioAreaRect, paceColorPaint);
        }else if (type == 8){ // 移动侦测
            canvas.drawRect(vedioAreaRect, moveColorPaint);
        }*/

        if (type == -1) {      // 全录像
            canvas.drawRect(vedioAreaRect, middlePaint);
        }/* else if (type == 666) {
            // 24小时录像报警测试用的
            canvas.drawRect(vedioAreaRect, record24Paint);
        } */ else { // 事件录像报警
            canvas.drawRect(vedioAreaRect, orangePaint);
        }
//        canvas.drawRect(vedioAreaRect, middlePaint);

    }

    /**
     * 画刻度尺
     *
     * @param canvas
     */
    private void drawRuler(Canvas canvas) {
        float viewWidth = getWidth();
        float itemWidth = viewWidth / (10 / divisor);
        float rightX = 0;
        if (scaleMode == ScaleMode.KEY_HOUSE) {
            //小时级别的画法
            if ((timeIndex / 10) % 6 == 0) { //大刻度
                //画上面的大刻度
//                canvas.drawLine(0, 0, 0, rulerHeightSamll * 2, largeRulerPaint);
                //画下面的大刻度
                canvas.drawLine(0, viewHeight - upAndDownLineWidth, 0, viewHeight - rulerHeightSamll * 2 - upAndDownLineWidth, largeRulerPaint);
                /*float timeStrWidth = keyTickTextPaint.measureText(DateUtils.getHourMinute(timeIndex));
                canvas.drawText(DateUtils.getHourMinute(timeIndex), -timeStrWidth / 2, (viewHeight + textSize)/2, keyTickTextPaint);*/
                String lhours = TimeScaleData.getLargeHourByIndex(timeIndex);
                float timeStrWidth = keyTickTextPaint.measureText(lhours);
                canvas.drawText(lhours, -timeStrWidth / 2, (viewHeight + textSize) / 2, keyTickTextPaint);

            } else {//小刻度
                //画上面的小刻度
//                canvas.drawLine(0, 0, 0, rulerHeightSamll, smallRulerPaint);
                //画下面的小刻度
                canvas.drawLine(0, viewHeight - upAndDownLineWidth, 0, viewHeight - rulerHeightSamll - upAndDownLineWidth, smallRulerPaint);
            }
        } else {
            for (int i = 0; i < 60; i++) {
                if (i == 0 || i == 59) {
                    if (i == 0) {
                        //画上面的大刻度
//                        canvas.drawLine(rightX, 0, rightX, rulerHeightSamll * 2, smallRulerPaint);
                        //画下面的大刻度
                        canvas.drawLine(rightX, viewHeight - upAndDownLineWidth, rightX, viewHeight - rulerHeightSamll * 2 - upAndDownLineWidth, smallRulerPaint);
                        rightX += itemWidth;
                        /*float timeStrWidth = keyTickTextPaint.measureText(DateUtils.getHourMinute(timeIndex));
                        canvas.drawText(DateUtils.getHourMinute(timeIndex), -timeStrWidth / 2, (viewHeight + textSize)/2, keyTickTextPaint);*/
                        String hourMinite = TimeScaleData.getSmallHourByIndex(timeIndex);
                        float timeStrWidth = keyTickTextPaint.measureText(hourMinite);
                        canvas.drawText(hourMinite, -timeStrWidth / 2, (viewHeight + textSize) / 2, keyTickTextPaint);
                    }
                } else if (i % divisor == 0) {
                    //画上面的小刻度
//                    canvas.drawLine(rightX, 0, rightX, rulerHeightSamll, largeRulerPaint);
                    //画下面的小刻度
                    canvas.drawLine(rightX, viewHeight - upAndDownLineWidth, rightX, viewHeight - rulerHeightSamll - upAndDownLineWidth, largeRulerPaint);
                    rightX += itemWidth;
                }
            }
        }
    }

    /**
     * 画上下两条线
     *
     * @param canvas
     */
    private void drawUpAndDownLine(Canvas canvas) {
        int viewWidth = getWidth();
        //画上下两条线
        canvas.drawLine(0, upAndDownLineWidth / 2, viewWidth, upAndDownLineWidth / 2, upAndDownLinePaint);
//        canvas.drawLine(0, viewHeight - upAndDownLineWidth / 2, viewWidth, viewHeight - upAndDownLineWidth / 2, upAndDownLinePaint);
    }

    /**
     * 设置当前时间（处于第几个小时）
     *
     * @param index
     */
    public void setCurTimeIndex(int index) {
        timeIndex = index * 10;
    }

    private int timeIndex;
    /**
     * 视频时间段集合
     */
    private List<TimeSlot> vedioTimeSlot = new ArrayList<>();
    /**
     * 报警录像时间段
     */
    private List<TimeSlot> alarmTimeSlot = new ArrayList<>();

    /**
     * 获取视频时间段
     *
     * @return
     */
    public List<TimeSlot> getVedioTimeSlot() {
        return vedioTimeSlot;
    }

    /**
     * 设置视频时间段
     *
     * @param vedioTimeSlot
     */
    public void setVedioTimeSlot(List<TimeSlot> vedioTimeSlot) {
        this.vedioTimeSlot.clear();
        this.vedioTimeSlot.addAll(vedioTimeSlot);
        postInvalidate();//重绘
    }

    public void setViewHeight(int viewHeight) {
        postInvalidate();
    }
}
