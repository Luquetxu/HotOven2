package com.example.hotoven.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.content.ContentValues;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hotoven.MainActivity;
import com.example.hotoven.R;
import com.example.hotoven.bdd_hotOven.BD_HotOven;
import com.example.hotoven.bdd_hotOven.TablasSQLite;
import com.example.hotoven.bdd_hotOven.Usuario;
import com.example.hotoven.utils.AdapterListViewPedidos;
import com.example.hotoven.utils.AdapterListViewRestaurantes;
import com.example.hotoven.workers.WorkerDeletePedido;
import com.example.hotoven.workers.WorkerInicioSesion;
import com.example.hotoven.workers.WorkerListaPedidos;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Pedidos extends AppCompatActivity {

    private Usuario usuario;

    private ArrayList<String> nombres;
    private ArrayList<String> fechas;
    private ArrayList<Integer> costes;
    private ArrayList<Integer> idPedidos;
    private AdapterListViewPedidos adapterListViewPedidos;

    private ListView listView;

    private SharedPreferences sharedPreferences;
    private String idiomaEstablecido;

    /**
     * Es uno de los menús del menú principal, en este caso los pedidos realizados.
     * Se establece el idioma en la aplicación de SharedPreferences.
     * Crea la actividad.
     * Se crea el usuario con los datos obtenidos del bundle.
     * Se inicializan los atributos
     * Se crea el ListView personalizado añadiendo los datos que se contiene en la Base de datos SQLITE
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

        setContentView(R.layout.activity_pedidos);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Log.i("usu",bundle.getString("usuarioNombre"));
            usuario = new Usuario(bundle.getString("usuarioNombre"));
        }
        //gestorBD = new BD_HotOven(getApplicationContext(), "bd_hot_oven.sqlite", null, 1);
        nombres = new ArrayList<String>();
        fechas = new ArrayList<String>();
        costes = new ArrayList<Integer>();
        idPedidos = new ArrayList<Integer>();

        Data.Builder data = new Data.Builder();
        data.putString(TablasSQLite.USUARIOS_USUARIO,usuario.getUsuario());

        // SE CREA EL WORKER QUE REALIZA UNA CONSULTA A LA BD
        OneTimeWorkRequest trabajoPuntual = new OneTimeWorkRequest.Builder(WorkerListaPedidos.class)
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
                                // RELLENA LA LISTA DE PEDIDOS RESPECTO A LA BASE DE DATOS

                                JSONArray jsonArray = (JSONArray) parser.parse(resultado);
                                if(jsonArray.size()>0){
                                    for(int i=0; i< jsonArray.size();i++) {
                                        JSONObject json = (JSONObject) jsonArray.get(i);

                                        nombres.add((String) json.get("nombre"));
                                        fechas.add((String) json.get("fechaTiempo"));
                                        costes.add(Integer.valueOf((String) json.get("precio")));
                                        idPedidos.add(Integer.valueOf((String) json.get("id")));

                                        listView = (ListView) findViewById(R.id.pedidos_lista);
                                        adapterListViewPedidos = new AdapterListViewPedidos(getApplicationContext(), nombres, costes, fechas);
                                        listView.setAdapter(adapterListViewPedidos);

                                        listenerListView();
                                    }
                                }else{
                                    Log.i("PEDIDOS","KKKKKKKKKKKKKKKKKKKK");
                                    TextView textView = (TextView) findViewById(R.id.pedidos_lista_vacia);
                                    textView.setText(getResources().getString(R.string.pedidos_lista_vacia));
                                    }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                });



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
     * Se inicializa el listener de pulsar un dato del ListView durante un largo tiempo. Si se pulsa
     * durante un buen rato se elimina el pedido, ya sea visualmente en la lista y de la base de datos.
     */
    private void listenerListView(){
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                Data.Builder data = new Data.Builder();
                data.putInt("pedido",idPedidos.get(i));

                // WORKER QUE ELIMINE EL PEDIDO
                OneTimeWorkRequest trabajoPuntual = new OneTimeWorkRequest.Builder(WorkerDeletePedido.class)
                        .setInitialDelay(0, TimeUnit.SECONDS)
                        .setInputData(data.build())
                        .build();
                WorkManager.getInstance(getApplicationContext()).enqueue(trabajoPuntual);

                nombres.remove(i);
                fechas.remove(i);
                costes.remove(i);
                adapterListViewPedidos.notifyDataSetChanged();
                return true;
            }
        });
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
