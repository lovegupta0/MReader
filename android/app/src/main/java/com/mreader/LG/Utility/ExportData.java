package com.mreader.LG.Utility;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mreader.LG.Common.SettingStorage;
import com.mreader.LG.DataModel.BookmarkDataModel;
import com.mreader.LG.DataModel.LibraryDataModel;
import com.mreader.LG.PoolService.CentralThreadPool;
import com.mreader.LG.Service.BookmarkService;
import com.mreader.LG.Service.LibraryService;
import com.mreader.R;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;

public class ExportData {
    private SettingStorage settingStorage;
    private LibraryService libraryService;
    private BookmarkService bookmarkService;
    private String TAG="ExportData";
    private Context context;
    private AlertDialog progressDialog;
    private ThreadsPoolManager threadsPoolManager;

    public ExportData(Context context){
        settingStorage=SettingStorage.getInstance();
        libraryService=LibraryService.getInstance();
        bookmarkService=new BookmarkService();
        this.context=context;
        threadsPoolManager= CentralThreadPool.getInstance();
    }
    public void showExportBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_export_confirm, null);

        view.findViewById(R.id.btn_cancel)
                .setOnClickListener(v -> dialog.dismiss());

        view.findViewById(R.id.btn_export)
                .setOnClickListener(v -> {
                    dialog.dismiss();
                    showExportProgressDialog();
                    startExportAsync();
                });

        dialog.setContentView(view);
        dialog.show();
    }

    private void showExportProgressDialog() {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.dialog_progress, null);

        progressDialog = new MaterialAlertDialogBuilder(context)
                .setView(view)
                .setCancelable(false)
                .show();
    }

    private void  startExportAsync(){
        Future<Void> future=threadsPoolManager.submitTask(()-> {
            try {
                // ⏳ Simulate heavy export work
                export(); // <-- your real export logic
                Thread.sleep(2000);

                // ✅ On success
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    showExportCompletedDialog();
                });

            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    showExportFailedDialog(e.getMessage());
                });
            }

        });

    }
    private void showExportCompletedDialog() {
        new MaterialAlertDialogBuilder(context)
                .setTitle("Export Completed")
                .setMessage("Your data has been exported successfully.")
                .setPositiveButton("OK", null)
                .show();
    }
    private void showExportFailedDialog(String error) {
        new MaterialAlertDialogBuilder(context)
                .setTitle("Export Failed")
                .setMessage(error == null ? "Something went wrong." : error)
                .setPositiveButton("OK", null)
                .show();
    }


    public void ExportDialog() {
        new MaterialAlertDialogBuilder(context)
                .setTitle("Export Data")
                .setMessage("Are you sure you want to export data?")
                .setPositiveButton("Export", (dialog, which) -> {
                    dialog.dismiss();
                    showExportProgressDialog();
                    startExportAsync();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void export(){


        String Librarypath=settingStorage.getDownload().getDirectory() + "/ExportedLibraryDataV1.dat";
        String Bookmarkspath=settingStorage.getDownload().getDirectory() + "/ExportedBookmarksDataV1.dat";
        File file=new File(settingStorage.getDownload().getDirectory());
        if(!file.exists()){
            file.mkdirs();
        }
        try(FileWriter writer=new FileWriter(Librarypath)){
            List<LibraryDataModel> libraryData=libraryService.getLibrary();
            writer.write(JsonConverter.listToJsonSafe(libraryData));
            Log.d(TAG,JsonConverter.listToJsonSafe(libraryData));
            writer.close();

        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        try(FileWriter writer=new FileWriter(Bookmarkspath)){
            List<BookmarkDataModel> bookmarkData=bookmarkService.getBookmarks();
            writer.write(JsonConverter.listToJsonSafe(bookmarkData));
            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
