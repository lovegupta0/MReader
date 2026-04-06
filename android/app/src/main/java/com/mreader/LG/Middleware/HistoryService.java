package com.mreader.LG.Middleware;

import android.content.Context;

import com.mreader.LG.AppRepository.AppRepository;
import com.mreader.LG.DataModel.History;
import com.mreader.LG.Utility.ContextManager;

import java.util.List;

public class HistoryService {

    private static HistoryService instance;
    private AppRepository repo;

    private HistoryService(){
        this(ContextManager.getInstance().getApplicationMainContext());
    }

    private HistoryService(Context context){
        repo = AppRepository.getInstance(context);
    }

    public static HistoryService getInstance(){
        if(instance == null){
            synchronized (HistoryService.class){
                if(instance == null){
                    instance = new HistoryService();
                }
            }
        }
        return instance;
    }

    public static HistoryService getInstance(Context context){
        if(instance == null){
            synchronized (HistoryService.class){
                if(instance == null){
                    instance = new HistoryService(context);
                }
            }
        }
        return instance;
    }
    private void addHistoryToDatabase(History history){
        repo.insertHistory(history);
    }


    public void addHistory(History history){
        addHistoryToDatabase(history);
    }
    public void addHistory(String url){
        addHistoryToDatabase(new History(url));
    }

    public List<History> getAllHistory(){
        return repo.getAllHistory();
    }

    public void deleteHistory(History history){
        repo.deleteHistory(history);
    }
    public void deleteAllHistory(){
        repo.clearHistory();
    }
    public List<History> getAllHistoryDesc(){
        return repo.getAllHistoryDesc();
    }
}


