package com.LG.mreader.PageActivity;

import android.content.Context;
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
import com.LG.mreader.DataModel.History;
import com.LG.mreader.DataModel.ViewImageDataModel;
import com.LG.mreader.Middleware.WebviewRepoMiddleware;
import com.LG.mreader.R;
import com.LG.mreader.Utility.ContextManager;
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
        webBinding = FragmentWebBinding.inflate(inflater, container, false);
        webViewModel = new ViewModelProvider(requireActivity()).get(WebViewModel.class);
        imageViewModel = new ViewModelProvider(requireActivity()).get(ImageViewModel.class);
        repo = new AppRepository(requireActivity());


        webBinding.web.getSettings().setJavaScriptEnabled(true);

        webviewRepoMiddleware = new WebviewRepoMiddleware(imageViewModel);
        ContextManager.getInstance().setWebFragmentContext(requireActivity());
        webBinding.web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (url != webViewModel.getUrlAddress().getValue()) {
                    webViewModel.setUrlAddress(url);
                }

                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                // Removed evaluateJavascript from here to prevent multiple calls
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                repo.insertHistory(new History(url));

                // Execute JavaScript once after page is fully loaded
                webBinding.web.evaluateJavascript(
                        "(function(){"
                                + "var obj=document.getElementById('chapter-reader');"
                                + "var homepage=document.querySelector('a.logo, a[href=\"/\"]') ? document.querySelector('a.logo, a[href=\"/\"]').href : '';"
                                + "var title=document.querySelector('title') ? document.querySelector('title').innerText : (document.querySelector('h1') ? document.querySelector('h1').innerText : '');"
                                + "var nextPage=document.querySelector('a.next, a[rel=\"next\"]') ? document.querySelector('a.next, a[rel=\"next\"]').href : '';"
                                + "var prevPage=document.querySelector('a.prev, a[rel=\"prev\"]') ? document.querySelector('a.prev, a[rel=\"prev\"]').href : '';"

                                // Collect all image sources inside chapter-reader
                                + "var img=obj ? document.getElementsByTagName('img') : [];"
                                +  "var pageSource=img[0].baseURI;"
                                + "var lst=[];"
                                + "for(var i=0;i<img.length;i++){ lst.push(img[i].currentSrc || img[i].src); }"

                                // Join images
                                + "var imgSrc=lst.join(',');"

                                // Combine final data
                                + "var data= homepage+'~#'+pageSource+'~#'+title+'~#'+nextPage+'~#'+prevPage+'~#'+imgSrc;"
                                + "return data;"
                                + "})();",
                        value -> {
                            //Log.d("hello", "data: " + value);

                            if (value != null && value.length() > 0) {
                                // Log.d("hello", value);
                                webviewRepoMiddleware.addviewImageList(value, webViewModel.getUrlAddress().getValue());
                            }
                        }
                );
            }
        });


        loadUrl();
        return webBinding.getRoot();
    }

    private void loadUrl() {
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