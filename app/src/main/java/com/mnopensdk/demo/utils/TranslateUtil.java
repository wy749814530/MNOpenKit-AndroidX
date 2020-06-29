package com.mnopensdk.demo.utils;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2019/5/8 0008.
 */

public class TranslateUtil {

    /**
     * 设置View 透明度，从一个透明度变化到另一个透明度
     *
     * @param view
     * @param fromAlpha
     * @param toAlpha
     * @param durationMillis 设置动画执行的时间
     * @param fillAfter      透明的设置完成之后是否还原到原来的状态
     */
    public static void setAlpha(View view, float fromAlpha, float toAlpha, long durationMillis, boolean fillAfter) {
        //创建一个AnimationSet对象，参数为Boolean型，
        //true表示使用Animation的interpolator，false则是使用自己的
        AnimationSet animationSet = new AnimationSet(true);
        AlphaAnimation alphaAnimation = new AlphaAnimation(fromAlpha, toAlpha);
        alphaAnimation.setDuration(durationMillis);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setFillAfter(fillAfter);
        view.startAnimation(animationSet);
    }

    /**
     * @param view
     * @param fromDegrees    从哪个旋转角度开始
     * @param toDegrees      转到什么角度
     *                       <p>
     *                       后4个参数用于设置围绕着旋转的圆的圆心在哪里
     * @param pivotXType     确定x轴坐标的类型，有Animation.ABSOLUT绝对坐标、Animation.RELATIVE_TO_SELF相对于自身坐标、Animation.RELATIVE_TO_PARENT相对于父控件的坐标
     * @param pivotXValue    x轴的值，0.5f表明是以自身这个控件的一半长度为x轴
     * @param pivotYType     确定Y轴坐标的类型，有Animation.ABSOLUT绝对坐标、Animation.RELATIVE_TO_SELF相对于自身坐标、Animation.RELATIVE_TO_PARENT相对于父控件的坐标
     * @param pivotYValue    y轴的值，0.5f表明是以自身这个控件的一半长度为x轴
     * @param durationMillis 设置动画执行的时间
     * @param fillAfter      透明的设置完成之后是否还原到原来的状态
     */
    public static void setRotateView(View view, float fromDegrees, float toDegrees, int pivotXType, float pivotXValue,
                                     int pivotYType, float pivotYValue, long durationMillis, boolean fillAfter) {
        AnimationSet animationSet = new AnimationSet(true);
        //参数1：从哪个旋转角度开始
        //参数2：转到什么角度
        //后4个参数用于设置围绕着旋转的圆的圆心在哪里
        //参数3：确定x轴坐标的类型，有Animation.ABSOLUT绝对坐标、Animation.RELATIVE_TO_SELF相对于自身坐标、Animation.RELATIVE_TO_PARENT相对于父控件的坐标
        //参数4：x轴的值，0.5f表明是以自身这个控件的一半长度为x轴
        //参数5：确定y轴坐标的类型
        //参数6：y轴的值，0.5f表明是以自身这个控件的一半长度为x轴
        RotateAnimation rotateAnimation = new RotateAnimation(fromDegrees, toDegrees, pivotXType, pivotXValue, pivotYType, pivotYValue);
        rotateAnimation.setDuration(durationMillis);
        animationSet.addAnimation(rotateAnimation);
        animationSet.setFillAfter(fillAfter);
        view.startAnimation(animationSet);
    }

    public static void setScaleView(View view) {
        AnimationSet animationSet = new AnimationSet(true);
        //参数1：x轴的初始值
        //参数2：x轴收缩后的值
        //参数3：y轴的初始值
        //参数4：y轴收缩后的值
        //参数5：确定x轴坐标的类型
        //参数6：x轴的值，0.5f表明是以自身这个控件的一半长度为x轴
        //参数7：确定y轴坐标的类型
        //参数8：y轴的值，0.5f表明是以自身这个控件的一半长度为x轴
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                0, 0.1f, 0, 0.1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(1000);
        animationSet.addAnimation(scaleAnimation);
        view.startAnimation(animationSet);
    }

    public static void setTranslateView(View view) {
        AnimationSet animationSet = new AnimationSet(true);
        //参数1～2：x轴的开始位置
        //参数3～4：y轴的开始位置
        //参数5～6：x轴的结束位置
        //参数7～8：x轴的结束位置
        TranslateAnimation translateAnimation =
                new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
        translateAnimation.setDuration(1000);
        animationSet.addAnimation(translateAnimation);
        view.startAnimation(animationSet);
    }

    /**
     * 从一个位置移动到另一个位置
     *
     * @param view
     * @param fromXDelta     X轴起始坐标点
     * @param toXDelta       X轴最终坐标点
     * @param fromYDelta     Y轴起始坐标点
     * @param toYDelta       Y轴最终坐标点
     * @param durationMillis 设置动画执行的时间
     * @param fillAfter      设置完成之后是否还原到原来的状态
     */
    public static void setTranslateView(View view, float fromXDelta, float toXDelta, float fromYDelta, float toYDelta, long durationMillis, boolean fillAfter, Animation.AnimationListener listener) {
        TranslateAnimation animation = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
        animation.setDuration(durationMillis);
        animation.setFillAfter(fillAfter);
        animation.setAnimationListener(listener);
        view.startAnimation(animation);
    }

    /**
     * 左右抖动效果
     *
     * @param view
     * @param scaleSmall
     * @param scaleLarge
     * @param shakeDegrees
     * @param duration
     */
    public static void startShakeByView(View view, float scaleSmall, float scaleLarge, float shakeDegrees, long duration) {
        //由小变大
        Animation scaleAnim = new ScaleAnimation(scaleSmall, scaleLarge, scaleSmall, scaleLarge);
        //从左向右
        Animation rotateAnim = new RotateAnimation(-shakeDegrees, shakeDegrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        scaleAnim.setDuration(duration);
        rotateAnim.setDuration(duration / 10);
        rotateAnim.setRepeatMode(Animation.REVERSE);
        rotateAnim.setRepeatCount(10);

        AnimationSet smallAnimationSet = new AnimationSet(false);
        smallAnimationSet.addAnimation(scaleAnim);
        smallAnimationSet.addAnimation(rotateAnim);

        view.startAnimation(smallAnimationSet);
    }

    /**
     * 变化大小的抖动效果
     *
     * @param view
     * @param scaleSmall
     * @param scaleLarge
     * @param shakeDegrees
     * @param duration
     */
    private void startShakeByProperty(View view, float scaleSmall, float scaleLarge, float shakeDegrees, long duration) {
        //先变小后变大
        PropertyValuesHolder scaleXValuesHolder = PropertyValuesHolder.ofKeyframe(View.SCALE_X,
                Keyframe.ofFloat(0f, 1.0f),
                Keyframe.ofFloat(0.25f, scaleSmall),
                Keyframe.ofFloat(0.5f, scaleLarge),
                Keyframe.ofFloat(0.75f, scaleLarge),
                Keyframe.ofFloat(1.0f, 1.0f)
        );
        PropertyValuesHolder scaleYValuesHolder = PropertyValuesHolder.ofKeyframe(View.SCALE_Y,
                Keyframe.ofFloat(0f, 1.0f),
                Keyframe.ofFloat(0.25f, scaleSmall),
                Keyframe.ofFloat(0.5f, scaleLarge),
                Keyframe.ofFloat(0.75f, scaleLarge),
                Keyframe.ofFloat(1.0f, 1.0f)
        );

        //先往左再往右
        PropertyValuesHolder rotateValuesHolder = PropertyValuesHolder.ofKeyframe(View.ROTATION,
                Keyframe.ofFloat(0f, 0f),
                Keyframe.ofFloat(0.1f, -shakeDegrees),
                Keyframe.ofFloat(0.2f, shakeDegrees),
                Keyframe.ofFloat(0.3f, -shakeDegrees),
                Keyframe.ofFloat(0.4f, shakeDegrees),
                Keyframe.ofFloat(0.5f, -shakeDegrees),
                Keyframe.ofFloat(0.6f, shakeDegrees),
                Keyframe.ofFloat(0.7f, -shakeDegrees),
                Keyframe.ofFloat(0.8f, shakeDegrees),
                Keyframe.ofFloat(0.9f, -shakeDegrees),
                Keyframe.ofFloat(1.0f, 0f)
        );

        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, scaleXValuesHolder, scaleYValuesHolder, rotateValuesHolder);
        objectAnimator.setDuration(duration);
        objectAnimator.start();
    }


    public static void stretchAnimation(RelativeLayout view, int width, int height, int formArea, int toArea) {

        int fromWidth;
        int fromHeight;
        int toWidth;
        int toHeight;
        if (formArea == 0) {
            fromWidth = width;
            fromHeight = height;
            toWidth = fromWidth / 2;
            toHeight = fromHeight / 2;
        } else {
            fromWidth = width / 2;
            fromHeight = height / 2;
            toWidth = width;
            toHeight = height;
        }

        LogUtil.i("stretchAnimation", "fromWidth : " + fromWidth + " ，fromHeight : " + fromHeight + " ，toWidth : " + toWidth + " ， toHeight : " + toHeight);
        view.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(fromWidth, fromHeight);
        view.setLayoutParams(layoutParams);

        ScaleAnimation scaleAnimation;
        if (formArea == 0) {
            // 全屏状态，到其他状态
            scaleAnimation = new ScaleAnimation(1, toWidth, 1, toHeight, 0, 0);
        } else if (formArea == 1) {
            // 从第一象限到全屏
            scaleAnimation = new ScaleAnimation(0, toWidth, 0, toHeight, 0, 0);
        } else if (formArea == 2) {
            // 从第二象限到全屏
            scaleAnimation = new ScaleAnimation(fromWidth, toWidth, 0, toHeight, 0, 0);
        } else if (formArea == 3) {
            // 从第三象限到全屏
            scaleAnimation = new ScaleAnimation(0, toWidth, fromHeight, toHeight, 0, 0);
        } else {
            // 从第四象限到全屏
            scaleAnimation = new ScaleAnimation(fromWidth, toWidth, fromHeight, toHeight, 0, 0);
        }

        view.setEnabled(false);

        //(0.5f,1,0.5f,1)放大(1,0.5f,1,0.5f)缩小
        scaleAnimation.setDuration(1000);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams ll = new RelativeLayout.LayoutParams((int) (toWidth), (int) (toHeight));
//                ll.gravity = (Gravity.LEFT | Gravity.BOTTOM);
                ll.setMargins(20, 0, 0, 20);
                view.setLayoutParams(ll);
                view.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(scaleAnimation);
    }
}
