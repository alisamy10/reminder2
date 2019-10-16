package com.example.reminder2.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class Note {
    @PrimaryKey(autoGenerate = true)
 private int id;
 private String title,description,date,time;
 private Double lat,lng;
 @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
 byte [] image;

@Ignore
    public Note(String title, String description, String date, String time, Double lat, Double lng, byte[] image) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.lat = lat;
        this.lng = lng;
        this.image = image;
    }

    public Note() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
