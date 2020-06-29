package com.mnopensdk.demo.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/7/6 0006.
 */

public class TFStorageBean {
    private Map<String, String> storageMap = new HashMap<>();

    public void addStorage(String key, String value){
        storageMap.put(key, value);
    }

    public String getStorage(String key){
        return storageMap.get(key);
    }
}
