package com.example.notes;


import static com.example.notes.MyDatabaseHelper.COLUMN_USER_ID;


import androidx.appcompat.app.AppCompatActivity;
import com.example.notes.MyDatabaseHelper;
import java.util.regex.Pattern;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    public EditText editTextName1;
    public EditText editTextLastName1;
    public EditText email;
    public EditText password;
    public TextView textView;

    public Button button;
    public MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);




    private boolean isEmailInUse(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = { "email" };
        String selection = "email = ?";
        String[] selectionArgs = { email };
        Cursor cursor = db.query("users", projection, selection, selectionArgs, null, null, null);
        boolean emailInUse = cursor.getCount() > 0;
        cursor.close();
        return emailInUse;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String authToken = "mi_token_de_autenticacion"; // Declaración y asignación de authToken



        editTextName1 = findViewById(R.id.editTextName);
        editTextLastName1 = findViewById(R.id.editTextLastName);
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        textView = findViewById(R.id.textViewTitle);
        button = findViewById(R.id.Registro);

        //VALIDACIONES CON EXPRESIONES REGULARES

        String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";
        String passwordPattern = "^(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$";





        Switch switchEnableFeature = findViewById(R.id.switchEnableSesion);
        switchEnableFeature.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Se ejecuta cuando el estado del Switch cambia

                if (isChecked) {
                    iniciarSesion();
                } else {
                    registrarme();
                }
            }
        });







            Button buttonRegister = findViewById(R.id.Registro);
            //evento de clic para el botón inicio de sesion o registro
            buttonRegister.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("Range")
                @Override
                public void onClick(View v) {
                    // Compila la expresión regular en un patrón
                    Pattern pattern1 = Pattern.compile(emailPattern);
                    Pattern pattern2 = Pattern.compile(passwordPattern);

                    // Luego, puedes usar el método matches() para verificar si la cadena cumple con el patrón
                    boolean isMatch1 = pattern1.matcher(email.getText().toString()).matches();
                    boolean isMatch2 = pattern2.matcher(password.getText().toString()).matches();

                    String inputEmail = email.getText().toString();
                    if (isMatch1 && isMatch2) {
                        if (textView.getText().equals("Registro")) {
                            if(email.getText().toString().isEmpty() &&
                                    password.getText().toString().isEmpty() &&
                                    editTextName1.getText().toString().isEmpty() &&
                                    editTextLastName1.getText().toString().isEmpty()){



                            }
                            else{

                                if (isEmailInUse(inputEmail)) {
                                    // El correo electrónico ya está en uso, muestra un mensaje de error
                                    Context context = v.getContext();
                                    Toast.makeText(context, "El correo electrónico ya está en uso", Toast.LENGTH_SHORT).show();
                                }else{

                                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                                    ContentValues values = new ContentValues();
                                    values.put("email", email.getText().toString());
                                    values.put("first_name", editTextLastName1.getText().toString());
                                    values.put("last_name", editTextLastName1.getText().toString());
                                    values.put("password", password.getText().toString());

                                    // Inserto el registro en la tabla de usuarios
                                    long newRowId = db.insert("users", null, values);

                                    // Compruebo si la inserción fue exitosa
                                    if (newRowId != -1) {

                                        int userId = (int) newRowId;

                                        // Almacena el ID de usuario en SharedPreferences
                                        guardarUserIdEnSharedPreferences(String.valueOf(userId));

                                        // Inserción exitosa, puedes mostrar un mensaje o realizar otras acciones.
                                        Context context = v.getContext();
                                        Toast.makeText(context, "Bienvenido a WindNotes", Toast.LENGTH_SHORT).show();
                                        // Cuando el usuario inicia sesión con éxito
                                        // Cuando el usuario inicia sesión con éxito
                                        SharedPreferences sharedPreferences = getSharedPreferences("UsuarioPreferences", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("authToken", authToken); // Guarda el token de autenticación en las preferencias compartidas
                                        editor.apply();


                                        Intent intent = new Intent(MainActivity.this, Lobby_Notes.class);
                                        startActivity(intent);
                                        finish();


                                    } else {
                                        // Error durante la inserción, maneja el error según sea necesario.
                                    }

                                    // Cierro la base de datos cuando hayas terminado
                                    db.close();
                                }


                            }
                        }else {
                            EditText editTextEmail = findViewById(R.id.editTextEmail);
                            EditText editTextPassword = findViewById(R.id.editTextPassword);

                            String email1 = editTextEmail.getText().toString();
                            String password = editTextPassword.getText().toString();


                            SQLiteDatabase db = dbHelper.getReadableDatabase();

                            String[] projection = {
                                    "password", // Campo de contraseña en la tabla de usuarios
                                    COLUMN_USER_ID // Columna que contiene el ID del usuario
                            };



                            String selection = "email = ?";
                            String[] selectionArgs = {email1};

                            Cursor cursor = db.query(
                                    "users",   // Nombre de la tabla
                                    projection, // Columnas a consultar (contraseña e ID)
                                    selection, // Cláusula WHERE
                                    selectionArgs, // Valores para la cláusula WHERE
                                    null,
                                    null,
                                    null
                            );

                            String storedPassword = null;
                            int userId = -1;

                            if (cursor.moveToFirst()) {
                                storedPassword = cursor.getString(cursor.getColumnIndex("password"));
                                userId = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID));
                            }

                            cursor.close();
                            db.close();


                            if (storedPassword != null && storedPassword.equals(password)) {

                                if (userId != -1 && storedPassword != null && storedPassword.equals(password)) {
                                    // El usuario se autenticó con éxito, userId contiene el ID del usuario
                                    // Ahora puedes hacer lo que necesites con el ID, como guardarlo en SharedPreferences
                                    guardarUserIdEnSharedPreferences(String.valueOf(userId));

                                    // Continúa con la lógica de inicio de sesión exitoso
                                } else {
                                    // Error de inicio de sesión, el correo electrónico o la contraseña no coinciden
                                    // Muestra un mensaje de error o realiza la lógica correspondiente
                                }
                                int duracion = Toast.LENGTH_SHORT;
                                String mensaje = "Bienvenido a WindNotes"; // Reemplaza con tu mensaje
                                Toast.makeText(getApplicationContext(), mensaje, duracion).show();
                                // Cuando el usuario inicia sesión con éxito
                                SharedPreferences sharedPreferences = getSharedPreferences("UsuarioPreferences", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("authToken", authToken); // Guarda el token de autenticación en las preferencias compartidas
                                editor.apply();



                                Intent intent = new Intent(MainActivity.this, Lobby_Notes.class);
                                startActivity(intent);
                                finish();

                            } else {
                                // Error de inicio de sesión, la contraseña no coincide o el correo electrónico no existe


                                // Crear y mostrar el Toast
                                String mensaje = "El Usuario no Existe"; // Reemplaza con tu mensaje
                                int duracion = Toast.LENGTH_SHORT;
                                Toast.makeText(getApplicationContext(), mensaje, duracion).show();

                            }


                        }

                    }else{

                        String mensaje = "Invalida la direccion de correo o contraseña"; // Reemplaza con tu mensaje
                        int duracion = Toast.LENGTH_SHORT;
                        Toast.makeText(getApplicationContext(), mensaje, duracion).show();

                    }


                }
            });






    }








    private void iniciarSesion(){
        editTextName1.setVisibility(View.GONE);
        editTextLastName1.setVisibility(View.GONE);
        textView.setText("Iniciar Sesion");
        button.setText("Iniciar Sesion");


    }

    private void registrarme(){

        editTextName1.setVisibility(View.VISIBLE);
        editTextLastName1.setVisibility(View.VISIBLE);
        textView.setText("Registro");
        button.setText("Registro");

    }

    private void guardarUserIdEnSharedPreferences(String userId) {
        SharedPreferences sharedPreferences = getSharedPreferences("UsuarioPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", userId);
        editor.apply();
    }




}