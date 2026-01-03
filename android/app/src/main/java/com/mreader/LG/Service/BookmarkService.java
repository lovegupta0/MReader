package com.mreader.LG.Service;

import androidx.lifecycle.LiveData;

import com.mreader.LG.AppRepository.AppRepository;
import com.mreader.LG.DataModel.BookmarkDataModel;

import java.util.List;

public class BookmarkService {
    private AppRepository repo;
    public BookmarkService(){
        repo=AppRepository.getInstance();
    }
    public void insertBookmark(BookmarkDataModel data){
        if(repo.getBookmarkByUrl(data.getAddress()).isEmpty()) repo.insertBookMark(data);

    }
    public List<BookmarkDataModel> getBookmarks(){
        return repo.getBookmarks();
    }
    public void deleteBookmark(BookmarkDataModel data) {
        repo.deleteBookmark(data);
    }

    public void updateBookmark(BookmarkDataModel data) {
        repo.updateBookmark(data);
    }
    public void insertBookmark(List<BookmarkDataModel> lst) {
        if (lst == null) return;
        for (BookmarkDataModel data : lst) {
            insertBookmark(data);
        }
    }
}
