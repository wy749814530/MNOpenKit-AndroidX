package com.mnopensdk.demo.utils;

import com.bumptech.glide.load.model.GlideUrl;

/**
 * Created by hjz on 2017/11/15.
 */

public class MyGlideUrl extends GlideUrl{
    private String mUrl;
    public MyGlideUrl(String  url) {
        super(url);
        mUrl = url;
    }

    @Override
    public String getCacheKey() {
        return  mUrl.replace(findTokenParam(), "");
    }


    private String findTokenParam() {
        String tokenParam = "";
        int tokenKeyIndex = mUrl.indexOf("?access_token=") >= 0 ? mUrl.indexOf("?access_token=") : mUrl.indexOf("&access_token=");
        if (tokenKeyIndex != -1) {
            int nextAndIndex = mUrl.indexOf("&", tokenKeyIndex + 1);
            if (nextAndIndex != -1) {
                tokenParam = mUrl.substring(tokenKeyIndex + 1, nextAndIndex + 1);
            } else {
                tokenParam = mUrl.substring(tokenKeyIndex);
            }
        }
        return tokenParam;
    }
}
