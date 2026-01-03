package com.mreader.LG.Utility;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;

public class ImportData {

    private SettingStorage settingStorage;
    private LibraryService libraryService;
    private BookmarkService bookmarkService;
    private String TAG="ImportData";
    private Context context;
    private AlertDialog progressDialog;
    private ThreadsPoolManager threadsPoolManager;
    public ImportData(Context context){
        settingStorage=SettingStorage.getInstance();
        libraryService=LibraryService.getInstance();
        bookmarkService=new BookmarkService();
        this.context=context;
        threadsPoolManager= CentralThreadPool.getInstance();
    }

    private void showExportCompletedDialog() {
        new MaterialAlertDialogBuilder(context)
                .setTitle("Import Completed")
                .setMessage("Your data has been imported successfully.")
                .setPositiveButton("OK", null)
                .show();
    }
    private void showExportFailedDialog(String error) {
        new MaterialAlertDialogBuilder(context)
                .setTitle("Import Failed")
                .setMessage(error == null ? "Something went wrong." : error)
                .setPositiveButton("OK", null)
                .show();
    }
    private void importData() throws IOException {
        String Librarypath=settingStorage.getDownload().getDirectory() + "/ExportedLibraryDataV1.dat";
        String Bookmarkspath=settingStorage.getDownload().getDirectory() + "/ExportedBookmarksDataV1.dat";

        File lib=new File(Librarypath);
        File book=new File(Bookmarkspath);
        if(lib.exists() && lib.isFile()){
            BufferedReader reader=new BufferedReader(new FileReader(lib));
            StringBuilder sb=new StringBuilder();
            String line;
            while((line=reader.readLine())!=null) {
                sb.append(line);
            }
            reader.close();
            List<LibraryDataModel> data=JsonConverter.jsonToList(sb.toString(),LibraryDataModel.class);
            libraryService.insertLibrary(data);
        }

        if(book.exists() && book.isFile()){
            BufferedReader reader=new BufferedReader(new FileReader(book));
            StringBuilder sb=new StringBuilder();
            String line;
            while((line=reader.readLine())!=null) {
                sb.append(line);
            }
            reader.close();
            List<BookmarkDataModel> data=JsonConverter.jsonToList(sb.toString(), BookmarkDataModel.class);
            bookmarkService.insertBookmark(data);
        }

    }

    private void showImportProgressDialog() {
        startImportAsync();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.dialog_progress, null);
        TextView textView=view.findViewById(R.id.status_text);
        textView.setText("Importing...");
        progressDialog = new MaterialAlertDialogBuilder(context)
                .setView(view)
                .setCancelable(false)
                .show();
    }
    public void showImportBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_export_confirm, null);

        view.findViewById(R.id.btn_cancel)
                .setOnClickListener(v -> dialog.dismiss());
        Button btnImport = view.findViewById(R.id.btn_export);
        btnImport.setText("Import Data");

        TextView textView=view.findViewById(R.id.text_title);
        textView.setText("Import Data?");

        view.findViewById(R.id.btn_export)
                .setOnClickListener(v -> {
                    dialog.dismiss();
                    importDialog();

                });


        dialog.setContentView(view);
        dialog.show();
    }

    private void importDialog() {
        new MaterialAlertDialogBuilder(context)
                .setTitle("Import Data")
                .setMessage("Make Sure you have Data to be Imported Placed in "+settingStorage.getDownload().getDirectory()+" with Name:\nExportedLibraryDataV1.dat\nExportedBookmarksDataV1.dat")
                .setPositiveButton("Ok", (dialog, which) -> {
                    dialog.dismiss();
                    showImportProgressDialog();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void  startImportAsync(){
        Future<Void> future=threadsPoolManager.submitTask(()-> {
            try {
                // ⏳ Simulate heavy export work
               importData();// <-- your real export logic
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


}
