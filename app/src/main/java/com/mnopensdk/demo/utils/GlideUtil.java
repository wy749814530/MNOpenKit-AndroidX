package com.mnopensdk.demo.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.EmptySignature;
import com.mnopensdk.demo.GlideApp;
import com.mnopensdk.demo.utils.glideutils.GlideRoundTransform;
import com.mnopensdk.demo.utils.glideutils.OriginalKey;
import com.mnopensdk.demo.utils.glideutils.ProgressInterceptor;
import com.mnopensdk.demo.utils.glideutils.ProgressListener;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.mnopensdk.demo.R;

/**
 * Created by jz on 2017/8/24.
 */

public class GlideUtil {
    private static GlideUtil glideUtil;
    Handler handler = null;

    private GlideUtil() {
        handler = new Handler(Looper.getMainLooper());
    }

    public static GlideUtil getInstance() {
        if (glideUtil == null) {
            glideUtil = new GlideUtil();
        }
        return glideUtil;
    }

    //加载图片(本地resources资源)
    public void load(Context mContext, ImageView view, int resources) {
        Glide.with(mContext).load(resources).into(view);//
    }

    public void load(Activity mActivity, ImageView view, int resources) {
        Glide.with(mActivity).load(resources).into(view);
    }

    public void load(Fragment mFrgment, ImageView view, int resources) {
        Glide.with(mFrgment).load(resources).into(view);
    }

    //加载url
    public void load(Activity mActivity, ImageView view, Uri uri) {
        GlideApp.with(mActivity).load(uri).dontAnimate().placeholder(R.mipmap.home_placeholder_default).into(view);
    }

    public void load(Context context, ImageView view, Uri uri) {
        GlideApp.with(context).load(uri).dontAnimate().placeholder(R.mipmap.home_placeholder_default).into(view);
    }

    //加载url
    public void load(Activity mActivity, ImageView view, String url) {
        GlideApp.with(mActivity).load(url).dontAnimate().placeholder(R.color.backgound_color).error(R.color.backgound_color).into(view);
    }

//    //加载url
//    public void loadPhoto(Activity mActivity, PhotoView view, String url) {
//        GlideApp.with(mActivity).load(url).dontAnimate().placeholder(R.mipmap.portrait_placeholder).into(view);
//    }

    public void load(Context mContext, ImageView view, int loadRes, int defRes, int width, int height) {
        GlideApp.with(mContext).load(loadRes)

                .placeholder(defRes)
                .dontAnimate()
                .override(width, height).centerCrop()
                .into(view);
    }

    public void load(Context mContext, ImageView view, String loadRes, int defRes, int width, int height) {
        GlideApp.with(mContext).load(loadRes)

                .placeholder(defRes)
                .dontAnimate()
                .override(width, height).centerCrop()
                .into(view);
    }

    public void load(Context mContext, ImageView view, Uri loadRes, int defRes, int width, int height) {
        GlideApp.with(mContext).load(loadRes)

                .placeholder(defRes)
                .dontAnimate()
                .override(width, height).centerCrop()
                .into(view);
    }


    public void loadRou(Context mContext, ImageView view, String loadRes, int defRes) {
        GlideApp.with(mContext).load(loadRes)
                .placeholder(defRes)
                .dontAnimate()
                .override(60, 60).centerCrop()
                .transform(new GlideRoundTransform(mContext))
                .into(view);
    }

    public void load(Context mContext, ImageView view, String url) {
        GlideApp.with(mContext).load(url).dontAnimate().placeholder(R.mipmap.home_placeholder_default).into(view);
    }
    public void loadAram(Context mContext, ImageView view, String url) {
        GlideApp.with(mContext).load(url).skipMemoryCache(false).placeholder(R.mipmap.dynamic_play_placeholder_default).into(view);
    }
    //个人头像
    public void loadHeadView(Context mContext, ImageView view, String url) {
        GlideApp.with(mContext).load(url).dontAnimate().placeholder(R.mipmap.mine_head_default).into(view);
    }

    public void loadDev(Fragment mContext, ImageView view, String url) {
        GlideApp.with(mContext).load(url).centerCrop().into(view);
    }
    public void loadDevC(Context mContext, ImageView view, String url) {
        GlideApp.with(mContext).load(url).centerCrop().into(view);
    }

    //截取token，相同图片保证缓存，不相同图片再去请求后台
    public void loadNewDevNOCrop(Context mContext, ImageView view, String url) {
        GlideApp.with(mContext).load(new MyGlideUrl(url)).placeholder(R.mipmap.home_placeholder_default).into(view);

    }
    //截取token，相同图片保证缓存，不相同图片再去请求后台
    public void loadNewDevNOCropCover(Activity mContext, ImageView view, String url) {
        GlideApp.with(mContext).load(new MyGlideUrl(url)).placeholder(R.mipmap.home_placeholder_default).into(view);

    }

    public void loadRound(Context mContext, ImageView view, String url) {
        //new api
        Glide.with(mContext).load(url).apply(new RequestOptions().placeholder(R.mipmap.dynamic_play_placeholder_default).transform(new GlideRoundTransform(mContext))).into(view);
        //or next
        // GlideApp.with(mContext).load(url).placeholder(R.mipmap.dynamic_play_placeholder_default).transform(new GlideRoundTransform(mContext)).into(view);
    }
    public void loadRoundF(Fragment mContext, ImageView view, String url) {
        //new api
        Glide.with(mContext).load(url).apply(new RequestOptions().placeholder(R.mipmap.dynamic_play_placeholder_default).transform(new GlideRoundTransform(mContext))).into(view);
        //or next
        // GlideApp.with(mContext).load(url).placeholder(R.mipmap.dynamic_play_placeholder_default).transform(new GlideRoundTransform(mContext)).into(view);
    }
    public void loadRoundFs(Fragment mContext, ImageView view, String url) {
        //new api
        Glide.with(mContext).load(url).apply(new RequestOptions().placeholder(R.mipmap.dynamic_play_placeholder_default)).into(view);
        //or next
        // GlideApp.with(mContext).load(url).placeholder(R.mipmap.dynamic_play_placeholder_default).transform(new GlideRoundTransform(mContext)).into(view);
    }
    public void load(Context mContext, ImageView view, int width, int height, String url) {
        GlideApp.with(mContext).load(url).dontAnimate().placeholder(R.mipmap.defult_face_head).override(width, height).centerCrop().into(view);
    }

    public void loadLocal(Context mContext, ImageView view, int width, int height, Uri url) {
        GlideApp.with(mContext).load(url).dontAnimate().placeholder(R.mipmap.home_placeholder_default).override(width, height).centerCrop().into(view);
    }

    public void loadLocalXYZ(Context mContext, ImageView view, int width, int height, String url) {
        GlideApp.with(mContext).load(url).dontAnimate().placeholder(R.mipmap.home_placeholder_default).override(width, height).centerCrop().transform(new GlideRoundTransform(mContext)).into(view);
    }

    public void loadLocal(Context mContext, ImageView view, int width, int height, String url) {
        GlideApp.with(mContext).load(url).dontAnimate().placeholder(R.mipmap.home_placeholder_default).override(width, height).centerCrop().into(view);
    }

    public void load(Context mContext, ImageView view, int width, int height, Uri uri) {
        GlideApp.with(mContext).load(uri).dontAnimate().placeholder(R.mipmap.home_placeholder_default).override(width, height).centerCrop().into(view);
    }

    public void load(Context mContext, ImageView view, int width, int height, int resources) {
        GlideApp.with(mContext).load(resources).dontAnimate().placeholder(R.mipmap.home_placeholder_default).override(width, height).centerCrop().into(view);
    }


    //占位符，和淡入淡出效果
    public void load(Activity mActivity, ImageView view, String url, int resources) {
        GlideApp.with(mActivity).load(url)/*.crossFade()*///设置淡入淡出效果，默认300ms,可以传参
                .placeholder(resources).into(view);
    }

    //
    public void load(Context mContext, ImageView view, String url, int resources) {
        GlideApp.with(mContext).load(url).dontAnimate().placeholder(resources).into(view);
    }


    public void progressload(Context mContext, ImageView ivView, String url, ProgressListener progressListener) {
        ProgressInterceptor.addListener(url, progressListener);
        RequestOptions options = new RequestOptions()
                .skipMemoryCache(true)
                .placeholder(R.mipmap.dynamic_play_placeholder_default)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
//                .transforms(new CircleTransform(mContext,2, Color.DKGRAY))
//                .transforms(new BlackWhiteTransformation());
//                .transforms(new BlurTransformation(mContext, 25),new CircleTransform(mContext,2, Color.DKGRAY)) // (0 < r <= 25)
//                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);

        Glide.with(mContext)
                .load(url)
                .apply(options)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if (ProgressInterceptor.getListener(url)!=null){
                            ProgressInterceptor.getListener(url).onLoadFailed();
                        }
                        ProgressInterceptor.removeListener(url);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (ProgressInterceptor.getListener(url)!=null){
                            ProgressInterceptor.getListener(url).onLoadSuc();
                        }
                        ProgressInterceptor.removeListener(url);
                        return false;
                    }
                })
                .into(ivView);

    }
    /**
     * 有缓存时，优先读取缓存
     *
     * @param context
     * @param imageView
     * @param url
     * @param defRes
     * @param widthPixels
     * @param heightPixels
     */
    public void loadCacheImage(Context context, ImageView imageView, String url, int defRes, int widthPixels, int heightPixels) {
        File file = DiskLruCacheWrapper.get(Glide.getPhotoCacheDir(context), 250 * 1024 * 1024).get(new OriginalKey(url, EmptySignature.obtain()));
        String loadPath = null;
        if (file != null) {
            loadPath = getLoadImage(file.getPath());
        } else {
            loadPath = url;
        }

        if (defRes >= 0) {
            if (widthPixels != 0 || heightPixels != 0) {
                load(context, imageView, loadPath, defRes, widthPixels, heightPixels);
            } else {
                load(context, imageView, loadPath, defRes);
            }
        } else {
            load(context, imageView, loadPath);
        }
    }

    /**
     * oldPath: 图片缓存的路径
     */
    private String getLoadImage(String oldPath) {
        String newPath = oldPath + ".jpg";
        try {
            int byteRead;
            File oldFile = new File(oldPath);
            if (oldFile.exists()) {
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ((byteRead = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteRead);
                }
                inStream.close();
            }
            return newPath;
        } catch (Exception e) {
            System.out.println("复制文件操作出错");
            e.printStackTrace();
        }
        return "";
    }

    // 清除图片磁盘缓存，调用Glide自带方法 // 只能在异步线程中执行
    public void clearCacheDisk(final Context mContext, final OnClearCacheDiskBack diskBack) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.i("Glide", "---- start clean ----" + System.currentTimeMillis());
                        Glide.get(mContext).clearDiskCache();
                        LogUtil.i("Glide", "---- start end ----" + System.currentTimeMillis());
                        try { // 防止缓存太小，清除缓存效果一闪而过，延时突出清除缓存效果
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        postExecute(diskBack);
                    }
                }).start();
            } else {
                Glide.get(mContext).clearDiskCache();
                diskBack.onCleanFinished();
            }
        } catch (Exception e) {
            e.printStackTrace();
            diskBack.onCleanFinished();
        }
    }

    // 清除Glide内存缓存 只能在主线程执行
    public void clearCacheMemory(Context context) {
        Glide.get(context).clearMemory();
    }

    private void postExecute(final OnClearCacheDiskBack diskBack) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                diskBack.onCleanFinished();
            }
        });
    }

    public long getCacherSize(Context context) {
        File file = Glide.getPhotoCacheDir(context);
        // LogUtil.i("Glide : ", "GlideCacherPatp : " + file.getPath());
        return getFolderSize(file);
    }

    // 获取指定文件夹内所有文件大小的和
    public long getFolderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File aFileList : fileList) {
                if (aFileList.isDirectory()) {
                    size = size + getFolderSize(aFileList);
                } else {
                    size = size + aFileList.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    public interface OnClearCacheDiskBack {
        void onCleanFinished();
    }
}
