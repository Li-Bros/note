package com.example.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private List<Note> notesList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Note note);
        void onDeleteClick(Note note);
    }

    public NotesAdapter(List<Note> notesList, OnItemClickListener listener) {
        this.notesList = notesList;
        this.listener = listener;
    }

    public void setNotes(List<Note> notesList) {
        this.notesList = notesList;
        notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View noteView = inflater.inflate(R.layout.list_item_note, parent, false);
        return new ViewHolder(noteView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note note = notesList.get(position);
        holder.bind(note, listener);
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView titleTextView;
        private final TextView contentTextView; // Nuevo TextView para mostrar el contenido truncado
        private final Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textViewNoteTitle);
            contentTextView = itemView.findViewById(R.id.textViewNoteContent); // Asigna el TextView de contenido
            deleteButton = itemView.findViewById(R.id.buttonDeleteNote);
        }

        public void bind(final Note note, final OnItemClickListener listener) {
            titleTextView.setText(note.getTitle());
            contentTextView.setText(note.getContent()); // Muestra el contenido completo en contentTextView
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(note);
                }
            });
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeleteClick(note);
                }
            });
        }
    }
}
