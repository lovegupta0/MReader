package com.LG.mreader.Middleware;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.Observer;

import com.LG.mreader.AppRepository.AppRepository;
import com.LG.mreader.DataModel.PageData;
import com.LG.mreader.DataModel.ViewImageDataModel;
import com.LG.mreader.ViewModel.ImageViewModel;
import com.LG.mreader.ViewModel.WebViewModel;
import com.google.gson.Gson;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class WebviewRepoMiddleware {
    private AppRepository repo;
    private ImageViewModel imageViewModel;

    public WebviewRepoMiddleware(AppRepository repo,ImageViewModel imageViewModel){
        this.repo=repo;
        this.imageViewModel=imageViewModel;
    }
    public void addviewImageList(String str,String url){
        try {
            String[] obj=str.split("~#");
            String hompage=obj[0];
            String pageSrc=obj[1];
            String title=obj[2];
            String imgSrc=obj[5];
            String prevPage=obj[4];
            String nextPage=obj[3];
            String[] img=imgSrc.split(",");
            List<String> imgList= Arrays.stream(img).filter(e->e.contains("chapter")).collect(Collectors.toList());

            if(imgList.size()>0 && url.contains("all-pages")){
                if (!compareData(hompage,pageSrc,title,prevPage,nextPage,imgList)){
                    Log.d("hello","Incomming: "+Integer.toString(imgList.size()));
                    repo.clearViewImage();
                    repo.addViewImageList(imgList,pageSrc);
                    imageViewModel.setShowImageView(true);
                    setData(hompage,pageSrc,title,prevPage,nextPage,imgList);

                }
            }
            List<ViewImageDataModel> imglst=repo.getViewImage().getValue();

        }catch (Exception e){
            Log.e("emsg",e.getMessage());
        }
    }

    private boolean compareData(String homepage,String pageSrc, String title, String prevPage,String nextPage,List<String> lst ){
        if(!imageViewModel.getHomePage().equals(homepage)){
            Log.d("hello","Home false");
            return false;
        }
        if(!imageViewModel.getTitle().equals(title))  {
            Log.d("hello","title false");
            return false;
        }
        if (!imageViewModel.getPageSrc().equals(pageSrc))  {
            Log.d("hello","pageSrc false");
            return false;
        };
        if (!imageViewModel.getNextPage().equals(nextPage)){
            Log.d("hello","nextPage false");
            return false;
        }
        if (!imageViewModel.getPrevPage().equals(prevPage)) {
            Log.d("hello","prevPage false");
            return false;
        }
        HashMap<String,Integer> map=new HashMap<>();
           if(imageViewModel.getImgList()!=null){
               for (String s: imageViewModel.getImgList()){
                   map.put(s,1);
               }
               for(String key:lst){
                   if(!map.containsKey(key)) return false;
               }
           }

        return true;
    }
    private void setData(String homepage,String pageSrc, String title, String prevPage,String nextPage,List<String> lst ){

        imageViewModel.setHomePage(homepage);
        imageViewModel.setImgList(lst);
        imageViewModel.setPageSrc(pageSrc);
        imageViewModel.setTitle(title);
        imageViewModel.setNextPage(nextPage);
        imageViewModel.setPrevPage(prevPage);

    }
}
