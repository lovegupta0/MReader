package com.LG.mreader;

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

import com.LG.mreader.AppRepository.AppRepository;
import com.LG.mreader.DataModel.BookmarkDataModel;
import com.LG.mreader.DataModel.ImageDataModel;
import com.LG.mreader.DataModel.ViewImageDataModel;
import com.LG.mreader.PageActivity.HomeFragment;
import com.LG.mreader.PageActivity.ImageActivity;
import com.LG.mreader.PageActivity.MenuFragement;
import com.LG.mreader.PageActivity.WebFragment;
import com.LG.mreader.ViewModel.ImageViewModel;
import com.LG.mreader.ViewModel.WebViewModel;
import com.LG.mreader.databinding.ActivityMainBinding;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(main.getRoot());
        runTimePermission();
        repo=new AppRepository(this);

        imageActivity=new Intent(this, ImageActivity.class);
        manger= getSupportFragmentManager();
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
                Fragment fragment=manger.findFragmentById(R.id.frame);
                //MenuFragement menuFragement=(MenuFragement) manger.findFragmentById(R.id.menu);
                if(fragment instanceof MenuFragement){
                    changeFragment(new HomeFragment());
                }
                else{
                    changeFragment(new MenuFragement());
                }

            }
        });

    }

    private void changeFragment(Fragment fragment){
        manger.beginTransaction().replace(R.id.frame,fragment).commit();
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