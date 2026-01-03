package com.mreader.LG.Middleware;

import com.mreader.LG.AppRepository.AppRepository;
import com.mreader.LG.DataModel.History;
import com.mreader.LG.Utility.ContextManager;

import java.util.List;

public class HistoryService {

    private static HistoryService instance;
    private AppRepository repo;

    private HistoryService(){
        repo = AppRepository.getInstance(ContextManager.getInstance().getApplicationMainContext());
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
}


