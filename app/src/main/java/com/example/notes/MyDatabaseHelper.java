package com.example.notes;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    // Nombre de la base de datos
    private static final String DATABASE_NAME = "Wind_Notes_DataBase.db";

    // Versión de la base de datos. Si cambias la estructura de la base de datos, debes incrementar la versión.
    private static final int DATABASE_VERSION = 1;

    // Nombre de la tabla de usuarios
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_EMAIL = "email"; // Agregado
    public static final String COLUMN_FIRST_NAME = "first_name"; // Agregado
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_PASSWORD = "password";

    // Nombre de la tabla de notas
    public static final String TABLE_NOTES = "notes";
    public static final String COLUMN_NOTE_ID = "note_id";
    public static final String COLUMN_USER_ID_FK = "user_id";
    public static final String COLUMN_NOTE_TITLE = "title";
    public static final String COLUMN_NOTE_CONTENT = "content";

    // Constructor
    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crea la tabla de usuarios
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_EMAIL + " TEXT,"
                + COLUMN_FIRST_NAME + " TEXT,"
                + COLUMN_LAST_NAME + " TEXT,"
                + COLUMN_PASSWORD + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        // Crea la tabla de notas
        String CREATE_NOTES_TABLE = "CREATE TABLE " + TABLE_NOTES + "("
                + COLUMN_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_ID_FK + " INTEGER,"
                + COLUMN_NOTE_TITLE + " TEXT,"
                + COLUMN_NOTE_CONTENT + " TEXT" + ")";
        db.execSQL(CREATE_NOTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Si cambias la versión de la base de datos, puedes realizar aquí las actualizaciones necesarias.
        // Por ejemplo, puedes eliminar y recrear las tablas.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    // Aquí puedes agregar métodos para insertar, actualizar, eliminar y consultar usuarios y notas.


    public long saveNote(int userId, String title, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        long newRowId = -1; // Valor predeterminado en caso de error

        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USER_ID_FK, userId);
            values.put(COLUMN_NOTE_TITLE, title);
            values.put(COLUMN_NOTE_CONTENT, content);

            newRowId = db.insertOrThrow(TABLE_NOTES, null, values);
        } catch (SQLiteException e) {
            // Manejar el error de inserción, por ejemplo, registrando el error o lanzando una excepción personalizada.
            e.printStackTrace();
        } finally {
            db.close();
        }

        return newRowId;
    }


    public List<Note> getUserNotes(String userId) {
        List<Note> notesList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_NOTE_ID,
                COLUMN_NOTE_TITLE,
                COLUMN_NOTE_CONTENT
        };

        String selection = COLUMN_USER_ID_FK + " = ?";
        String[] selectionArgs = {userId};

        Cursor cursor = db.query(
                TABLE_NOTES,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            int noteId = cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_ID));
            String title = cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_TITLE));
            String content = cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_CONTENT));

            Note note = new Note(noteId, title, content);
            notesList.add(note);
        }
        cursor.close();
        db.close();

        return notesList;
    }

    public void deleteNote(int noteId) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            // Define la cláusula WHERE para eliminar la nota con el ID específico
            String selection = COLUMN_NOTE_ID + " = ?";
            String[] selectionArgs = {String.valueOf(noteId)};

            // Realiza la eliminación
            int rowsDeleted = db.delete(TABLE_NOTES, selection, selectionArgs);
            Log.d("DeleteNote", "Rows deleted: " + rowsDeleted);
        } catch (SQLiteException e) {
            // Maneja cualquier excepción que pueda ocurrir durante la eliminación
            e.printStackTrace();
        } finally {
            // Cierra la conexión de la base de datos
            db.close();
        }
    }



    public Note getNoteById(int noteId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                COLUMN_NOTE_TITLE,
                COLUMN_NOTE_CONTENT
        };

        String selection = COLUMN_NOTE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(noteId)};

        Cursor cursor = db.query(
                TABLE_NOTES,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        Note note = null;

        if (cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_TITLE));
            String content = cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_CONTENT));

            note = new Note(noteId, title, content);
        }

        cursor.close();
        db.close();

        return note;
    }

    public int updateNote(int noteId, String title, String content) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TITLE, title);
        values.put(COLUMN_NOTE_CONTENT, content);

        String selection = COLUMN_NOTE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(noteId)};

        int rowsUpdated = db.update(TABLE_NOTES, values, selection, selectionArgs);

        db.close();

        return rowsUpdated;
    }

    public List<Note> searchNotes(String userId, String query) {
        List<Note> notesList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();





        String[] projection = {
                COLUMN_NOTE_ID,
                COLUMN_NOTE_TITLE,
                COLUMN_NOTE_CONTENT
        };

        String selection = COLUMN_USER_ID_FK + " = ? AND (" +
                COLUMN_NOTE_TITLE + " LIKE ? OR " +
                COLUMN_NOTE_CONTENT + " LIKE ?)";

        String[] selectionArgs = { userId, "%" + query + "%", "%" + query + "%" };

        Cursor cursor = db.query(
                TABLE_NOTES,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            do {
                int noteId = cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_ID));
                String title = cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_TITLE));
                String content = cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_CONTENT));

                Note note = new Note(noteId, title, content);
                notesList.add(note);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return notesList;
    }


}
