package com.tools.security.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.tools.security.utils.statusbar.StatusBarUtil;

/**
 * description:颜色工具类
 * author: xiaodifu
 * date: 2016/12/18.
 */

public class ColorUtil {

    /**
     * 计算从startColor过度到endColor过程中百分比为franch时的颜色值
     *
     * @param startColor 起始颜色 int类型
     * @param endColor   结束颜色 int类型
     * @param franch     franch 百分比0.5
     * @return 返回int格式的color
     */
    public static int caculateColor(int startColor, int endColor, float franch) {
        String strStartColor = "#" + Integer.toHexString(startColor);
        String strEndColor = "#" + Integer.toHexString(endColor);
        return Color.parseColor(caculateColor(strStartColor, strEndColor, franch));
    }

    /**
     * 计算从startColor过度到endColor过程中百分比为franch时的颜色值
     *
     * @param startColor 起始颜色 （格式#FFFFFFFF）
     * @param endColor   结束颜色 （格式#FFFFFFFF）
     * @param franch     百分比0.5
     * @return 返回String格式的color（格式#FFFFFFFF）
     */
    public static String caculateColor(String startColor, String endColor, float franch) {

        int startAlpha = Integer.parseInt(startColor.substring(1, 3), 16);
        int startRed = Integer.parseInt(startColor.substring(3, 5), 16);
        int startGreen = Integer.parseInt(startColor.substring(5, 7), 16);
        int startBlue = Integer.parseInt(startColor.substring(7), 16);

        int endAlpha = Integer.parseInt(endColor.substring(1, 3), 16);
        int endRed = Integer.parseInt(endColor.substring(3, 5), 16);
        int endGreen = Integer.parseInt(endColor.substring(5, 7), 16);
        int endBlue = Integer.parseInt(endColor.substring(7), 16);

        int currentAlpha = (int) ((endAlpha - startAlpha) * franch + startAlpha);
        int currentRed = (int) ((endRed - startRed) * franch + startRed);
        int currentGreen = (int) ((endGreen - startGreen) * franch + startGreen);
        int currentBlue = (int) ((endBlue - startBlue) * franch + startBlue);

        return "#" + getHexString(currentAlpha) + getHexString(currentRed)
                + getHexString(currentGreen) + getHexString(currentBlue);

    }

    /**
     * 将10进制颜色值转换成16进制。
     */
    public static String getHexString(int value) {
        String hexString = Integer.toHexString(value);
        if (hexString.length() == 1) {
            hexString = "0" + hexString;
        }
        return hexString;
    }

    public static void translateGradientColors(int width, int height, int radius, int startColor, int endColor, long duration, final View view) {
        Rect mRect = new Rect(0, 0, width, height);
        final GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setGradientCenter(0.5f, 0.4f);
        gradientDrawable.setBounds(mRect);
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        gradientDrawable.setGradientRadius(radius);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(startColor, endColor);
        valueAnimator.setEvaluator(new ArgbEvaluator());
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                gradientDrawable.mutate();
                Integer value = (Integer) animation.getAnimatedValue();
                gradientDrawable.setColors(new int[]{value, 0x00000000});
                view.setBackground(gradientDrawable);
            }
        });
        valueAnimator.start();
    }

    public static void translateStatusBarColors(final Activity activity, int startColor, int endColor, long duration,AnimatorListenerAdapter animatorListenerAdapter) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(startColor, endColor);
        valueAnimator.setEvaluator(new ArgbEvaluator());
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                StatusBarUtil.setColorNoTranslucent(activity, value);
            }
        });
        valueAnimator.addListener(animatorListenerAdapter);
        valueAnimator.start();
    }

}
