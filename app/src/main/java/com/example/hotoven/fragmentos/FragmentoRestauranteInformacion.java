package com.example.hotoven.fragmentos;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.provider.Settings;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hotoven.R;
import com.example.hotoven.bdd_hotOven.BD_HotOven;
import com.example.hotoven.bdd_hotOven.TablasSQLite;
import com.example.hotoven.dialogs.DialogRealizarPedido;
import com.example.hotoven.utils.GestorNotificacion;
import com.example.hotoven.views.MapaRestaurante;
import com.example.hotoven.views.RealizarPedidoAct;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FragmentoRestauranteInformacion extends Fragment {

    private TextView tipo;
    private TextView titulo;
    private TextView telefono;
    private TextView uri;
    private String nombreUsuario;
    private int imagen;
    private int idRestaurante;

    private double latitud;
    private double longitud;

    private Button btnRealizarPedido;

    private View view;

    private Button verMapa;

    //private DialogRealizarPedido dialogRealizarPedido;

    /**
     * Al crear el fragmento se inicializan los atributos
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fragmento_restaurante_informacion, container, false);

        tipo = (TextView) view.findViewById(R.id.restaurante_info_tipoComida);
        titulo = (TextView) view.findViewById(R.id.restaurante_info_titulo);
        telefono = (TextView) view.findViewById(R.id.restaurante_info_telefonoRestaurante);
        uri = (TextView) view.findViewById(R.id.restaurante_info_urlRestaurante);

        asignarListeners(view);

        return view;
    }

    /**
     * Se asignan los listeners de la fila de la tabla del teléfono y de pulsar el botón de realizar
     * pedido
     * @param view
     */
    private void asignarListeners(View view) {
        TableRow tabla_telefono = (TableRow) view.findViewById(R.id.restauranteInfo_tabla_telefono);
        tabla_telefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llamarTelefono();
            }
        });

        btnRealizarPedido = (Button) view.findViewById(R.id.btnRealizarPedido);
        btnRealizarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), RealizarPedidoAct.class);
                intent.putExtra("tituloRes",titulo.getText().toString());
                intent.putExtra("nombreUsuario",nombreUsuario);
                intent.putExtra("id",idRestaurante);
                startActivity(intent);

            }
        });
        verMapa = (Button) view.findViewById(R.id.btnMapa);
        verMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verMapa();
            }
        });

    }


    /**
     * Inicializa los datos que serán importantes para la notificación y añade
     * la información a los EditTexts del layout del diálogo
     * @param restaurante nombre del restaurante
     * @param telefonoRestaurante telefono del restaurante
     * @param nombreUsuario nombre del usuario
     * @param url: url del restaurante
     * @param tipo: tipo de restaurante
     * @param imagen: imagen del restaurante
     * @param id: id del restaurante
     * @param latitud: latitud del restaurante
     * @param longitud: longitud del restaurante
     */
    public void cargarDatos(String restaurante, int telefonoRestaurante, String nombreUsuario, String url,String tipo, int imagen,int id,
                            double latitud, double longitud) {
        titulo.setText(subrayarTitulo(restaurante));
        titulo.setCompoundDrawablesWithIntrinsicBounds(
                imagen, 0, 0, 0);
        telefono.setText(telefonoRestaurante + "");
        this.nombreUsuario = nombreUsuario;

        this.uri.setText(url);
        asignarURL(url);

        this.tipo.setText(tipo);
        this.imagen = imagen;

        this.idRestaurante = id;

        this.latitud = latitud;
        this.longitud = longitud;
    }

    /**
     * Añade el texto del tipo de resurante que es al Edit Text y el icono al título dependiendo del
     * tipo de restaunte que sea. También se añade la URL en el EditText obteniendo la URL del
     * fichero de texto.
     *//*
    private void anadirInfoTipoRestaurante() {

        //---------TIPO DE RESTAURANTE
         tipoRes = obtenerTipoRestaurante();

        if (tipoRes == 0) {
            tipo.setText(R.string.restaurante_info_comidaRapida);
            titulo.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_comida_rapida_36dp, 0, 0, 0);
        } else if (tipoRes == 1) {
            tipo.setText(R.string.restautante_info_comidaRestaurante);
            titulo.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_comida_restaurante_normal_36dp, 0, 0, 0);
        } else {
            tipo.setText(R.string.restaurante_info_comidaBar);
            titulo.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_comida_bar_36dp, 0, 0, 0);
        }

    /**
     * Se subraya el texto del título
     * @param titulo el nombre del restaurante
     * @return el nombre del restaurante subrayado
     */
    //https://es.stackoverflow.com/questions/32572/agregar-una-l%C3%ADnea-debajo-de-un-textview-xml
    private SpannableString subrayarTitulo(String titulo) {
        SpannableString spannableString = new SpannableString(titulo);
        spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), 0);
        return spannableString;
    }


    /**
     * Se crea el intent para realizar una llamada. Se comprueba si no se ha aceptado los permisos de la llamada
     * y si no está entonces te lo pide que aceptes.
     */
    private void llamarTelefono() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CALL_PHONE)) {
                Toast.makeText(getActivity().getApplicationContext(),getActivity().getText(R.string.permisos),Toast.LENGTH_LONG).show();
            } else {
                Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                i.setData(uri);
                startActivity(i);
            }
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 10);
        } else {
            Intent llamada = new Intent(Intent.ACTION_DIAL);
            llamada.setData(Uri.parse("tel:" + telefono.getText().toString()));
            if (llamada.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(llamada);
            }
        }
    }

    /**
     * Se añade la URL al editText si el restaurante contiene página web. Si  tiene página web entonces
     * se crea un evento de que al pulsar se abrirá el navegador con la página web.
     * @param url
     */
    private void asignarURL(String url){
        TableRow tabla_uri = (TableRow) view.findViewById(R.id.restauranteInfo_tabla_url);
        if(!url.equals("-")){
            uri.setText(url);

            tabla_uri.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent navegador = new Intent(Intent.ACTION_VIEW, Uri.parse("https://"+url));
                    startActivity(navegador);
                }
            });
        }else{
            uri.setText(getResources().getString(R.string.restaurante_info_no_hay_url));
            tabla_uri.setOnClickListener(null);
        }

    }


    /**
     * Comprueba si los permisos están permitidos a la cámara, y si lo están se diriga a la actividad
     * del mapa del restaurante
     */
    private void verMapa(){
        if(comprobarPermisos()){
            Intent i = new Intent(getActivity(), MapaRestaurante.class);

            i.putExtra("nombreRestaurante",titulo.getText().toString());
            i.putExtra("tipoRestaurante",tipo.getText().toString());
            i.putExtra("longitud",longitud);
            i.putExtra("latitud",latitud);
            i.putExtra("telefonoRestaurante",Integer.valueOf(telefono.getText().toString()));
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            i.putExtra("nombreUsuario",pref.getString("usuario",""));
            i.putExtra("urlRestaurante",uri.getText().toString());
            i.putExtra("imagenRestaurante",imagen);
            i.putExtra("id",idRestaurante);

            startActivity(i);
        }
    }

    /**
     * Se comprueban los permisos. Si hay algún permiso que no se ha aceptado entonces se manda la petición de
     * que se acepten.
     * @return Si no se aceptan los permisos entonces devolverá true, sino false.
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
