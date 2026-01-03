package com.mreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mreader.LG.AppRepository.AppRepository;
import com.mreader.LG.Common.SettingStorage;
import com.mreader.LG.InitialOTO.UploadDefaultConfiguration;
import com.mreader.LG.PageActivity.HomeFragment;
import com.mreader.LG.PageActivity.ImageActivity;
import com.mreader.LG.PageActivity.MenuFragement;
import com.mreader.LG.PageActivity.WebFragment;
import com.mreader.LG.Utility.ContextManager;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(main.getRoot());
        runTimePermission();
        repo=AppRepository.getInstance(this);
        ContextManager.getInstance().setApplicationMainContext(this);
        imageActivity=new Intent(this, ImageActivity.class);
        manger= getSupportFragmentManager();
        menuViewModel=MenuViewModel.getInstance();
        defaultConfiguration=new UploadDefaultConfiguration();
        settingStorage=SettingStorage.getInstance();
        Intent intent=getIntent();
        if(!settingStorage.verify()){
            defaultConfiguration.restore();
        }
        if(savedInstanceState==null){
            manger.beginTransaction().replace(R.id.frame,new HomeFragment()).commit();
        }


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
                    changeFragment(new WebFragment());
                }
                else{
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
                changeFragment(new HomeFragment());
            }
        });

    }

    private void changeFragment(Fragment fragment){
        manger.beginTransaction().replace(R.id.frame,fragment).commit();
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
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE

                ).withListener(new MultiplePermissionsListener() {
                                   @Override
                                   public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                       if(multiplePermissionsReport.areAllPermissionsGranted()){
                                           Toast.makeText(MainActivity.this, "All Permission Granted", Toast.LENGTH_SHORT).show();
                                       }
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
        // If menu is visible, hide it instead of going back
        if (isMenuVisible) {
            hideMenu();
            return;
        }

        WebFragment web=(WebFragment) manger.findFragmentById(R.id.frame);
        if(web!=null && web.onBackPressed()){
            return;
        }
        else if (web!=null && web.onBackPressed()==false) {
            changeFragment(new HomeFragment());
            return;
        }
        super.onBackPressed();
    }


}