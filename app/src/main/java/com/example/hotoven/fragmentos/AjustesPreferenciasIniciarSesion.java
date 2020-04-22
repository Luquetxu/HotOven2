package com.example.hotoven.fragmentos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.hotoven.R;

import java.util.Locale;

public class AjustesPreferenciasIniciarSesion extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{

        /**
         * Se registrará el listener al crear el fragmento
         * @param savedInstanceState
         * @param rootKey
         */
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
                addPreferencesFromResource(R.xml.preferencias_iniciarsesion);
                getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        /**
         * Se desregistrará el listener cuando el fragmento pase al estado de pausa.
         */
        @Override
        public void onPause() {
                super.onPause();
                getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        /**
         * Al detectar un cambio en el dato del "idioma" ejecutará el método cambiarIdioma() con el nuevo
         * idioma seleccionado
         * @param sharedPreferences las preferencias
         * @param s el key de la preferencia modificada
         */
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                if(s.equals("idioma")){
                        String idioma = sharedPreferences.getString("idioma", Locale.getDefault().getLanguage());
                        cambiarIdioma(idioma);
                }
        }

        /**
         * Cambia el idioma de la aplicación
         * @param idim: idioma nuevo
         */
        private void cambiarIdioma(String idim){
                Locale nuevaloc = new Locale(idim);
                Locale.setDefault(nuevaloc);
                Configuration config = new Configuration();
                config.locale = nuevaloc;
                getActivity().getBaseContext().getResources().updateConfiguration(config,getActivity().getBaseContext().getResources()
                .getDisplayMetrics());
                getActivity().finish();
                Intent intent = getActivity().getIntent();
                startActivity(intent);
        }
}
