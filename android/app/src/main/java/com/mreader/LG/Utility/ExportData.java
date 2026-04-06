package com.mreader.LG.Utility;

import android.content.Context;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Future;

public class ExportData {
    private static final String EXPORT_DIRECTORY = Environment.DIRECTORY_DOWNLOADS + "/MReader";
    private static final String LIBRARY_FILENAME = "ExportedLibraryDataV1.dat";
    private static final String BOOKMARKS_FILENAME = "ExportedBookmarksDataV1.dat";
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
                .setMessage("Your data has been exported successfully to Downloads/MReader.")
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
        try {
            List<LibraryDataModel> libraryData = libraryService.getLibrary();
            String libraryJson = JsonConverter.listToJsonSafe(libraryData);
            Log.d(TAG, libraryJson);
            writeExportFile(LIBRARY_FILENAME, libraryJson);

            List<BookmarkDataModel> bookmarkData = bookmarkService.getBookmarks();
            String bookmarksJson = JsonConverter.listToJsonSafe(bookmarkData);
            writeExportFile(BOOKMARKS_FILENAME, bookmarksJson);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeExportFile(String fileName, String contents) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            writeExportFileScoped(fileName, contents);
            return;
        }

        String exportDirectory = settingStorage.getDownload().getDirectory();
        File directory = new File(exportDirectory);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Unable to create export directory: " + exportDirectory);
        }

        File outFile = new File(directory, fileName);
        try (FileWriter writer = new FileWriter(outFile, false)) {
            writer.write(contents);
        }
    }

    private void writeExportFileScoped(String fileName, String contents) throws IOException {
        deleteExistingScopedFile(fileName);

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "application/octet-stream");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, EXPORT_DIRECTORY);
        values.put(MediaStore.MediaColumns.IS_PENDING, 1);

        Uri uri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
        if (uri == null) {
            throw new IOException("Unable to create export file in Downloads/MReader");
        }

        try (OutputStream outputStream = context.getContentResolver().openOutputStream(uri, "w");
             OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
            if (outputStream == null) {
                throw new IOException("Unable to open export stream for " + fileName);
            }
            writer.write(contents);
            writer.flush();
        }

        ContentValues completed = new ContentValues();
        completed.put(MediaStore.MediaColumns.IS_PENDING, 0);
        context.getContentResolver().update(uri, completed, null, null);
    }

    private void deleteExistingScopedFile(String fileName) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return;
        }

        String selection = MediaStore.MediaColumns.DISPLAY_NAME + "=? AND " +
                MediaStore.MediaColumns.RELATIVE_PATH + "=?";
        String[] args = new String[]{fileName, EXPORT_DIRECTORY + "/"};
        context.getContentResolver().delete(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                selection,
                args
        );
    }
}
