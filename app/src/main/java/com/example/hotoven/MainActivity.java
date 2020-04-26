package com.example.hotoven;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;


import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.provider.Settings;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.hotoven.bdd_hotOven.TablasSQLite;
import com.example.hotoven.utils.GestorNotificacion;
import com.example.hotoven.utils.NotificationControl;
import com.example.hotoven.views.AjustesIniciarSesion;
import com.example.hotoven.views.MenuPrincipal;
import com.example.hotoven.views.Registro;
import com.example.hotoven.workers.WorkerInicioSesion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.ByteArrayOutputStream;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    private TextInputEditText usuario;
    private TextInputEditText contrasena;

    private SharedPreferences sharedPreferences;
    private String idiomaEstablecido;

    private NotificationControl broad;

    private String token;

    /**
     * Se establece el idioma en la aplicación de SharedPreferences.
     * Se inicializa los TextInputEditText
     * Se inicia un Broadcast Receiver dinámico referente a las Notificaciones
     * Se crea el canal de notificación
     * Crea la actividad.
     * @param savedInstanceState: el bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Extraído de Stack Overflow. Añadido en varias actividades.
         * Pregunta: https://stackoverflow.com/questions/31183732/changing-language-in-run-time-with-preferences-android
         * Autor: https://stackoverflow.com/users/5027640/zolt%c3%a1n-umlauf
         */
        sharedPreferences = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(this);
        idiomaEstablecido = sharedPreferences.getString("idioma","es");
        if(idiomaEstablecido.equals("es")){
            Locale locale = new Locale("es");
            cambiarIdiomaOnCreate(locale);
        }else if(idiomaEstablecido.equals("en")){
            Locale locale = new Locale("en");
            cambiarIdiomaOnCreate(locale);
        }

        // CREA EL TOKEN
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(!task.isSuccessful()){
                            return;
                        }
                        token = task.getResult().getToken();
                        Log.i("Firebase",token);

                        String tokenShared = sharedPreferences.getString("token","");
                        if(!tokenShared.equals(token)){
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("token",token);
                            editor.apply();
                        }
                    }
                });


        setContentView(R.layout.activity_main);

        usuario = (TextInputEditText) findViewById(R.id.username);
        contrasena = (TextInputEditText) findViewById(R.id.contrasena);
        //gestorBD = new BD_HotOven(this, "bd_hot_oven.sqlite", null, 1);

        // SE INICIALIZA LAS ACCIONES DE LAS NOTIFICACIONES PARA QUE SE EJECUTEN EN EL BROADCAST
        broad = new NotificationControl();
        IntentFilter confirmar = new IntentFilter("CONFIRMAR");
        registerReceiver(broad,confirmar);
        IntentFilter rechazar = new IntentFilter("RECHAZAR");
        registerReceiver(broad,rechazar);

        GestorNotificacion gestorNotificacion = GestorNotificacion.getGestorNotificacion();
        gestorNotificacion.changeContext(this);
        gestorNotificacion.createNotificationChannel();
    }

    /**
     * Al ponerse en marcha la actividad se limpia el texto de los TextInputEditText.
     * Se comprueba el idioma que tenía la actividad con el de SharedPreferences: si es distinto se
     * cambia el idioma cerrando y volviendo a iniciar la actividad.
     */
    @Override
    protected void onResume() {
        super.onResume();

        /**
         * Extraído de Stack . Añadido en varias actividades.
         * Pregunta: https://stackoverflow.com/questions/31183732/changing-language-in-run-time-with-preferences-android
         * Autor: https://stackoverflow.com/users/5027640/zolt%c3%a1n-umlauf
         */
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
     * Al destruir la actividad se elimina el registro
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broad);
    }


    /**
     * Se envía a la actividad registro
     * @param vista: el texto de no tener una cuenta
     */
    public void crearCuenta(View vista){
        Intent intent = new Intent(MainActivity.this, Registro.class);
        startActivity(intent);
    }

    /**
     * Se inicia sesión. Se realiza comprobaciones si existe el usuario y la contraseña y si es correcto.
     * Si no es correcto te avisa mediante un mensaje.
     * Se envía en el intent el nombre del usuario y el id.
     * @param vista: el botón de inicio sesión
     */
    public void iniciarSesion(View vista){
        boolean todoCorrecto = comprobarEditTexts();

        if(validaPermisos()){
            if(todoCorrecto){

                Data.Builder data = new Data.Builder();
                data.putString(TablasSQLite.USUARIOS_USUARIO, usuario.getText().toString());
                data.putString(TablasSQLite.USUARIOS_CONTRASENA,contrasena.getText().toString());
                data.putString("token",sharedPreferences.getString("token",""));


                // WORKER QUE INICIA SESION
                OneTimeWorkRequest trabajoPuntual = new OneTimeWorkRequest.Builder(WorkerInicioSesion.class)
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
                                    JSONParser parser = new JSONParser();
                                    try{
                                        JSONObject json = (JSONObject) parser.parse(resultado);
                                        String usuarioInicio = (String) json.get("usuario");
                                        if(!usuarioInicio.equals("")){
                                            // SI ES CORRECTO EL INICIO SE SESION

                                            Intent intent = new Intent(MainActivity.this, MenuPrincipal.class);
                                            intent.putExtra("usuarioIniciado",usuario.getText().toString());
                                            startActivity(intent);
                                        }else{
                                            usuario.setCompoundDrawablesWithIntrinsicBounds(
                                                    0,0,R.drawable.ic_error_rojo24,0);
                                            Toast.makeText(getApplicationContext(),"Usuario/Contraseña mal",Toast.LENGTH_SHORT).show();
                                        }

                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
            }
        }

    }

    /**
     * Se comprueba si los textos de usuario y contraseña se han introducido datos.
     * @return devuelve true si se ha introducido los textos, sino devuelve false.
     */
    private boolean comprobarEditTexts(){
        boolean todoCorrecto=true;

        if(usuario.getText().toString().equals("")){
            todoCorrecto=false;
            usuario.setCompoundDrawablesWithIntrinsicBounds(
                    0,0,R.drawable.ic_error_rojo24,0);
        }else{
            usuario.setCompoundDrawablesWithIntrinsicBounds(
                    0,0,0,0);
        }

        if(contrasena.getText().toString().equals("")){
            todoCorrecto=false;
            contrasena.setCompoundDrawablesWithIntrinsicBounds(
                    0,0,R.drawable.ic_error_rojo24,0);
        }else{
            contrasena.setCompoundDrawablesWithIntrinsicBounds(
                    0,0,0,0);
        }
        return todoCorrecto;
    }

    /**
     * Cambia el idioma de la aplicación al reanudarse la actividad. Se destruye la actividad y se
     * vuelve a iniciar
     * @param locale: el idioma almacenado en SharedPreferences
     */
    public void cambiarIdiomaOnResume(Locale locale){

        /**
         * Extraído de Stack Overflow. Añadido en varias actividades.
         * Pregunta: https://stackoverflow.com/questions/31183732/changing-language-in-run-time-with-preferences-android
         * Autor: https://stackoverflow.com/users/5027640/zolt%c3%a1n-umlauf
         */
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

        /**
         * Extraído de Stack Overflow. Añadido en varias actividades.
         * Pregunta: https://stackoverflow.com/questions/31183732/changing-language-in-run-time-with-preferences-android
         * Autor: https://stackoverflow.com/users/5027640/zolt%c3%a1n-umlauf
         */
        Locale.setDefault(locale);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);
    }

    /**
     * Se envía a la actividad AjustesIniciarSesion
     * @param view: el LinearLayout superior de la esquina derecha.
     */
    public void cambiarIdiomaAjustes(View view){
        Intent intent = new Intent(MainActivity.this, AjustesIniciarSesion.class);
        startActivity(intent);
    }

    /**
     * Se comprueban los permisos. Si hay algún permiso que no se ha aceptado entonces se manda la petición de
     * que se acepten.
     * @return Si no se aceptan los permisos entonces devolverá true, sino false.
     */
    private boolean validaPermisos(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE ) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE )) {
                Toast.makeText(getApplicationContext(),getResources().getText(R.string.permisos),Toast.LENGTH_LONG).show();
            } else {
                Intent i = new Intent();
                i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                i.setData(uri);
                startActivity(i);
            }
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 15);
        } else {
            return true;
        }
        return false;
    }
}
