package com.example.reminder2.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.reminder2.database.model.Note;


@Database(entities = {Note.class},version = 3,exportSchema = false)
public abstract class NoteDataBase extends RoomDatabase {

    private static NoteDataBase instance;

    public abstract NoteDao notesDao();
    public static synchronized NoteDataBase getInstance(Context context){
        if(instance==null){
            //initialize
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    NoteDataBase.class,"Notes database")
                    .fallbackToDestructiveMigration()
                   .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}
