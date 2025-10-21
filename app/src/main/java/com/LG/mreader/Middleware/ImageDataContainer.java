package com.LG.mreader.Middleware;

import com.LG.mreader.DataModel.Chapter;
import com.LG.mreader.DataModel.ImageModel;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class ImageDataContainer {
    private static ImageDataContainer instance;
    private BlockingQueue<Chapter> imageModels;

    private Chapter model;
    private ImageDataContainer(){
        imageModels=new LinkedBlockingQueue<>();
    }

    public static ImageDataContainer getInstance(){
        if(instance==null){
            synchronized (ImageDataContainer.class){
                if(instance==null){
                    instance=new ImageDataContainer();
                }
            }
        }
        return instance;

    }

    public boolean isEmpty(){
        return imageModels.isEmpty() ;
    }
    public Chapter getModel(){
        if(!isEmpty()){
            try {
                model=imageModels.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return model;
    }

    public void addImageModel(Chapter model) {
        imageModels.add(model);
    }



}
