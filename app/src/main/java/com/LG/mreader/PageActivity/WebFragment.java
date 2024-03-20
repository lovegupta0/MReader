package com.LG.mreader.PageActivity;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.LG.mreader.AppRepository.AppRepository;
import com.LG.mreader.DataModel.ViewImageDataModel;
import com.LG.mreader.Middleware.WebviewRepoMiddleware;
import com.LG.mreader.R;
import com.LG.mreader.ViewModel.ImageViewModel;
import com.LG.mreader.ViewModel.WebViewModel;
import com.LG.mreader.databinding.FragmentWebBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class WebFragment extends Fragment {
    private FragmentWebBinding webBinding;
    private WebViewModel webViewModel;
    private WebviewRepoMiddleware webviewRepoMiddleware;
    private ImageViewModel imageViewModel;
    private AppRepository repo;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        webBinding=FragmentWebBinding.inflate(inflater, container, false);
        webViewModel=new ViewModelProvider(requireActivity()).get(WebViewModel.class);
        imageViewModel=new ViewModelProvider(requireActivity()).get(ImageViewModel.class);
        webBinding.web.getSettings().setJavaScriptEnabled(true);
        repo=new AppRepository(requireActivity());
        webviewRepoMiddleware=new WebviewRepoMiddleware(repo,imageViewModel);
        webBinding.web.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if(url!=webViewModel.getUrlAddress().getValue()){
                    webViewModel.setUrlAddress(url);
                }

                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onLoadResource(WebView view, String url) {

                webBinding.web.evaluateJavascript("(function(){"+
                        "var obj=document.getElementById('content');" +
                        "var url=obj.getElementsByTagName('a');" +
                        "var homepage=url[0].href;" +
                        "var pageSource=url[1].href;" +
                        "var title=url[1].title;" +
                        "var lst=[];" +
                        "var nextPage=[];" +
                        "var prevPage=[];" +
                        "var img=obj.getElementsByTagName('img');" +
                        "for(let i=0;i<img.length;i++){" +
                        " lst.push(img[i].currentSrc);" +
                        " }" +
                        "for(let i=0;i<url.length;i++){" +
                        " if(url[i].className.includes('next')){" +
                        "nextPage.push(url[i].href);" +
                        "}" +
                        " if(url[i].className.includes('prev')){" +
                        "prevPage.push(url[i].href);" +
                        "}" +
                        "}" +
                        "var imgSrc=lst.join(',');" +
                        "var data= homepage+'~#'+pageSource+'~#'+title+'~#'+nextPage+'~#'+prevPage+'~#'+imgSrc ;"+
                        "return data;"+
                        "})();",value -> {

                   if(value!=null && value.length()>0){
                       webviewRepoMiddleware.addviewImageList(value,webViewModel.getUrlAddress().getValue());

                   }
                });
/*
                webBinding.web.evaluateJavascript("(function(){" +
                        "var obj=document.getElementsByTagName('a');" +
                        "var lst=[];" +
                        "for(let i=0;i<obj.length;i++){" +
                        "   if(obj[i].className.includes('next')){ " +
                        "lst.push(obj[i].href);" +
                        "}" +
                        "}" +
                        "return lst.join(',')" +
                        "})();",value -> {
                    //Log.d("javascript","Next chapt"+value.split(",")[0]);
                });*/

                super.onLoadResource(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                /*
                webBinding.web.evaluateJavascript("(function(){" +
                        "var obj=document.getElementsByTagName('img');" +
                        "var lst=[];" +
                        "for(let i=0;i<obj.length;i++){" +
                        "lst.push(obj[i].currentSrc);" +
                        "}" +
                        "return lst.join(',');" +
                        "})();",value -> {
                    String[] img=value.split(",");
                    List<String> imgList= Arrays.stream(img).filter(e->e.contains("chapter")).collect(Collectors.toList());

                    if(imgList.size()>0 && url.contains("all-pages")){
                        Log.d("hello","Incomming: "+Integer.toString(imgList.size()));
                            repo.addViewImageList(imgList,imageViewModel.getPageSrc());
                            imageViewModel.setShowImageView(true);

                    }
                });*/
            }
        });





        loadUrl();
        return webBinding.getRoot();
    }
    private void loadUrl(){
        webBinding.web.loadUrl(webViewModel.getUrlAddress().getValue());

    }

    public boolean onBackPressed() {
        if (webBinding.web.canGoBack()) {
            webBinding.web.goBack();
            return true;
        } else {
            return false;
        }
    }
    public void onForward() {
        if (webBinding.web.canGoForward()) {

            webBinding.web.goForward();
        }
    }

    @Override
    public void onDestroy() {
        webViewModel.setWebRequest(false);
        super.onDestroy();
    }




}