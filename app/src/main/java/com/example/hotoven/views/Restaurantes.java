package com.example.hotoven.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.example.hotoven.R;

import com.example.hotoven.bdd_hotOven.Usuario;
import com.example.hotoven.fragmentos.FragmentoListaRestaurantes;
import com.example.hotoven.fragmentos.FragmentoRestauranteInformacion;

import org.json.JSONObject;

import java.util.Locale;


public class Restaurantes extends AppCompatActivity implements FragmentoListaRestaurantes.ListenerListaFragment {

    private SharedPreferences sharedPreferences;
    private String idiomaEstablecido;

    private Usuario usuario;

    /**
     * Se establece el idioma en la aplicación de SharedPreferences.
     * Crea la actividad de la lista de restaurantes.
     * Obtiene los datos del usuario gracias al bundle y crea el objeto Usuario
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

        setContentView(R.layout.activity_restaurantes);

        Bundle bundle = getIntent().getExtras();
        if(bundle !=null){
            usuario = new Usuario(bundle.getString("usuarioNombre"));
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
     * Muestra los datos del restaurante pulsado de la lista. Si el móvil está en vertical se creará
     * una actividad nueva y se le pasará en el intent el nombre del restaurante y el teléfono. Si
     * está en horizontal entonces ejecutará una función que hará que muestre los datos del restaurante.
     * @param titulo: el nombre del restaurante
     * @param telefono: el teléfono del restaurante
     * @param url: url del restaurante
     * @param tipo: tipo de restaurante
     * @param imagen: imagen del restaurante
     * @param id: id del restaurante
     * @param jsonLugar: json que contiene la longitud y latitud del restaurante
     */
    @Override
    public void seleccionarElemento(String titulo, int telefono, String url, String tipo, int imagen, int id, JSONObject jsonLugar) {

        if( getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            FragmentoRestauranteInformacion restauranteInformacion = (FragmentoRestauranteInformacion)
                    getSupportFragmentManager().findFragmentById(R.id.fragmento_restaurante_info);
            try{
                String latitud = (String) jsonLugar.get("latitud");
                String longitud = (String) jsonLugar.get("longitud");
                restauranteInformacion.cargarDatos(titulo,telefono,usuario.getUsuario(),url,tipo,imagen,id,
                        Double.valueOf(latitud),Double.valueOf(longitud));

            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            Intent intent_RInfo= new Intent(this,RestauranteInformacion.class);
            intent_RInfo.putExtra("nombreRestaurante",titulo);
            intent_RInfo.putExtra("telefonoRestaurante",telefono);
            intent_RInfo.putExtra("nombreUsuario",usuario.getUsuario());
            intent_RInfo.putExtra("urlRestaurante",url);
            intent_RInfo.putExtra("tipoRestaurante",tipo);
            intent_RInfo.putExtra("imagenRestaurante",imagen);
            intent_RInfo.putExtra("id",id);
            try{
                String latitud = (String) jsonLugar.get("latitud");
                String longitud = (String) jsonLugar.get("longitud");
                intent_RInfo.putExtra("latitud",latitud);
                intent_RInfo.putExtra("longitud",longitud);
            }catch (Exception e){
                e.printStackTrace();
            }
            startActivity(intent_RInfo);
        }
    }
}
