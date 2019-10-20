package com.example.reminder2.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.example.reminder2.R;
import com.example.reminder2.database.model.Note;

import java.util.List;


public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {

    private List<Note> noteList;

    private Context context;

    public RecycleAdapter (List<Note> noteList, Context context) {
        this.noteList = noteList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Note note = noteList.get(position);
        holder.title.setText(note.getTitle());
        holder.txt_desc.setText(note.getDescription());
        holder.txt_time.setText(note.getTime());
        holder.txt_date.setText(note.getDate());

         if(note.getImage()!=null)
        holder.memoryImage.setImageBitmap(BitmapFactory.decodeByteArray(note.getImage(),0,note.getImage().length));
    }

    @Override
    public int getItemCount() {
        if(noteList==null)return 0;
        return noteList.size();
    }
    public Note getNote( int position){

        Note todo = noteList.get(position);
        return todo;
    }
    public void changeLIst ( List<Note> newList){

        this.noteList= newList;
        notifyDataSetChanged();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView txt_desc;
        TextView txt_date;
        TextView txt_time ;
        ImageView memoryImage;


        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            title =itemView.findViewById(R.id.tittle);
            txt_desc=itemView.findViewById(R.id.txt_desc);
            txt_date=itemView.findViewById(R.id.txt_date);
            txt_time=itemView.findViewById(R.id.txt_time);
            memoryImage=itemView.findViewById(R.id.image);

        }
    }
}
