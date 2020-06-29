package com.mnopensdk.demo.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * Created by Administrator on 2019/7/3 0003.
 */

public class CanvasImageUtils {

    public static Bitmap createImage(Bitmap bgsource, Bitmap source) {
        Bitmap newBitmap = Bitmap.createBitmap(bgsource.getWidth(), bgsource.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        //画背景图片
        Paint bgPaint = new Paint();
        canvas.drawBitmap(bgsource, 0, 0, bgPaint);
        int width = source.getWidth();
        int height = source.getHeight();
        int newWidth = bgsource.getWidth() * 1 / 2;
        int newHeight = bgsource.getWidth() * 1 / 2;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        source = Bitmap.createBitmap(source, 0, 0, width, height, matrix, true);

        //把图片画上来
        Paint bitmapPaint = new Paint();
        int bottomHight = bgsource.getHeight() * 128 / 1920;
        canvas.drawBitmap(source, (bgsource.getWidth() - source.getWidth()) / 2, (bgsource.getHeight() - source.getHeight() - bottomHight), bitmapPaint);

        canvas.save(/*Canvas.ALL_SAVE_FLAG*/);
        canvas.restore();
        return newBitmap;
    }

}
