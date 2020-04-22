package com.example.hotoven.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;


import com.example.hotoven.R;
import com.example.hotoven.fragmentos.FragmentoRestauranteInformacion;

import java.util.Locale;


public class RestauranteInformacion extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private String idiomaEstablecido;

    public RestauranteInformacion(){

    }

    /**
     * Se establece el idioma en la aplicación de SharedPreferences.
     * Crea la actividad que contiene la información del restaurante seleccionado.
     * Se envía al fragmento el nombre y el telefono del restaurante junto con la información del
     * usuario.
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

        setContentView(R.layout.activity_restaurante_informacion);

        // SE PASAN LOS DATOS DEL USUARIO Y DEL RESTAURANTE AL FRAGMENTO
        FragmentoRestauranteInformacion restauranteInformacion = (FragmentoRestauranteInformacion)
                getSupportFragmentManager().findFragmentById(R.id.fragmento_restaurante_info);
        String nombreRes = getIntent().getStringExtra("nombreRestaurante");
        int telRes = getIntent().getIntExtra("telefonoRestaurante",0);
        String nombreUsuario = getIntent().getStringExtra("nombreUsuario");
        String url = getIntent().getStringExtra("urlRestaurante");
        String tipo = getIntent().getStringExtra("tipoRestaurante");
        int imagen = getIntent().getIntExtra("imagenRestaurante",0);
        double latitud;
        double longitud;
        try{
             latitud = Double.valueOf(getIntent().getStringExtra("latitud"));
             longitud = Double.valueOf(getIntent().getStringExtra("longitud"));
        }catch (Exception e){
             latitud = getIntent().getDoubleExtra("latitud",0);
             longitud = getIntent().getDoubleExtra("longitud",0);
        }

        int idRestaurante = getIntent().getIntExtra("id",0);
        restauranteInformacion.cargarDatos(nombreRes,telRes,nombreUsuario,url,tipo,imagen,idRestaurante,
                latitud,longitud);

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

}
