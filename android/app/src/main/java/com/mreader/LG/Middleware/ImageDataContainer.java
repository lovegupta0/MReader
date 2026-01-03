package com.mreader.LG.Middleware;

import com.mreader.LG.DataModel.Chapter;

import java.util.concurrent.BlockingQueue;
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
    public Chapter getCurrentChapter() {
        return model;
    }

    public void clear(){
        imageModels.clear();
        model=null;
    }


}
