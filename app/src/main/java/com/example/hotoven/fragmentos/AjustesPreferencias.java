package com.example.hotoven.fragmentos;


import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceFragmentCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.hotoven.MainActivity;
import com.example.hotoven.R;
import com.example.hotoven.bdd_hotOven.BD_HotOven;
import com.example.hotoven.bdd_hotOven.TablasSQLite;
import com.example.hotoven.bdd_hotOven.Usuario;
import com.example.hotoven.views.MenuPrincipal;
import com.example.hotoven.workers.WorkerInicioSesion;
import com.example.hotoven.workers.WorkerUpdateUsuario;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class AjustesPreferencias extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{

    private Usuario usuario;
    /**
     * Se registrará el listener al crear el fragmento
     * @param savedInstanceState
     * @param rootKey
     */
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferencias);
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
            // si detecta cambios en la key idioma cambia el idioma.
            String idioma = sharedPreferences.getString("idioma",Locale.getDefault().getLanguage());
            cambiarIdioma(idioma);
        }else if(s.equals("name")){
           // si detecta cambios en la key name(usuario)
            String usuarioNuevo = sharedPreferences.getString("name",usuario.getUsuario());
            /*BD_HotOven bd_hotOven = new BD_HotOven(getActivity(), "bd_hot_oven.sqlite", null, 1);
            SQLiteDatabase bdd = bd_hotOven.getWritableDatabase();
            String[] devuelve = new String[]{TablasSQLite.USUARIOS_ID};
            Cursor cursor = bdd.query(TablasSQLite.TABLA_USUARIOS,
                    devuelve,TablasSQLite.USUARIOS_USUARIO+"='"+usuarioNuevo+"'",
                    null,null,null,null);

            //si no existe ese usuario, lo modifica en la BDD
            if(cursor.getCount() == 0){
                ContentValues modificacion = new ContentValues();
                modificacion.put(TablasSQLite.USUARIOS_USUARIO,usuarioNuevo);
                bdd.update(TablasSQLite.TABLA_USUARIOS,modificacion,
                        TablasSQLite.USUARIOS_ID+"="+usuario.getId(),null);
            }else{
                // si existe ese usuario, no se realiza ningún cambio
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name",usuario.getUsuario());
                editor.apply();
                Toast.makeText(getActivity().getApplicationContext(),
                        getResources().getText(R.string.ajustes_usuario_no_cambiado),Toast.LENGTH_LONG).show();
            }
            cursor.close();
            bd_hotOven.close();
            */

            Data.Builder data = new Data.Builder();
            data.putString("usuarioNuevo",usuarioNuevo);
            data.putString("usuarioAntiguo",usuario.getUsuario());

            OneTimeWorkRequest trabajoPuntual = new OneTimeWorkRequest.Builder(WorkerUpdateUsuario.class)
                    .setInitialDelay(0, TimeUnit.SECONDS)
                    .setInputData(data.build())
                    .build();
            WorkManager.getInstance(getActivity()).enqueue(trabajoPuntual);

            WorkManager.getInstance(getActivity()).getWorkInfoByIdLiveData(trabajoPuntual.getId())
                    .observe( this,new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(WorkInfo workInfo) {
                            if(workInfo != null&& workInfo.getState().isFinished()) {
                                String resultado = workInfo.getOutputData().getString("resultado");
                                if(resultado.equals("Fail")){
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("name",usuario.getUsuario());
                                    editor.apply();
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            getResources().getText(R.string.ajustes_usuario_no_cambiado),Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
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

    public void cargarDatos(String usuario){
        if(this.usuario == null) {
            this.usuario = new Usuario(usuario);
        }
    }
}
