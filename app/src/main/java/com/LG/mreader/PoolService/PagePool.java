package com.LG.mreader.PoolService;

import android.net.Uri;

import com.LG.mreader.DataModel.Page;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PagePool {

    private static PagePool instance;
    private ConcurrentHashMap<String, Page> pool;

    private PagePool() {
        pool = new ConcurrentHashMap<>();
    }

    public static PagePool getInstance() {
        if (instance == null) {
            synchronized (PagePool.class){
                if(instance==null){
                    instance = new PagePool();
                }
            }
        }
        return instance;
    }

    public Page getPage(String key) {
        return pool.get(key);
    }

    public void putPage(String key, Page page) {
        pool.put(key, page);
    }

    public void removePage(String key) {
        pool.remove(key);
    }

    public boolean containsKey(String key) {
        return pool.containsKey(key);
    }

    public int size() {
        return pool.size();
    }

    public void clear() {
        pool.clear();
    }
    public Page getOrCreatePage(String key) {
        if (pool.containsKey(key)) {
            return pool.get(key);
        } else {
            Page page = new Page(key);
            pool.put(key, page);
            return page;
        }
    }
}
