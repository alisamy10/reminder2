package com.example.reminder2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Bundle;
import android.widget.EditText;
import android.widget.SearchView;

import com.example.reminder2.model.Note;

import java.util.List;

public class ShowMemory extends AppCompatActivity  implements SearchView.OnQueryTextListener {
    RecyclerView recyclerView  ;
    private RecycleAdapter adapter;
    private List<Note>  noteList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_memory);


    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
