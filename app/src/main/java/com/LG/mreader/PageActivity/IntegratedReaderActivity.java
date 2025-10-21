package com.LG.mreader.PageActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.LG.mreader.GPT.Chapter;
import com.LG.mreader.GPT.LongStripReaderFragment;
import com.LG.mreader.GPT.Page;
import com.LG.mreader.GPT.PagedReaderFragment;
import com.LG.mreader.R;

import java.io.Serializable;
import java.util.ArrayList;

public class IntegratedReaderActivity extends AppCompatActivity {
    private PagedReaderFragment pagedFragment;
    private LongStripReaderFragment longStripFragment;
    //private Button toggleBtn;
    private boolean isPaged = false;
    private Chapter chapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d("hello","intent trigger-1");
        setContentView(R.layout.activity_integrated_reader);


        Log.d("hello","intent trigger");
        // 1️⃣ Receive the Chapter object from Intent
        this.chapter = getIntent().getParcelableExtra("chapter");

        longStripFragment = new LongStripReaderFragment();

        // 3️⃣ Start in Paged mode by default
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.reader_container, longStripFragment)
                .commit();

        // 4️⃣ Load pages into fragments
        passChapterToFragments();

        // 5️⃣ Setup toggle

    }

    private void passChapterToFragments() {
        if (chapter == null || chapter.pages == null) return;
        ArrayList<Page> list = new ArrayList<>(chapter.pages);
        pagedFragment.setPages(list);
        longStripFragment.setPages(list);
    }
}
