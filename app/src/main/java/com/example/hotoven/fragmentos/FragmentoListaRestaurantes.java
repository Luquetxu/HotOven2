package com.example.hotoven.fragmentos;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hotoven.MainActivity;
import com.example.hotoven.R;
import com.example.hotoven.bdd_hotOven.BD_HotOven;
import com.example.hotoven.bdd_hotOven.TablasSQLite;
import com.example.hotoven.otros.Distancia;
import com.example.hotoven.otros.Posicion;
import com.example.hotoven.utils.AdapterListViewRestaurantes;
import com.example.hotoven.views.MapaRestaurante;
import com.example.hotoven.views.MenuPrincipal;
import com.example.hotoven.views.RestauranteInformacion;
import com.example.hotoven.views.Restaurantes;
import com.example.hotoven.workers.WorkerInicioSesion;
import com.example.hotoven.workers.WorkerListaRestaurantes;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentoListaRestaurantes extends Fragment {

    private String[] restaurantesNombres;
    private int[] restaurantesTelefono;
    private int[] restaurantesImg;
    private String[] restaurantesTipo;
    private String[] restauranteUrl;
    private int[] restauranteID;
    private org.json.JSONObject[] jsonsLugar;

    private ListenerListaFragment listenerListaFragment;

    private double latitudUsuario;
    private double longitudUsuario;

    public interface ListenerListaFragment {
        void seleccionarElemento(String titulo, int telefono, String url, String tipo, int imagen, int id, org.json.JSONObject json);
    }

    /**
     * Se añade al fragmento el layout de la lista personalizada
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return el fragmento con la lista personalizada
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_fragmento_lista_restaurantes, container, false);

        // CALCULA QUE RESTAURANTE ESTÁ MÁS CERCA DEL USUARIO
        LinearLayout linearLayout_res_cercano = vista.findViewById(R.id.linearLayaout_restaurante_cercano);
        linearLayout_res_cercano.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(comprobarPermisos()){
                    //SE OBTIENE LA LOCALIZACIÓN DEL USUARIO
                    FusedLocationProviderClient locationUser = LocationServices.getFusedLocationProviderClient(
                            getActivity());

                    locationUser.getLastLocation()
                            .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if(location !=null){
                                        // SI SE OBTIENE LA LOCALIZACIÓN
                                        latitudUsuario = location.getLatitude();
                                        longitudUsuario = location.getLongitude();

                                        Posicion posicionUser = new Posicion(latitudUsuario,longitudUsuario);

                                        int restauranteMasCercano = 0;
                                        double distanciaMasCercana = 0;

                                        // SE CALCULA QUE RESTAURANTE ESTÁ MÁS CERCA
                                        for (int j = 0; j < jsonsLugar.length; j++) {
                                            org.json.JSONObject json = jsonsLugar[j];
                                            try {
                                                double latitud = Double.valueOf((String) json.get("latitud"));
                                                double longitud = Double.valueOf((String) json.get("longitud"));

                                                Posicion posicionRestaurante = new Posicion(latitud,longitud);
                                                Distancia distancia = new Distancia(posicionUser,posicionRestaurante);
                                                double distanciaARestaurante = distancia.calcularDistancia();

                                                if(distanciaARestaurante < distanciaMasCercana){
                                                    restauranteMasCercano = j;
                                                    distanciaMasCercana = distanciaARestaurante;
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        org.json.JSONObject jsonCercano = jsonsLugar[restauranteMasCercano];
                                        try{
                                            // SE DIRIGE A LA ACTIVIDAD DEL MAPA DEL RESTAURANTE MÁS CERCANO
                                            double latitudCercano = Double.valueOf((String) jsonCercano.get("latitud"));
                                            double longitudCercano = Double.valueOf((String) jsonCercano.get("longitud"));

                                            Intent i = new Intent(getActivity(), MapaRestaurante.class);

                                            i.putExtra("nombreRestaurante",restaurantesNombres[restauranteMasCercano]);
                                            i.putExtra("tipoRestaurante",restaurantesTipo[restauranteMasCercano]);
                                            i.putExtra("longitud",longitudCercano);
                                            i.putExtra("latitud",latitudCercano);
                                            i.putExtra("telefonoRestaurante",restaurantesTelefono[restauranteMasCercano]);
                                            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                            i.putExtra("nombreUsuario",pref.getString("usuario",""));
                                            i.putExtra("urlRestaurante",restauranteUrl[restauranteMasCercano]);
                                            i.putExtra("imagenRestaurante",restaurantesImg[restauranteMasCercano]);
                                            i.putExtra("id",restauranteID[restauranteMasCercano]);

                                            startActivity(i);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                }

            }
        });

        return vista;
    }

    /**
     * Al crear la actividad se rellena la listView del layout del fragmento con todos los datos de la
     * Tabla Restaurantes de la BDD.
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // CONSULTA A LA BD MEDIANTE UN WORKER
        OneTimeWorkRequest trabajoPuntual = new OneTimeWorkRequest.Builder(WorkerListaRestaurantes.class)
                .setInitialDelay(0, TimeUnit.SECONDS)
                .build();
        WorkManager.getInstance(getActivity()).enqueue(trabajoPuntual);

        WorkManager.getInstance(getActivity()).getWorkInfoByIdLiveData(trabajoPuntual.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null && workInfo.getState().isFinished()) {
                            String resultado = workInfo.getOutputData().getString("resultado");
                            JSONParser parser = new JSONParser();
                            try {
                                JSONArray jsonLista = (JSONArray) parser.parse(resultado);

                                restaurantesNombres = new String[jsonLista.size()];
                                restaurantesTelefono = new int[jsonLista.size()];
                                restaurantesImg = new int[jsonLista.size()];
                                restaurantesTipo = new String[jsonLista.size()];
                                restauranteUrl = new String[jsonLista.size()];
                                restauranteID = new int[jsonLista.size()];
                                jsonsLugar = new org.json.JSONObject[jsonLista.size()];

                                for (int i = 0; i < jsonLista.size(); i++) {
                                    JSONObject json = (JSONObject) jsonLista.get(i);

                                    // SE AÑADE LA INFORMACIÓN OBTENIDA DEL SELECT A LOS ARRAYS PARA EL LISTVIEW PERSONALIZADO.
                                    restauranteID[i] = Integer.parseInt((String) json.get("id"));
                                    restaurantesNombres[i] = (String) json.get("nombre");
                                    restaurantesTelefono[i] = Integer.parseInt((String) json.get("telefono"));
                                    restauranteUrl[i] = (String) json.get("url");

                                    org.json.JSONObject jsonObject = new org.json.JSONObject();
                                    jsonObject.put("latitud", (String) json.get("latitud"));
                                    jsonObject.put("longitud", (String) json.get("longitud"));
                                    jsonsLugar[i] = jsonObject;

                                    // EL "tipoRest" DETERMINA EL TIPO DE RESTAURANTE QUE ES PARA AÑADIRLE EL ICONO.
                                    int tipoRest = Integer.parseInt((String) json.get("tipo"));

                                    if (tipoRest == 0) {
                                        restaurantesImg[i] = R.drawable.ic_comida_rapida_36dp;
                                        restaurantesTipo[i] = getResources().getString(R.string.restaurante_info_comidaRapida);
                                    } else if (tipoRest == 1) {
                                        restaurantesImg[i] = R.drawable.ic_comida_restaurante_normal_36dp;
                                        restaurantesTipo[i] = getResources().getString(R.string.restautante_info_comidaRestaurante);
                                    } else {
                                        restaurantesImg[i] = R.drawable.ic_comida_bar_36dp;
                                        restaurantesTipo[i] = getResources().getString(R.string.restaurante_info_comidaBar);
                                    }
                                }

                                // SE CREA EL ADAPTER Y SE AÑADE AL LISTVIEW PERSONALIZADO
                                ListView restaurantes = (ListView) getView().findViewById(R.id.restaurantes_lista);
                                AdapterListViewRestaurantes adapter = new AdapterListViewRestaurantes(
                                        getActivity().getApplicationContext(), restaurantesNombres, restaurantesImg, restaurantesTelefono, restaurantesTipo);
                                restaurantes.setAdapter(adapter);

                                restaurantes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int posicion, long id) {
                                        listenerListaFragment.seleccionarElemento(
                                                restaurantesNombres[posicion],
                                                restaurantesTelefono[posicion],
                                                restauranteUrl[posicion],
                                                restaurantesTipo[posicion],
                                                restaurantesImg[posicion],
                                                restauranteID[posicion],
                                                jsonsLugar[posicion]);
                                    }
                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listenerListaFragment = (ListenerListaFragment) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  Se comprueban los permisos. Si hay algún permiso que no se ha aceptado entonces se manda la petición de
     *  que se acepten.
     *  @return Si no se aceptan los permisos entonces devolverá true, sino false.
     */
    private boolean comprobarPermisos(){

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(getActivity(),getResources().getText(R.string.permisos),Toast.LENGTH_LONG).show();
            } else {
                Intent i = new Intent();
                i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                i.setData(uri);
                startActivity(i);
            }
            ActivityCompat.requestPermissions(getActivity(), new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }else{
            return true;
        }
        return false;
    }

}
