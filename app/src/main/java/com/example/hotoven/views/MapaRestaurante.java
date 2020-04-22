package com.example.hotoven.views;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.hotoven.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;


public class MapaRestaurante extends FragmentActivity implements OnMapReadyCallback {

    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    private Spinner mMapTypeSelector;
    private int[] mMapTypes;
    private double longitud;
    private double latitud;
    private String titulo;
    private int tipoRes;
    private int imagen;

    /**
     * Se cargan los datos y los listeners y prepara el mapa para mostrarlo
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_restaurante);

        Bundle bundle = getIntent().getExtras();
        if(bundle !=null){
            longitud = bundle.getDouble("longitud");
            latitud = bundle.getDouble("latitud");
            tipoRes = bundle.getInt("tipoRestaurante");
            titulo = bundle.getString("nombreRestaurante");
            imagen = bundle.getInt("imagenRestaurante");
        }

        TextView nombreRestaurante = (TextView) findViewById(R.id.mapaRestauranteNombre);

        nombreRestaurante.setText(titulo);
        nombreRestaurante.setCompoundDrawablesWithIntrinsicBounds(imagen,0,0,0);

        mMapTypeSelector = (Spinner) findViewById(R.id.map_type_selector);


        // TIPO DE MAPA DISPONIBLE
        mMapTypes = new int[4];
        mMapTypes[0] = GoogleMap.MAP_TYPE_NORMAL;
        mMapTypes[1] = GoogleMap.MAP_TYPE_SATELLITE;
        mMapTypes[2] = GoogleMap.MAP_TYPE_HYBRID;
        mMapTypes[3] = GoogleMap.MAP_TYPE_TERRAIN;

        mMapTypeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try{
                    mMap.setMapType(mMapTypes[i]);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        // SI LA HORA ES ENTRE 21:00 - 08:00 EL MAPA NORMAL ESTÁ EN MODO NOCTURNO
        Calendar calendar = Calendar.getInstance();
        if((calendar.get(Calendar.HOUR_OF_DAY) <=21) && (calendar.get(Calendar.HOUR_OF_DAY) >=8)){
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.estilo_mapadia));
        }else{
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.estilo_mapanoche));
        }

        // SE AÑADE EL MARKER DEL RESTAURANTE
        LatLng coordenadasRestaurante = new LatLng(latitud, longitud);
        mMap.addMarker(new MarkerOptions()
                .position(coordenadasRestaurante)
                .title(titulo)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mapa_restaurante))
                .snippet(obtenerTipoRestauranteTexto())
                .draggable(false));

        // SE CREA UN CIRCULO AL REDEDOR DEL RESTAURANTE
        CircleOptions circulo = new CircleOptions()
                .center(new LatLng(latitud,longitud))
                .radius(40)
                .strokeColor(Color.RED)
                .strokeWidth(8)
                .fillColor(Color.argb(200, 33, 150, 243));
        mMap.addCircle(circulo);


        // Controles UI
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(false);

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordenadasRestaurante,15));
    }

    private String obtenerTipoRestauranteTexto(){
        if(tipoRes==0){
            return getResources().getString(R.string.restaurante_info_comidaRapida);
        }else if(tipoRes==1){
            return getResources().getString(R.string.restautante_info_comidaRestaurante);
        }else{
            return getResources().getString(R.string.restaurante_info_comidaBar);
        }
    }


}
