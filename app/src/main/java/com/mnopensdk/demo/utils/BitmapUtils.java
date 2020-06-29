package com.mnopensdk.demo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;

import com.mnopensdk.demo.BaseApplication;
import com.mnopensdk.demo.Constants;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.mnopensdk.demo.R;

/**
 * Created by jz on 2017/8/17.
 */

public class BitmapUtils {

    public static Bitmap getBitmapFromUri(Context mContext, Uri uri) {
        try {
            // 读取uri所在的图片
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 保存图片(头像)
     *
     * @param
     */
    public static File CompressToFile(Bitmap bitmap, String filename, int percent) {
        File fileDir = new File(Constants.photosPath);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        } else {
            File file = new File(Constants.photosPath, filename);
            if (file.isFile() && file.exists()) {
                file.delete();
            }
        }
        File imagefile = new File(Constants.photosPath, filename);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, percent, baos);

        InputStream sbs = new ByteArrayInputStream(baos.toByteArray());
        OutputStream os = null;
        try {
            os = new FileOutputStream(imagefile);
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            try {
                while ((bytesRead = sbs.read(buffer, 0, 1024)) != -1)
                    os.write(buffer, 0, bytesRead);
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (os != null)
                    os.close();
                sbs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return imagefile;
    }


    /**
     * 销毁头像文件(缓存)
     */
    public static void clearCache(String filename) {
        File fileDir = new File(Constants.photosPath);
        if (fileDir.exists()) {
            File file = new File(Constants.photosPath, filename);
            if (file.isFile() && file.exists()) {
                file.delete();
            }
        }
    }

    static double[][] YUV2RGB_CONVERT_MATRIX = {{1, 0, 1.4022}, {1, -0.3456, -0.7145}, {1, 1.771, 0}};

    public static void YUV420SP2RGB565(byte[] rgb, byte[] _y, byte[] _u, byte[] _v, int width, int height) {
        byte[] yuv420sp = new byte[width * height * 3 / 2];

        System.arraycopy(_y, 0, yuv420sp, 0, _y.length);
        System.arraycopy(_u, 0, yuv420sp, _y.length, _u.length);
        System.arraycopy(_v, 0, yuv420sp, _y.length + _u.length, _v.length);

        int uIndex = width * height;
        int vIndex = uIndex + ((width * height) >> 2);
        int gIndex = width * height;
        int bIndex = gIndex * 2;
        int temp = 0;


        for (int y = 0; y < height; y++) {
            long begin = System.currentTimeMillis();

            for (int x = 0; x < width; x++) {
                // R分量
                temp = (int) (yuv420sp[y * width + x] + (yuv420sp[vIndex + (y / 2) * (width / 2) + x / 2] - 128) * YUV2RGB_CONVERT_MATRIX[0][2]);
                rgb[y * width + x] = (byte) (temp < 0 ? 0 : (temp > 255 ? 255 : temp));
                // G分量
                temp = (int) (yuv420sp[y * width + x] + (yuv420sp[uIndex + (y / 2) * (width / 2) + x / 2] - 128) * YUV2RGB_CONVERT_MATRIX[1][1] + (yuv420sp[vIndex + (y / 2) * (width / 2) + x / 2] - 128) * YUV2RGB_CONVERT_MATRIX[1][2]);
                rgb[gIndex + y * width + x] = (byte) (temp < 0 ? 0 : (temp > 255 ? 255 : temp));
                // B分量
                temp = (int) (yuv420sp[y * width + x] + (yuv420sp[uIndex + (y / 2) * (width / 2) + x / 2] - 128) * YUV2RGB_CONVERT_MATRIX[2][1]);
                rgb[bIndex + y * width + x] = (byte) (temp < 0 ? 0 : (temp > 255 ? 255 : temp));
            }

            long end = System.currentTimeMillis();

            System.out.println("costxxx : " + (end - begin) + "ms");
        }
    }


//    public static void YUV420SP2RGB565(byte[] rgb, byte[] _y, byte[] _u, byte[] _v, int width, int height) {
//        final int frameSize = width * height;
//
//        byte[] yuv420sp = new byte[width * height * 3 / 2];
//
//        System.arraycopy(_y, 0, yuv420sp, 0, _y.length);
//        System.arraycopy(_u, 0, yuv420sp, _y.length, _u.length);
//        System.arraycopy(_v, 0, yuv420sp, _y.length + _u.length, _v.length);
//
//        for (int j = 0, yp = 0; j < height; j++) {
//            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
//
//            for (int i = 0; i < width; i++, yp++) {
//                int y = (0xff & ((int) yuv420sp[yp])) - 16;
//                if (y < 0) y = 0;
//                if ((i & 1) == 0) {
//                    v = (0xff & yuv420sp[uvp++]) - 128;
//                    u = (0xff & yuv420sp[uvp++]) - 128;
//                }
//                int y1192 = 1192 * y;
//                int r = (y1192 + 1634 * v);
//                int g = (y1192 - 833 * v - 400 * u);
//                int b = (y1192 + 2066 * u);
//                if (r < 0) r = 0;
//                else if (r > 262143) r = 262143;
//                if (g < 0) g = 0;
//                else if (g > 262143) g = 262143;
//                if (b < 0) b = 0;
//                else if (b > 262143) b = 262143;
//
//                int a = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
//                rgb[yp * 4 + 0] = (byte)(a >> 6 & 0xFF);
//                rgb[yp * 4 + 1] = (byte)(a >> 4 & 0xFF);
//                rgb[yp * 4 + 2] = (byte)(a >> 2 & 0xFF);
//                rgb[yp * 4 + 3] = (byte)(a & 0xFF);
//            }
//        }
//
//        yuv420sp = null;
//    }

    public static Bitmap getRGB565toBitmap(byte[] outBytes, int width, int height) {
        Bitmap bmp = null;
        try {
            byte[] bmpBuff = null;
            ByteBuffer byteBuffer = null;
            if (outBytes == null)
                return null;
            int width_frame = width;
            int height_frame = height;
            if (width_frame > 0 && height_frame > 0) {
                bmpBuff = new byte[width_frame * height_frame * 4];
                bmp = Bitmap.createBitmap(width_frame,
                        height_frame,
                        Bitmap.Config.RGB_565);

                if (bmpBuff != null) {
                    System.arraycopy(outBytes, 0, bmpBuff, 0, outBytes.length);
                    byteBuffer = ByteBuffer.wrap(bmpBuff);
                    bmp.copyPixelsFromBuffer(byteBuffer);
                }
            }
        } catch (Exception e) {

        }

        return bmp;
    }

    //将Byte数组转换成文件
    public static void getFileByBytes(byte[] bytes, /*String filePath,*/ String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        long l = System.currentTimeMillis();
        try {
            File dir = new File(fileName);
            if (!dir.exists() && dir.isDirectory()) {// 判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            //   LogUtil.i("getFileByBytes", "sizekb =" + "DDDDDDDDDDD");
            bos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void saveBitmaptoFile(Bitmap bitmap, String filename) {
        if (bitmap == null) {
            return;
        }
        File f = new File(filename);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static Handler handler = new Handler(Looper.getMainLooper());

    public static void savebytetoFiles(byte[] rgb, String picname, String devType) {
        long l = System.currentTimeMillis();
        String path = Constants.photosPath;
        if (path == null) return;
        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();
        if (SharedPreferUtils.read("local", picname, "").contains(picname)) {
            FileUtil.deleteSeletect(SharedPreferUtils.read("local", picname, ""));
        }
        String _fileName = path + l + picname + ".bmp";
        getFileByBytes(rgb, _fileName);
        saveBitmaptoFile(BitmapUtils.getSmallBitmap(_fileName, 480, 800), _fileName);
        File file = new File(_fileName);
        SharedPreferUtils.write("local", picname, file.getPath());
        try {
            if (file.isFile() && file.exists()) {
                long filesiz = FileSizeUtil.getFileSize(file);
                double sizekb = FileSizeUtil.FormetFileSize(filesiz, FileSizeUtil.SIZETYPE_KB);
            //    LogUtil.i("sizekb", "sizekb = " + sizekb + "file.getPath():" + file.getPath());
                if (sizekb == 12.23 || sizekb == 12.53 || sizekb == 32.49 || sizekb == 14.68 || sizekb == 5.69 || sizekb == 5.55) {
                    FileUtil.deleteSeletect(file.getPath());
                    return;//绿屏
                } else {
                    SharedPreferUtils.write("cutoPic", picname, file.getPath());
                   // handler.post(() -> upData(file, picname));
                }
            } else {
                LogUtil.i("sizekb", "file is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveBitmaptoFiles(Bitmap bitmap, String picname, String devType) {
        if (bitmap == null) {
            return;
        }
        long l = System.currentTimeMillis();
        File f = new File(Constants.photosPath + l + picname + ".bmp");
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
            out.flush();
            out.close();
            File file = new File(Constants.photosPath + l + picname + ".bmp");
            if (file.isFile() && file.exists()) {
                long filesiz = FileSizeUtil.getFileSize(file);
                double sizekb = FileSizeUtil.FormetFileSize(filesiz, FileSizeUtil.SIZETYPE_KB);
                LogUtil.i("getSn", "sizekb = " + sizekb);
                if (sizekb > 0) {
                    SharedPreferUtils.write("cutoPic", picname, f.getPath());
                } else {
                    FileUtil.deleteSeletect(file.getPath());
                }
            } else {
                LogUtil.i("getSn", "file is null");

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogUtil.i("getSn", "FileNotFoundException");
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.i("getSn", "IOException");
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.i("getSn", "FileSizeUtil");
        }
    }

    //2018.8.20
    public static Bitmap byteToBitmap(byte[] imgByte, int x, int y) {
        InputStream input = null;
        Bitmap bitmap = null;
        Options options = new Options();
        LogUtil.i("byteToBitmap", x + "," + y);
        options.outWidth = x;
        options.outHeight = y;
        options.inSampleSize = 1;

        input = new ByteArrayInputStream(imgByte);
        SoftReference softRef = new SoftReference(BitmapFactory.decodeStream(input, null, options));
        bitmap = (Bitmap) softRef.get();
        if (imgByte != null) {
            imgByte = null;
        }
        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static int getinSampleSize(Options options, int x, int y) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int reqHeight = x;
        int reqWidth = y;
        int inSampleSize = 1;

        if (height > width && height > reqHeight) {
            inSampleSize = (int) Math.ceil(height / reqHeight);
            System.out.println(inSampleSize + "inSampleSize" + reqHeight + ",," + reqWidth);
        } else if (height <= width && width > reqWidth) {
            inSampleSize = (int) Math.ceil(width / reqWidth);
            System.out.println(inSampleSize + "inSampleSize");

        }
        System.out.println("inSampleSize" + inSampleSize);
        return inSampleSize;
    }

    /**
     * 压缩图片
     *
     * @param originalPath  原始图片路径
     * @param thumbnailPath 缩略图路径
     * @param i             压缩比例,最终为原图的1/(i^2)
     * @return
     */
    private Bitmap onCreateThumbnail(String originalPath, String thumbnailPath, int i) {
        Options options = new Options();
        //设置为不读内容，值读取边界值
        options.inJustDecodeBounds = true;
        //通过编辑，得到边界值，并存入到option中
        BitmapFactory.decodeFile(originalPath, options);
        //赋值缩放比例
        options.inSampleSize = i;
        //设置显示的图片格式
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        //设置为读取内容，
        options.inJustDecodeBounds = false;
        //得到缩略图
        return BitmapFactory.decodeFile(thumbnailPath, options);
    }

    /**
     * 获取bitmap存储到本地的路径
     *
     * @return
     */
    public static String getFileName() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String strDate = sdf.format(date);
        String path = Constants.ImagePath + BaseApplication.getInstance().getString(R.string.app_name) + File.separator;
        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();
        String fileName = path + strDate + ".bmp";
        return fileName;
    }

    public static Bitmap getSmallBitmap(String filePath, int reqWidth, int reqHeight) {
        final Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static int calculateInSampleSize(Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static void bitmapToString(String filePath) {
        Bitmap bm = getSmallBitmap(filePath, 480, 800);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] b = baos.toByteArray();
        // return Base64.encodeToString(b, Base64.DEFAULT);
    }
}
