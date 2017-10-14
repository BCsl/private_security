package com.tools.security.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wushuangshuang on 16/5/19.
 */
public class DrawUtils {
    public static float sDensity = 1.0f;
    public static int sDensityDpi;
    public static float sFontDensity;

    public static int sWidthPixels = -1;
    public static int sHeightPixels = -1;
    public static int sRealWidthPixels = -1;
    public static int sRealHeightPixels = -1;

    private static Class<?> sClass = null;
    private static Method sMethodForWidth = null;
    private static Method sMethodForHeight = null;

    // 在某些机子上存在不同的density值，所以增加两个虚拟值
    public static float sVirtualDensity = -1;
    public static float sVirtualDensityDpi = -1;

    public static int sTouchSlop;

    private static Point sOutSize = new Point();
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * sp 转 px
     *
     * @param spValue
     *            sp大小
     * @return 像素值
     */
    public static int sp2px(Context context,float spValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (scale * spValue);
    }

    /**
     * px转sp
     *
     * @param pxValue
     *            像素大小
     * @return sp值
     */
    public static int px2sp(float pxValue) {
        final float scale = sDensity;
        return (int) (pxValue / scale);
    }


    /**
     * 获取控件的高度，如果获取的高度为0，则重新计算尺寸后再返回高度
     * @param view
     * @return
     */
    public static int getViewMeasuredHeight(View view){
        calcViewMeasure(view);
        return view.getMeasuredHeight();
    }

    /**
     * 获取控件的宽度，如果获取的宽度为0，则重新计算尺寸后再返回宽度
     * @param view
     * @return
     */
    public static int getViewMeasuredWidth(View view){
        calcViewMeasure(view);
        return view.getMeasuredWidth();
    }

    /**
     * 测量控件的尺寸
     * @param view
     */
    public static void calcViewMeasure(View view){
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        view.measure(width, expandSpec);
    }

    /**
     * 是否竖屏
     *
     * @param context
     * @return
     */
    public static boolean isPortrait(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point sOutSize = new Point();

        int width = 0;
        int height = 0;
        if ( Build.VERSION.SDK_INT >= 14) {
            display.getSize(sOutSize);
            width = sOutSize.x;
            height = sOutSize.y;
        } else {
            width = display.getWidth();
            height = display.getHeight();
        }
        return width < height;
    }

    @SuppressLint("NewApi")
    public synchronized static void resetDensity(Context context) {
        if (context != null && null != context.getResources()) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            sDensity = metrics.density;
            sFontDensity = metrics.scaledDensity;
            sDensityDpi = metrics.densityDpi;

            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            if (Build.VERSION.SDK_INT >= 14) {
                display.getSize(sOutSize);
                sWidthPixels = sOutSize.x;
                sHeightPixels = sOutSize.y;
            } else {
                sWidthPixels = display.getWidth();
                sHeightPixels = display.getHeight();
            }

            try {
                Class<?> clazz = Class.forName("android.view.Display");
                Point realSize = new Point();
                Method method = clazz.getMethod("getRealSize", Point.class);
                method.invoke(display, realSize);
                sRealWidthPixels = realSize.x;
                sRealHeightPixels = realSize.y;
            } catch (Throwable e) {
                sRealWidthPixels = sWidthPixels;
                sRealHeightPixels = sHeightPixels;
            }

            try {
                final ViewConfiguration configuration = ViewConfiguration.get(context);
                if (null != configuration) {
                    sTouchSlop = configuration.getScaledTouchSlop();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 注意：尽量不要使用此方法，建议使用WindowController.getScreenWidth()
     * @param context
     * @return
     */
    public static int getRealWidth(Context context) {
        if (sRealWidthPixels == -1 || sWidthPixels == -1) {
            resetDensity(context);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            return sRealWidthPixels;
        }
        return sWidthPixels;
    }

    /**
     * 注意：尽量不要使用此方法，建议使用WindowController.getScreenHeight()
     * @param context
     * @return
     */
    public static int getRealHeight(Context context) {
        if (sRealHeightPixels == -1 || sHeightPixels == -1) {
            resetDensity(context);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            return sRealHeightPixels;
        }
        return sHeightPixels;
    }

    /**
     * 获取状态栏高度
     * @param context
     * @return
     */
    public static int getStatusbar(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = context.getResources().getDimensionPixelSize(x);
        } catch(Exception e1) {
            e1.printStackTrace();
        }

        return sbar;
    }

    /**
     *
     * @param view
     * @return
     */
    public static int getViewHeight(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        int height = view.getMeasuredHeight();

        return height;
    }

    public static BitmapDrawable zoomDrawable(Context context, Drawable drawable,
                                              int w, int h) {
        if (drawable != null) {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            Bitmap oldbmp = null;
            // drawable 转换成 bitmap
            if (drawable instanceof BitmapDrawable) {
                // 如果传入的drawable是BitmapDrawable,就不必要生成新的bitmap
                oldbmp = ((BitmapDrawable) drawable).getBitmap();
            } else {

                oldbmp = createBitmapFromDrawable(drawable);
            }

            Matrix matrix = new Matrix(); // 创建操作图片用的 Matrix 对象
            float scaleWidth = (float) w / width; // 计算缩放比例
            float scaleHeight = (float) h / height;
            matrix.postScale(scaleWidth, scaleHeight); // 设置缩放比例
            Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
                    matrix, true); // 建立新的 bitmap ，其内容是对原 bitmap 的缩放后的图
            matrix = null;
            return new BitmapDrawable(context.getResources(), newbmp); // 把
            // bitmap
            // 转换成
            // drawable
            // 并返回
        }
        return null;
    }

    public static Bitmap createBitmapFromDrawable(final Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        Bitmap bitmap = null;
        final int intrinsicWidth = drawable.getIntrinsicWidth();
        final int intrinsicHeight = drawable.getIntrinsicHeight();

        try {
            Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                    : Bitmap.Config.RGB_565;
            bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight,
                    config);
        } catch (OutOfMemoryError e) {
            return null;
        }

        Canvas canvas = new Canvas(bitmap);
        // canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
        drawable.draw(canvas);
        canvas = null;
        return bitmap;
    }

    public static Bitmap downloadBitmap(String fileUrl) {
        if (TextUtils.isEmpty(fileUrl)) {
            return null;
        }
        Bitmap bitmap = null;
        final int connectTimeout = 10 * 1000;
        final int readTimeout = 10 * 1000;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(fileUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(false);
            urlConnection.setConnectTimeout(connectTimeout);
            urlConnection.setReadTimeout(readTimeout);
            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                bitmap = BitmapFactory.decodeStream(urlConnection.getInputStream());
            }
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return bitmap;
    }

    /**
     * drawable转成bitmap
     * @param drawable
     * @return
     */
    public static Bitmap drawable2Bitmap(Drawable drawable){
        BitmapDrawable bd = (BitmapDrawable) drawable;
        Bitmap bitmap = bd.getBitmap();
        return bitmap;
    }
}
