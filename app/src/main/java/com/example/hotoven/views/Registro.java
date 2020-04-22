package com.example.hotoven.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.hotoven.R;
import com.example.hotoven.bdd_hotOven.BD_HotOven;
import com.example.hotoven.bdd_hotOven.TablasSQLite;
import com.example.hotoven.dialogs.DialogRegistroDatosVacios;
import com.example.hotoven.workers.WorkerRegistro;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Registro extends AppCompatActivity {

    private TextInputEditText usuario;
    private TextInputEditText contrasena;
    private TextInputEditText contrasenaRepetida;
    private Button botonRegistro;

    //private BD_HotOven gestorBD;
    private SharedPreferences sharedPreferences;
    private String idiomaEstablecido;


    private DialogRegistroDatosVacios dialogRegistroDatosVacios;

    /**
     * Se establece el idioma en la aplicación de SharedPreferences.
     * Crea la actividad Registro.
     * Se inicializan los atributos.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(this);
        idiomaEstablecido = sharedPreferences.getString("idioma","es");
        if(idiomaEstablecido.equals("es")){
            Locale locale = new Locale("es");
            cambiarIdiomaOnCreate(locale);
        }else if(idiomaEstablecido.equals("en")){
            Locale locale = new Locale("en");
            cambiarIdiomaOnCreate(locale);
        }

        setContentView(R.layout.activity_registro);

        //gestorBD = new BD_HotOven(getApplicationContext(), "bd_hot_oven.sqlite", null, 1);
        usuario = (TextInputEditText) findViewById(R.id.registroUsername);
        contrasena = (TextInputEditText) findViewById(R.id.registroContrasena);
        contrasenaRepetida = (TextInputEditText) findViewById(R.id.registroContrasenaRepetida);
    }

    /**
     * Primero se comprueba si todos los datos introducidos son correctos. Si es correcto entonces
     * comprobará si las dos contraseñas son iguales y si es así entonces comprobará si existe el usuario.
     * Si no existe se crea el usuario.
     * @param view el botón de registro
     */
    public void registrarUsuario(View view){
        boolean[] editTexts = {false, false, false};

        editTexts = comprobarEditText(editTexts);

        boolean todoCorrecto = true;
        if (editTexts[0] || editTexts[1] || editTexts[2]) {
            // ALGÚN DATO ESTÁ VACÍO
            todoCorrecto = false;
        }

        if (todoCorrecto) {
            if(contrasena.getText().toString().equals(contrasenaRepetida.getText().toString())){
                // SI CONTRASEÑAS COINCIDEN
                registro();
            }else{
                Toast.makeText(getApplicationContext(),"Las contraseñas no coinciden",Toast.LENGTH_SHORT).show();
            }

        } else {
            Log.i("Registro", "Registro incorrecto");
            if (dialogRegistroDatosVacios == null) {
                dialogRegistroDatosVacios = new DialogRegistroDatosVacios();
            }
            dialogRegistroDatosVacios.show(getSupportFragmentManager(), "datosVaciosRegistro");
        }
    }

    /**
     * Se comprueba el idioma que tenía la actividad con el de SharedPreferences: si es distinto se
     * cambia el idioma cerrando y volviendo a iniciar la actividad.
     */
    @Override
    protected void onResume() {
        super.onResume();

        String idiomaNuevo = sharedPreferences.getString("idioma","es");

        if(!idiomaNuevo.equals(idiomaEstablecido)){
            idiomaEstablecido = idiomaNuevo;
            if(idiomaEstablecido.equals("es")){
                Locale locale = new Locale("es");
                cambiarIdiomaOnResume(locale);
            }else if(idiomaEstablecido.equals("en")){
                Locale locale = new Locale("en");
                cambiarIdiomaOnResume(locale);
            }
        }
    }

    /**
     * Cambia el idioma de la aplicación al reanudarse la actividad. Se destruye la actividad y se
     * vuelve a iniciar
     * @param locale: el idioma almacenado en SharedPreferences
     */
    public void cambiarIdiomaOnResume(Locale locale){
        Locale.setDefault(locale);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);
        recreate();
    }

    /**
     * Cambia el idioma de la aplicación al crearse la actividad
     * @param locale: el idioma almacenado en SharedPreferences
     */
    public void cambiarIdiomaOnCreate(Locale locale){
        Locale.setDefault(locale);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);
    }

    /**
     * Comprueba los TextInputEditTexts si están vacíos o no. Los datos vacíos se almacenan en un array
     * de booleano como true.
     * @param editTexts: array booleano para indicar que editText está vacío
     * @return devuelve un array de booleanos indicando que datos son vacíos
     */
    private boolean[] comprobarEditText(boolean[] editTexts) {
        if (usuario.getText().toString().equals("")) {
            editTexts[0] = true;
        }

        if (contrasena.getText().toString().equals("")) {
            editTexts[1] = true;
        }

        if (contrasenaRepetida.getText().toString().equals("")) {
            editTexts[2] = true;
        }
        return editTexts;
    }

    /**
     * Se añade en la base de datos de la tabla Usuarios el usuario con su contraseña.
     */
    private void registro() {

        Data.Builder data = new Data.Builder();
        data.putString(TablasSQLite.USUARIOS_USUARIO, usuario.getText().toString());
        data.putString(TablasSQLite.USUARIOS_CONTRASENA,contrasena.getText().toString());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        data.putString("token",preferences.getString("token",""));

        OneTimeWorkRequest trabajoPuntual = new OneTimeWorkRequest.Builder(WorkerRegistro.class)
                .setInitialDelay(0, TimeUnit.SECONDS)
                .setInputData(data.build())
                .build();
        WorkManager.getInstance(this).enqueue(trabajoPuntual);

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(trabajoPuntual.getId())
                .observe( this,new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null&& workInfo.getState().isFinished()) {
                            String resultado = workInfo.getOutputData().getString("resultado");
                            if(resultado.equals("Fail")){
                                Toast.makeText(getApplicationContext(),"Existe usuario",Toast.LENGTH_SHORT).show();
                            }else if(resultado.equals("Error")){
                                Toast.makeText(getApplicationContext(),"Fallo servidor",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(),"Registro completado",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    }
                });
    }
    /**
     * Se envía a la actividad AjustesIniciarSesion
     * @param view: el LinearLayout superior de la esquina derecha.
     */
    public void cambiarIdiomaAjustes(View view){
        Intent intent = new Intent(Registro.this, AjustesIniciarSesion.class);
        startActivity(intent);
    }
}
