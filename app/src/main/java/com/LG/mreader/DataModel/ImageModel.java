package com.LG.mreader.DataModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;

public class ImageModel {
    private String title;
    private String pageSource;
    private String nextPage;
    private String prevPage;
    private String homePage;
    private PriorityQueue<String> imageSrc=new PriorityQueue<>();
    private List<String> imgList=new ArrayList<>();

    public ImageModel(String title, String pageSource, String nextPage, String prevPage, String homePage, List<String> imgList) {
        this.title = title;
        this.pageSource = pageSource;
        this.nextPage = nextPage;
        this.prevPage = prevPage;
        this.homePage = homePage;
        this.imgList = imgList;
        for(String s:imgList){
            imageSrc.add(s);
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPageSource() {
        return pageSource;
    }

    public void setPageSource(String pageSource) {
        this.pageSource = pageSource;
    }

    public String getNextPage() {
        return nextPage;
    }

    public void setNextPage(String nextPage) {
        this.nextPage = nextPage;
    }

    public String getPrevPage() {
        return prevPage;
    }

    public void setPrevPage(String prevPage) {
        this.prevPage = prevPage;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public PriorityQueue<String> getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(PriorityQueue<String> imageSrc) {
        this.imageSrc = imageSrc;
    }
    public void setImageSrc(String src){
        String[] arr=src.split(",");
        for(String s:arr){
            imageSrc.add(s);
        }
    }
    public List<String> getImgList() {
        return imgList;
    }
    public void setImgList(List<String> imgList) {
        this.imgList = imgList;
        for(String s:imgList){
            imageSrc.add(s);
        }
    }

    public void updateList(){
        for(String s:imgList){
            imageSrc.add(s);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageModel that = (ImageModel) o;
        return Objects.equals(title, that.title) &&
                Objects.equals(pageSource, that.pageSource) &&
                Objects.equals(nextPage, that.nextPage) &&
                Objects.equals(prevPage, that.prevPage) &&
                Objects.equals(homePage, that.homePage) &&
                Objects.equals(imgList, that.imgList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, pageSource, nextPage, prevPage, homePage, imgList);
    }
    
    public boolean isEmpty(){
        return imageSrc.isEmpty();
    }
    public String getImageUrl(){
        if(imageSrc.isEmpty()) return "";
        return imageSrc.poll();
    }
    public int getSize(){
        return imageSrc.size();
    }
}
