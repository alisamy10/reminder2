package com.example.reminder2;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;

import android.os.Bundle;
import android.view.Menu;
import android.widget.SearchView;
import com.example.reminder2.database.NoteDataBase;
import com.example.reminder2.model.Note;

import java.util.ArrayList;
import java.util.List;


public class ShowMemory extends AppCompatActivity  implements SearchView.OnQueryTextListener {
    RecyclerView recyclerView  ;
    private RecycleAdapter adapter;
    private List<Note>  noteList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_memory);
        Toolbar toolbar = findViewById(R.id.seach_edit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        noteList  = NoteDataBase.getInstance(this)
                .notesDao()
                .getAllNotes();
        recyclerView = findViewById(R.id.rec);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecycleAdapter(noteList,this);
        recyclerView.setAdapter(adapter);





    }
    @Override
    protected void onStart() {
        super.onStart();
        noteList  = NoteDataBase.getInstance(this)
                .notesDao()
                .getAllNotes();
        adapter.changeLIst(noteList);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        newText= newText.toLowerCase().trim();
        List<Note> noteArrayList= new ArrayList<>();

        for( Note note : noteList){
            String title = note.getTitle().toLowerCase().trim();
            if(title.contains(newText)){
                noteArrayList.add(note);
            }
        }
        adapter.changeLIst(noteArrayList);
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.

        // Associate searchable configuration with the SearchView
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = (SearchView) menu.findItem(R.id.itemSearch).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);

        return true;

    }

}
