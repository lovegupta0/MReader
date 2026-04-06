package com.mreader.LG.Middleware;

import com.mreader.LG.DataModel.Chapter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ImageDataContainer {
    private static ImageDataContainer instance;
    private BlockingQueue<Chapter> imageModels;

    private Chapter model;
    private  Chapter modelV2;
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
        modelV2=model;
    }
    public Chapter getCurrentChapter() {
        if(model==null) return modelV2;
        return model;
    }

    public void clear(){
        imageModels.clear();
        model=null;
        modelV2=null;
    }


}
