package com.mreader.LG.PageActivity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.mreader.LG.DataModel.BookmarkDataModel;
import com.mreader.LG.DataModel.LibraryDataModel;
import com.mreader.LG.Middleware.HistoryService;
import com.mreader.LG.Middleware.WebviewRepoMiddleware;
import com.mreader.LG.Service.BookmarkService;
import com.mreader.LG.Service.LibraryService;
import com.mreader.LG.Utility.ContextManager;
import com.mreader.LG.ViewModel.BookmarksViewModel;
import com.mreader.LG.ViewModel.ImageViewModel;
import com.mreader.LG.ViewModel.WebViewModel;
import com.mreader.R;
import com.mreader.databinding.FragmentWebBinding;

import java.util.List;


public class WebFragment extends Fragment {
    private FragmentWebBinding webBinding;
    private WebViewModel webViewModel;
    private WebviewRepoMiddleware webviewRepoMiddleware;
    private ImageViewModel imageViewModel;
    private LibraryService libraryService;
    private HistoryService historyMiddleware;
    private BookmarksViewModel bookmarksViewModel;
    private BookmarkService bookmarkService;
    private View popupView;
    private String TAG="WebFragment";
    private Handler popupHandler = new Handler(Looper.getMainLooper());
    private float startY;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        webBinding = FragmentWebBinding.inflate(inflater, container, false);
        webViewModel = new ViewModelProvider(requireActivity()).get(WebViewModel.class);
        imageViewModel = new ViewModelProvider(requireActivity()).get(ImageViewModel.class);

        historyMiddleware= HistoryService.getInstance();
        bookmarksViewModel=new ViewModelProvider(requireActivity()).get(BookmarksViewModel.class);
        bookmarkService=new BookmarkService();
        libraryService=LibraryService.getInstance();
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
                submitHistory(url);
                if(libraryService.isExist(url)){
                    showContinuePopup();
                };

                bookmarksViewModel.getAddBookmark().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        if(aBoolean){
                            BookmarkDataModel data=new BookmarkDataModel();
                            data.setAddress(url);
                            data.setTitle(url);
                            bookmarkService.insertBookmark(data);
                            bookmarksViewModel.setAddBookmark(false);
                        }

                    }
                });
                // Execute JavaScript once after page is fully loaded
                webBinding.web.evaluateJavascript(
                        "(function(){"
                                + "var obj=document.getElementById('chapter-reader');"
                                + "const aTag = document.querySelector('.titles h1 a');"
                                + "var homepage=document.querySelector('a.logo, a[href=\"/\"]') ? document.querySelector('a.logo, a[href=\"/\"]').href : '';"
                                + "var title=aTag.textContent.trim();"
                                + "var nextPage=document.querySelector('a.next, a[rel=\"next\"]') ? document.querySelector('a.next, a[rel=\"next\"]').href : '';"
                                + "var prevPage=document.querySelector('a.prev, a[rel=\"prev\"]') ? document.querySelector('a.prev, a[rel=\"prev\"]').href : '';"

                                // Collect all image sources inside chapter-reader
                                + "var img=obj ? document.getElementsByTagName('img') : [];"
                                +  "var pageSource=aTag.href;"
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
        webViewModel.getReload().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    reload();

                }
            }
        });

        return webBinding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ✅ SAFE
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
    private void submitHistory(String url) {
        Thread t = new Thread(()->historyMiddleware.addHistory(url));
        t.start();
    }

    @Override
    public void onDestroy() {
        popupHandler.removeCallbacksAndMessages(null);
        popupView = null;
        webViewModel.setWebRequest(false);
        super.onDestroy();
    }



    private void showContinuePopup() {
        if (popupView != null || !isAdded()) return;

        Activity activity = getActivity();
        if (activity == null) return;

        FrameLayout container = activity.findViewById(R.id.popup_container);
        if (container == null) return;

        popupView = LayoutInflater.from(activity)
                .inflate(R.layout.view_continue_last_read, container, false);

        popupView.startAnimation(
                AnimationUtils.loadAnimation(activity, R.anim.slide_up));

        popupView.findViewById(R.id.btn_ok)
                .setOnClickListener(v -> loadContinueReadPage(activity));

        container.addView(popupView);

        popupHandler.postDelayed(this::removePopup, 5000);
    }

    private void removePopup() {
        if (popupView == null) return;

        popupHandler.removeCallbacksAndMessages(null);

        Context ctx = getContext();
        if (ctx == null) {
            // Fragment already detached — just clean reference
            popupView = null;
            return;
        }

        Animation slideDown =
                AnimationUtils.loadAnimation(ctx, R.anim.slide_down);

        slideDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                if (popupView != null && popupView.getParent() instanceof ViewGroup) {
                    ((ViewGroup) popupView.getParent()).removeView(popupView);
                }
                popupView = null;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        popupView.startAnimation(slideDown);
    }



    private void loadContinueReadPage(Activity activity){
        String url=webViewModel.getUrlAddress().getValue();
        List<LibraryDataModel> lst=libraryService.getLibraryByUrl(url);
        if(!lst.isEmpty()){
            LibraryDataModel data=lst.get(0);
            webViewModel.setUrlAddress(data.getChapterUrl());
            loadUrl();

        }
    }

    private void reload(){
        webBinding.web.reload();
    }



}