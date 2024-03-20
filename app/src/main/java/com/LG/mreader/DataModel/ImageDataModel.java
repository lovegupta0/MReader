package com.LG.mreader.DataModel;


import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "LGImage",foreignKeys = @ForeignKey(
        entity = LibraryDataModel.class,
        parentColumns = "id",
        childColumns = "id",
        onDelete = ForeignKey.CASCADE
))
public class ImageDataModel {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String src;

    public ImageDataModel() {
    }

    public ImageDataModel(int id, String src) {
        this.id = id;
        this.src = src;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }
}
