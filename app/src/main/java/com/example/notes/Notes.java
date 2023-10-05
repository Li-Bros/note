package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;


public class Notes extends AppCompatActivity {


    @Override
    public void onBackPressed() {
        // Personaliza la acción cuando se presiona el botón de retroceso en el dispositivo
        // Por ejemplo, puedes cerrar la actividad actual o realizar alguna otra acción.
        // Llama a super.onBackPressed() si deseas mantener el comportamiento predeterminado de retroceso.

        // Llama al método saveNote() para guardar la nota
        EditText editTextTitle = findViewById(R.id.Title);
        EditText editTextContent = findViewById(R.id.editTextTextEmailAddress);
        String title = editTextTitle.getText().toString();
        String content = editTextContent.getText().toString();


        if(title.isEmpty() || content.isEmpty()){
            if(title.isEmpty() && content.isEmpty()){
                Intent intent = new Intent(Notes.this, Lobby_Notes.class);
                startActivity(intent);
                finish();

            }else{
                String mensaje = "Debe llenar los campos";
                int duracion = Toast.LENGTH_SHORT;
                Toast.makeText(getApplicationContext(), mensaje, duracion).show();
            }

        }else{
            String mensaje = "Guardado Exitosamente";
            int duracion = Toast.LENGTH_SHORT;
            Toast.makeText(getApplicationContext(), mensaje, duracion).show();
            saveNote();
        }


        }
        MyDatabaseHelper dbHelper; // No es necesario inicializarlo aquí
        private boolean isEditing = false;
        private int noteIdToUpdate = -1;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_notes);

            dbHelper = new MyDatabaseHelper(this); // Inicializa dbHelper aquí

            // Obtén el ID de la nota que se está editando desde los extras del intent
            Intent intent = getIntent();
            noteIdToUpdate = intent.getIntExtra("noteId", -1);

            // Llama a loadNoteDataToEdit para cargar los datos de la nota si es una edición
            if (noteIdToUpdate != -1) {
                loadNoteDataToEdit(noteIdToUpdate);
                isEditing = true; // Indica que estás editando una nota existente
            }

            Button buttonSave = findViewById(R.id.Save);
            buttonSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Llama al método saveNote() para guardar la nota
                    EditText editTextTitle = findViewById(R.id.Title);
                    EditText editTextContent = findViewById(R.id.editTextTextEmailAddress);
                    String title = editTextTitle.getText().toString();
                    String content = editTextContent.getText().toString();

                    if(title.isEmpty() || content.isEmpty()){
                        String mensaje = "Debe llenar los campos";
                        int duracion = Toast.LENGTH_SHORT;
                        Toast.makeText(getApplicationContext(), mensaje, duracion).show();
                    }else{
                        String mensaje = "Guardado Exitosamente";
                        int duracion = Toast.LENGTH_SHORT;
                        Toast.makeText(getApplicationContext(), mensaje, duracion).show();
                        saveNote();
                    }

                }
            });



            Button button = findViewById(R.id.Back);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Llama al método saveNote() para guardar la nota

                    Intent intent = new Intent(Notes.this, Lobby_Notes.class);
                    startActivity(intent);
                    finish();
                }
            });
        }

        private String obtenerUserIdDesdeSharedPreferences() {
            SharedPreferences sharedPreferences = getSharedPreferences("UsuarioPreferences", MODE_PRIVATE);
            return sharedPreferences.getString("userId", null);
        }

        private void saveNote() {
            EditText editTextTitle = findViewById(R.id.Title);
            EditText editTextContent = findViewById(R.id.editTextTextEmailAddress);
            String title = editTextTitle.getText().toString();
            String content = editTextContent.getText().toString();

            // Obtiene el userId almacenado en SharedPreferences
            String userId = obtenerUserIdDesdeSharedPreferences();

            if (isEditing) {
                // Estamos editando una nota existente, actualiza la nota
                int rowsAffected = dbHelper.updateNote(noteIdToUpdate, title, content);
                if (rowsAffected > 0) {
                    // Actualización exitosa, puedes mostrar un mensaje de éxito o realizar otras acciones
                    editTextTitle.setText("");
                    editTextContent.setText("");
                    Intent intent = new Intent(Notes.this, Lobby_Notes.class);
                    startActivity(intent);
                    finish();// Cierra la actividad de edición después de guardar
                } else {
                    // Error durante la actualización, maneja el error según sea necesario
                }
            } else {
                // Estamos creando una nueva nota
                long newRowId = dbHelper.saveNote(Integer.parseInt(userId), title, content);

                if (newRowId != -1) {
                    // Inserción exitosa, puedes mostrar un mensaje de éxito o realizar otras acciones
                    // Limpia los campos de entrada después de guardar la nota
                    editTextTitle.setText("");
                    editTextContent.setText("");

                    Intent intent = new Intent(Notes.this, Lobby_Notes.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Error durante la inserción, maneja el error según sea necesario
                }
            }
        }

        private void loadNoteDataToEdit(int noteIdToUpdate) {
            if (noteIdToUpdate != -1) {
                // No es necesario crear una nueva instancia de MyDatabaseHelper aquí
                // Utiliza la instancia dbHelper ya creada en onCreate

                // Obtén la nota existente por su ID
                Note existingNote = dbHelper.getNoteById(noteIdToUpdate);

                if (existingNote != null) {
                    // Si se encuentra la nota, carga sus datos en los campos de entrada
                    EditText editTextTitle = findViewById(R.id.Title);
                    EditText editTextContent = findViewById(R.id.editTextTextEmailAddress);

                    editTextTitle.setText(existingNote.getTitle());
                    editTextContent.setText(existingNote.getContent());
                } else {
                    // Manejar el caso en el que no se pueda encontrar la nota
                }
            }
        }




}
