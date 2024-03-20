package com.LG.mreader.DataModel;

import java.util.List;

public class PageData {
    private String homePage="";
    private String title="";
    private String pageSrc="";
    private String nextPage="";
    private String prevPage="";
    private String imgSrc="";

    public PageData() {
    }

    public PageData(String homePage, String title, String pageSrc, String nextPage, String prevPage, String imgSrc) {
        this.homePage = homePage;
        this.title = title;
        this.pageSrc = pageSrc;
        this.nextPage = nextPage;
        this.prevPage = prevPage;
        this.imgSrc = imgSrc;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPageSrc() {
        return pageSrc;
    }

    public void setPageSrc(String pageSrc) {
        this.pageSrc = pageSrc;
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

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }
}
