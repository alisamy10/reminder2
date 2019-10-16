package com.example.reminder2.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.reminder2.model.Note;


@Database(entities = {Note.class},version = 3,exportSchema = false)
public abstract class NoteDataBase extends RoomDatabase {

    public static final String DB_NAME = "Notes database";
    private static NoteDataBase instance;

    public abstract NoteDao notesDao();
    public static synchronized NoteDataBase getInstance(Context context){
        if(instance==null){
            //initialize
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    NoteDataBase.class,DB_NAME)
                    .fallbackToDestructiveMigration()
                   .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}
