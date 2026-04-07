package com.mreader;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mreader.LG.AppRepository.AppRepository;
import com.mreader.LG.Common.SettingStorage;
import com.mreader.LG.InitialOTO.UploadDefaultConfiguration;
import com.mreader.LG.PageActivity.HomeFragment;
import com.mreader.LG.PageActivity.ImageActivity;
import com.mreader.LG.PageActivity.MenuFragement;
import com.mreader.LG.PageActivity.WebFragment;
import com.mreader.LG.Utility.ContextManager;
import com.mreader.LG.Utility.LibraryCheckForUpdate;
import com.mreader.LG.ViewModel.ImageViewModel;
import com.mreader.LG.ViewModel.MenuViewModel;
import com.mreader.LG.ViewModel.WebViewModel;
import com.mreader.databinding.ActivityMainBinding;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding main;
    private WebViewModel webViewModel;
    private FragmentManager manger;
    private ImageViewModel imgViewModel;
    private AppRepository repo;
    private Intent imageActivity;
    private MenuViewModel menuViewModel;
    private boolean isMenuVisible = false;
    private SettingStorage settingStorage;
    private UploadDefaultConfiguration defaultConfiguration;
    private String TAG="MainActivity";
    private LibraryCheckForUpdate libraryCheckForUpdate;
    private boolean isSearchBarVisible = true;
    private ValueAnimator searchBarAnimator;
    private int searchBarExpandedHeight = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(main.getRoot());
        setupWindowInsets();

        repo=AppRepository.getInstance(this);
        ContextManager.getInstance().setApplicationMainContext(this);
        imageActivity=new Intent(this, ImageActivity.class);

        manger= getSupportFragmentManager();
        defaultConfiguration=new UploadDefaultConfiguration();
        settingStorage=SettingStorage.getInstance();
        Intent intent=getIntent();
        if(!settingStorage.verify()){
            defaultConfiguration.restore();
        }
        if(savedInstanceState==null){
            manger.beginTransaction().replace(R.id.frame,new HomeFragment()).commit();
        }
        init();
        webViewModel=new ViewModelProvider(this).get(WebViewModel.class);
        imgViewModel =new ViewModelProvider(this).get(ImageViewModel.class);
        main.goUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webViewModel.setUrlAddress(main.searchURL.getText().toString());
                webViewModel.setWebRequest(true);
            }
        });

        webViewModel.getWebRequest().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean req) {
                if(req){
                    setSearchBarVisible(true, false);
                    changeFragment(new WebFragment());
                }
                else{
                    setSearchBarVisible(true, false);
                    changeFragment(new HomeFragment());
                }
            }
        });
        imgViewModel.getShowImageView().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    startActivity(imageActivity);
                }
            }
        });
        webViewModel.getUrlAddress().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(!main.searchURL.getText().toString().equals(s)){
                    main.searchURL.setText(s);
                }
            }
        });
        if(intent.hasExtra("openWeb") && intent.getBooleanExtra("openWeb",false) && intent.hasExtra("url")){
            webViewModel.setUrlAddress(intent.getStringExtra("url"));
            webViewModel.setWebRequest(true);
        }


        main.prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        main.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebFragment web=(WebFragment) manger.findFragmentById(R.id.frame);
                if(web!=null && webViewModel.getWebRequest().getValue()){
                    web.onForward();
                }
                else {
                    return;
                }
            }
        });
        main.menu.setOnClickListener(new  View.OnClickListener(){
            @Override
            public void onClick(View v) {
                menuViewModel.toggleMenu();
            }
        });
        menuViewModel.isMenuOpen().observe(this,new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    showMenu();
                }
                else{
                    hideMenu();
                }
            }
        });
        main.home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webViewModel.setUrlAddress("");
                setSearchBarVisible(true, false);
                changeFragment(new HomeFragment());
            }
        });

    }

    private void changeFragment(Fragment fragment){
        manger.beginTransaction().replace(R.id.frame,fragment).commit();
    }

    public void setSearchBarVisible(boolean visible, boolean animated) {
        if (main == null) {
            return;
        }

        View container = main.searchBarContainer;
        container.post(() -> {
            if (searchBarExpandedHeight < 0) {
                searchBarExpandedHeight = container.getHeight();
                if (searchBarExpandedHeight <= 0) {
                    container.measure(
                            View.MeasureSpec.makeMeasureSpec(main.linearLayout.getWidth(), View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    );
                    searchBarExpandedHeight = container.getMeasuredHeight();
                }
            }

            if (searchBarExpandedHeight <= 0) {
                return;
            }
            if (visible == isSearchBarVisible && searchBarAnimator == null) {
                return;
            }

            if (searchBarAnimator != null) {
                searchBarAnimator.cancel();
            }

            int currentHeight = container.getLayoutParams().height;
            if (currentHeight <= 0 || currentHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
                currentHeight = container.getHeight();
            }
            if (currentHeight <= 0) {
                currentHeight = visible ? 0 : searchBarExpandedHeight;
            }

            int targetHeight = visible ? searchBarExpandedHeight : 0;
            if (!animated) {
                applySearchBarState(container, targetHeight);
                isSearchBarVisible = visible;
                return;
            }

            searchBarAnimator = ValueAnimator.ofInt(currentHeight, targetHeight);
            searchBarAnimator.setDuration(180);
            searchBarAnimator.addUpdateListener(animation ->
                    applySearchBarState(container, (int) animation.getAnimatedValue()));
            searchBarAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    isSearchBarVisible = visible;
                    searchBarAnimator = null;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    searchBarAnimator = null;
                }
            });
            searchBarAnimator.start();
        });
    }

    private void applySearchBarState(View container, int height) {
        ViewGroup.LayoutParams params = container.getLayoutParams();
        params.height = height;
        container.setLayoutParams(params);
        if (searchBarExpandedHeight > 0) {
            container.setAlpha((float) height / searchBarExpandedHeight);
        }
    }

    private void showMenu() {
        isMenuVisible = true;
        main.menuContainer.setVisibility(View.VISIBLE);
        manger.beginTransaction()
                .add(R.id.menu_container, new MenuFragement(), "menu_tag")
                .addToBackStack(null)
                .commit();
    }

    private void hideMenu() {
        isMenuVisible = false;
        main.menuContainer.setVisibility(View.GONE);
        Fragment menuFragment = manger.findFragmentByTag("menu_tag");
        if (menuFragment != null) {
            manger.beginTransaction()
                    .remove(menuFragment)
                    .commit();
        }
        manger.popBackStack();
    }

    private void runTimePermission(){
        ArrayList<String> permissions = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES);
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }

        Dexter.withContext(this)
                .withPermissions(permissions)
                .withListener(new MultiplePermissionsListener() {
                                   @Override
                                   public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                                   }

                                   @Override
                                   public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                       permissionToken.continuePermissionRequest();

                                   }
                               }

                ).check();
    }


    @Override
    public void onBackPressed() {
        if (isMenuVisible) {
            hideMenu();
            return;
        }

        WebFragment web = (WebFragment) manger.findFragmentById(R.id.frame);
        if (web != null) {
            boolean handled = web.onBackPressed();
            if (handled) {
                return;
            }

            changeFragment(new HomeFragment());
            return;
        }

        super.onBackPressed();
    }

    private void init(){
        runTimePermission();
        menuViewModel=MenuViewModel.getInstance();
        libraryCheckForUpdate=new LibraryCheckForUpdate();
        libraryCheckForUpdate.updateOnStart();
    }

    private void setupWindowInsets() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat insetsController =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insetsController.setAppearanceLightStatusBars(true);

        final int rootStart = main.linearLayout.getPaddingStart();
        final int rootTop = main.linearLayout.getPaddingTop();
        final int rootEnd = main.linearLayout.getPaddingEnd();
        final int rootBottom = main.linearLayout.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(main.linearLayout, (view, windowInsets) -> {
            Insets systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(
                    rootStart,
                    rootTop + systemBars.top,
                    rootEnd,
                    rootBottom + systemBars.bottom
            );
            return WindowInsetsCompat.CONSUMED;
        });

        ViewCompat.requestApplyInsets(main.linearLayout);
    }


}
