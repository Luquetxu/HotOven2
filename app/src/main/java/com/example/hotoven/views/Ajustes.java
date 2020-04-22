package com.example.hotoven.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;

import com.example.hotoven.R;
import com.example.hotoven.bdd_hotOven.Usuario;
import com.example.hotoven.fragmentos.AjustesPreferencias;
import com.example.hotoven.fragmentos.FragmentoRestauranteInformacion;

import java.util.Locale;

public class Ajustes extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private String idiomaEstablecido;
    private Usuario usuario;

    /**
     * Se establece el idioma en la aplicación de SharedPreferences.
     * Crea la actividad.
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

        setContentView(R.layout.activity_ajustes_preferencias);


        Bundle bundle = getIntent().getExtras();
        if(bundle !=null){
            usuario = new Usuario(bundle.getString("usuarioNombre"));
        }

        AjustesPreferencias ajustesFragment = (AjustesPreferencias)
                getSupportFragmentManager().findFragmentById(R.id.activity_ajustes);

        ajustesFragment.cargarDatos(usuario.getUsuario());
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
