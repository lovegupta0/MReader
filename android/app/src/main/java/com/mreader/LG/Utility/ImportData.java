package com.mreader.LG.Utility;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.provider.MediaStore;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class ImportData {
    private static final String EXPORT_DIRECTORY = Environment.DIRECTORY_DOWNLOADS + "/MReader";
    private static final String LIBRARY_FILENAME = "ExportedLibraryDataV1.dat";
    private static final String BOOKMARKS_FILENAME = "ExportedBookmarksDataV1.dat";

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
        String libraryJson = readImportFile(LIBRARY_FILENAME);
        String bookmarksJson = readImportFile(BOOKMARKS_FILENAME);

        if (libraryJson != null && !libraryJson.isEmpty()) {
            List<LibraryDataModel> data = JsonConverter.jsonToList(libraryJson, LibraryDataModel.class);
            libraryService.insertLibrary(data);
        }

        if (bookmarksJson != null && !bookmarksJson.isEmpty()) {
            List<BookmarkDataModel> data = JsonConverter.jsonToList(bookmarksJson, BookmarkDataModel.class);
            bookmarkService.insertBookmark(data);
        }
    }

    private void showImportProgressDialog() {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.dialog_progress, null);
        TextView textView=view.findViewById(R.id.status_text);
        textView.setText("Importing...");
        progressDialog = new MaterialAlertDialogBuilder(context)
                .setView(view)
                .setCancelable(false)
                .show();
    }
    public void showImportBottomSheet(Runnable onChooseFiles) {
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
                    importDialog(onChooseFiles);

                });


        dialog.setContentView(view);
        dialog.show();
    }

    private void importDialog(Runnable onChooseFiles) {
        new MaterialAlertDialogBuilder(context)
                .setTitle("Import Data")
                .setMessage("Choose the backup files to import:\n" +
                        LIBRARY_FILENAME + "\n" + BOOKMARKS_FILENAME)
                .setPositiveButton("Ok", (dialog, which) -> {
                    dialog.dismiss();
                    if (onChooseFiles != null) {
                        onChooseFiles.run();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void  startImportAsync(){
        Future<Void> future=threadsPoolManager.submitTask(()-> {
            try {
                // ⏳ Simulate heavy export work
                Log.d(TAG,"Importing started through startImportAsync...");
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

    public void importSelectedFiles(List<Uri> selectedUris) {
        if (selectedUris == null || selectedUris.isEmpty()) {
            showExportFailedDialog("No backup files were selected.");
            return;
        }

        Map<String, Uri> fileMap = new HashMap<>();
        for (Uri uri : selectedUris) {
            String displayName = getDisplayName(uri);
            if (displayName != null) {
                fileMap.put(displayName, uri);
            }
        }

        if (!fileMap.containsKey(LIBRARY_FILENAME) && !fileMap.containsKey(BOOKMARKS_FILENAME)) {
            showExportFailedDialog("Select at least one valid MReader backup file.");
            return;
        }


        Future<Void> future=threadsPoolManager.submitTask(() -> {
            try {
                importFromSelectedUris(fileMap);
                Thread.sleep(2000);

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
            return null;
        });
        showImportProgressDialog();
    }

    private String readImportFile(String fileName) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            String contents = readImportFileScoped(fileName);
            if (contents != null) {
                return contents;
            }
        }

        String legacyDirectory = settingStorage.getDownload().getDirectory();
        File targetFile = new File(legacyDirectory, fileName);
        if (!targetFile.exists() || !targetFile.isFile()) {
            return null;
        }

        return readAll(new BufferedReader(new FileReader(targetFile)));
    }

    private String readImportFileScoped(String fileName) throws IOException {
        Uri uri = findScopedImportUri(fileName);
        if (uri == null) {
            return null;
        }

        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            if (inputStream == null) {
                throw new IOException("Unable to open import stream for " + fileName);
            }
            return readAll(reader);
        }
    }

    private void importFromSelectedUris(Map<String, Uri> selectedFiles) throws IOException {
        Uri libraryUri = selectedFiles.get(LIBRARY_FILENAME);

        if (libraryUri != null) {
            String libraryJson = readUriContents(libraryUri);

            if (libraryJson != null && !libraryJson.isEmpty()) {
                List<LibraryDataModel> data = JsonConverter.jsonToList(libraryJson, LibraryDataModel.class);
                libraryService.insertLibrary(data);
            }
        }


        Uri bookmarksUri = selectedFiles.get(BOOKMARKS_FILENAME);
        if (bookmarksUri != null) {
            String bookmarksJson = readUriContents(bookmarksUri);
            if (bookmarksJson != null && !bookmarksJson.isEmpty()) {
                List<BookmarkDataModel> data = JsonConverter.jsonToList(bookmarksJson, BookmarkDataModel.class);
                bookmarkService.insertBookmark(data);
            }
        }
    }

    private String readUriContents(Uri uri) throws IOException {
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            if (inputStream == null) {
                throw new IOException("Unable to open selected import file.");
            }
            return readAll(reader);
        }
    }

    private String getDisplayName(Uri uri) {
        try (Cursor cursor = context.getContentResolver().query(
                uri,
                new String[]{OpenableColumns.DISPLAY_NAME},
                null,
                null,
                null
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index >= 0) {
                    return cursor.getString(index);
                }
            }
        }

        return null;
    }

    private Uri findScopedImportUri(String fileName) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return null;
        }

        String[] projection = new String[]{MediaStore.MediaColumns._ID};
        String selection = MediaStore.MediaColumns.DISPLAY_NAME + "=? AND " +
                MediaStore.MediaColumns.RELATIVE_PATH + "=?";
        String[] args = new String[]{fileName, EXPORT_DIRECTORY + "/"};

        try (Cursor cursor = context.getContentResolver().query(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                args,
                null
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
                return Uri.withAppendedPath(MediaStore.Downloads.EXTERNAL_CONTENT_URI, String.valueOf(id));
            }
        }

        return null;
    }

    private String readAll(BufferedReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }


}
