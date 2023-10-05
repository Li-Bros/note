package com.example.notes;

public class Note {
    private int noteId;      // Identificador único de la nota
    private String title;    // Título de la nota
    private String content;  // Contenido de la nota

    public Note(int noteId, String title, String content) {
        this.noteId = noteId;
        this.title = title;
        this.content = content;
    }



    public String getTitle() {
        return title; // Donde 'title' es el campo que almacena el título de la nota
    }

    public int getId() {
        return noteId; // Donde 'title' es el campo que almacena el título de la nota
    }

    public String getContent() {
        return content;
    }
}

