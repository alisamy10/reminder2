package com.example.reminder2.ui;


import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.reminder2.Base.BaseActivity;
import com.example.reminder2.R;
import com.example.reminder2.adapter.RecycleAdapter;
import com.example.reminder2.database.NoteDataBase;
import com.example.reminder2.helper.SwipeToDeleteCallback;
import com.example.reminder2.database.model.Note;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ShowMemory extends BaseActivity implements SearchView.OnQueryTextListener {
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
         noteList=new ArrayList<>();
        recyclerView = findViewById(R.id.rec);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Collections.reverse(noteList);

        adapter = new RecycleAdapter(noteList,this);
        recyclerView.setAdapter(adapter);
        enableSwipeToDeleteAndUndo();

    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {

                showMessage("delete", "ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NoteDataBase.getInstance(ShowMemory.this)
                                .notesDao()
                                .delete(adapter.getTodo(viewHolder.getAdapterPosition()));

                        List<Note> list = NoteDataBase.getInstance(ShowMemory.this)
                                .notesDao()
                                .getAllNotes();
                        adapter.changeLIst(list);
                        dialog.dismiss();

                    }
                }, "no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<Note> list = NoteDataBase.getInstance(ShowMemory.this)
                                .notesDao()
                                .getAllNotes();
                        adapter.changeLIst(list);
                        dialog.dismiss();

                    }
                },true);



            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {



        if (item.getItemId() == R.id.delete_All) {
           if(NoteDataBase.getInstance(ShowMemory.this).notesDao().getCount()==0)
               Toast.makeText(this, "No Memories To Delete It ", Toast.LENGTH_SHORT).show();
               else

                  showMessage("Do you want to delete all memory ? ", "YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    NoteDataBase.getInstance(ShowMemory.this)
                            .notesDao()
                            .deleteAllNotes();
                    List<Note> list = NoteDataBase.getInstance(ShowMemory.this)
                            .notesDao()
                            .getAllNotes();
                    adapter.changeLIst(list);

                }
            }, "NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            },true);


        }



        return super.onOptionsItemSelected(item);
    }


}
