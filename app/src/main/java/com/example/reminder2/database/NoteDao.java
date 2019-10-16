package com.example.reminder2.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.reminder2.model.Note;

import java.util.List;

@Dao
public interface NoteDao {
    @Insert
    void insert(Note... note);

    @Update
    void update(Note... note);

    @Delete
    void delete(Note... note);

    @Query("DELETE FROM Note")
    void deleteAllNotes();

    @Query("delete from Note where id=:id")
    void deleteNoteById(int id);

    @Query("select * from Note where id=:id")
    Note getNote(int id);

    @Query("select COUNT (id) from Note ")
    int getCount();

    @Query("SELECT * FROM Note ORDER BY time ASC")
    List<Note> getAllNotes();
}
