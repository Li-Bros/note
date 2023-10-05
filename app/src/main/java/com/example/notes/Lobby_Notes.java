package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class Lobby_Notes extends AppCompatActivity {



    MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
    NotesAdapter adapter;
    List<Note> notesList = new ArrayList<>();
    List<Note> filteredNotesList = new ArrayList<>();
    private boolean isSearching = false;
    private String userId;
    private List<Note> userNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby_notes);

        // Verifica si el usuario está autenticado
        if (!isUserAuthenticated()) {
            // El usuario no está autenticado, redirige a la pantalla de inicio de sesión
            redirectToLoginActivity();
        }

        Button buttonCerrar = findViewById(R.id.Exit);

        buttonCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cuando el usuario cierra sesión
                SharedPreferences sharedPreferences = getSharedPreferences("SesionPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.apply();

                // Elimina el token de autenticación o el ID de usuario de SharedPreferences
                clearUserAuthentication();

                // Redirige al usuario a la pantalla de inicio de sesión
                redirectToLoginActivity();
            }
        });

        // Obtén el usuario autenticado desde SharedPreferences
        userId = obtenerUserIdDesdeSharedPreferences();
        userNotes = dbHelper.getUserNotes(userId);

        // Configura el RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewNotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Obtiene las notas del usuario y configura el adaptador
        List<Note> userNotes = dbHelper.getUserNotes(userId);
        adapter = new NotesAdapter(userNotes, new NotesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                // Implementa lo que sucede cuando se hace clic en un elemento de la lista
                // Por ejemplo, puedes abrir la nota en una nueva actividad o realizar otra acción.
                // Puedes usar la información de 'note' para abrir la nota seleccionada.
                // Crea un Intent para abrir la actividad Notes en modo de edición

                Intent intent = new Intent(Lobby_Notes.this, Notes.class);
                intent.putExtra("isEditing", true); // Indica que estás editando una nota existente
                intent.putExtra("noteId", note.getId()); // Envía el ID de la nota a la actividad de edición
                startActivity(intent);
                finish();

            }

            @Override
            public void onDeleteClick(Note note) {
                // Obtén el ID de la nota que se va a eliminar
                int noteId = note.getId();

                // Llama al método deleteNote para eliminar la nota de la base de datos
                dbHelper.deleteNote(noteId);

                // Actualiza la lista de notas del usuario (userNotes) para reflejar la eliminación
                userNotes.remove(note);

                // Si estás en modo de búsqueda, también actualiza filteredNotesList si es necesario
                if (isSearching) {
                    filteredNotesList.remove(note);
                    // Notifica al adaptador que se ha eliminado un elemento
                    adapter.notifyDataSetChanged();
                }

                // Notifica al adaptador que se ha eliminado un elemento
                adapter.notifyDataSetChanged();

                Intent intent = new Intent(Lobby_Notes.this, Lobby_Notes.class);
                startActivity(intent);
                finish();
            }
        });
        recyclerView.setAdapter(adapter);

        Button buttonRegister = findViewById(R.id.New);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Este código se ejecutará cuando se haga clic en el botón "Registro"
                // Abre la actividad Notes para crear una nueva nota

                Intent intent = new Intent(Lobby_Notes.this, Notes.class);
                startActivity(intent);
                finish();

            }
        });

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Aquí puedes realizar la búsqueda de notas en tu base de datos
                // y actualizar la lista de notas en el adaptador con los resultados
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Si deseas realizar la búsqueda en tiempo real mientras el usuario escribe,
                // puedes manejarla en este método
                if (!newText.isEmpty()) {
                    // Realiza la búsqueda en tiempo real
                    performSearch(newText);
                } else {
                    // Si el texto de búsqueda está vacío, muestra todas las notas del usuario
                    isSearching = false; // No estamos en modo de búsqueda
                    adapter.setNotes(userNotes);
                    adapter.notifyDataSetChanged();
                }
                return true;
            }
        });
    }

    private String obtenerUserIdDesdeSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("UsuarioPreferences", MODE_PRIVATE);
        return sharedPreferences.getString("userId", null);
    }

    private void performSearch(String query) {
        // Realiza la búsqueda en la base de datos
        List<Note> searchResults = dbHelper.searchNotes(userId, query);

        // Actualiza el adaptador con los resultados de búsqueda
        adapter.setNotes(searchResults);
        adapter.notifyDataSetChanged();

        // Establece isSearching según si hay texto de búsqueda o no
        isSearching = !query.isEmpty();
    }

    // Método para verificar si el usuario está autenticado
    private boolean isUserAuthenticated() {
        // Verifica si el token de autenticación o el ID de usuario está presente en SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UsuarioPreferences", MODE_PRIVATE);
        String authToken = sharedPreferences.getString("authToken", null);
        return authToken != null; // Retorna true si el usuario está autenticado, de lo contrario, false
    }

    // Método para redirigir a la pantalla de inicio de sesión
    private void redirectToLoginActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // Cierra la actividad actual para evitar que el usuario retroceda
    }

    // Método para eliminar la autenticación del usuario
    private void clearUserAuthentication() {
        SharedPreferences sharedPreferences = getSharedPreferences("UsuarioPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("authToken"); // Reemplaza con la clave que utilizas para el token de autenticación
        editor.apply();
    }
}
