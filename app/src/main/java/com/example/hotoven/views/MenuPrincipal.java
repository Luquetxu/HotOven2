package com.example.hotoven.views;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hotoven.R;
import com.example.hotoven.bdd_hotOven.TablasSQLite;
import com.example.hotoven.bdd_hotOven.Usuario;
import com.example.hotoven.dialogs.DialogAvisoCerrarSesion;
import com.example.hotoven.dialogs.DialogEleccionFoto;
import com.example.hotoven.otros.AlmacenamientoDatos;
import com.example.hotoven.utils.ElAdaptadorRecycler;
import com.example.hotoven.workers.WorkerInsertImagen;
import com.example.hotoven.workers.WorkerNotificacion;
import com.example.hotoven.workers.WorkerObtenerFoto;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MenuPrincipal extends AppCompatActivity implements DialogEleccionFoto.ListenerFoto {

    private RecyclerView lalista;
    private SharedPreferences sharedPreferences;
    private Usuario usuario;
    private TextView textoUsuario;
    private String idiomaEstablecido;
    private ImageView fotoPerfil;

    private DialogAvisoCerrarSesion avisoCerrarSesion;
    private DialogEleccionFoto dialogoFoto;

    private final String CARPETA="misImagenes/";
    private final String RUTA_IMAGEN=CARPETA+"misFotos";

    private String path;
    private int tipoImagen;

    private Bitmap bitmapFoto;
    private String foto64;

    /**
     * Se establece el idioma en la aplicación de SharedPreferences.
     * Crea la actividad.
     * Se crea el usuario con los datos obtenidos del bundle.
     * Se inicializan los atributos.
     * Se crea el Recycler View mediante CardViews, así creando el menú principal.
     * Comprueba si existe una foto en sharedPreferences, y si lo existe lo añade.
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

        setContentView(R.layout.activity_menu_principal);

        Bundle bundle = getIntent().getExtras();
        if(bundle !=null){
            if(usuario==null){
                usuario = new Usuario(bundle.getString("usuarioIniciado"));

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name",bundle.getString("usuarioIniciado"));
                editor.apply();
            }
        }


        fotoPerfil = (ImageView) findViewById(R.id.menuPrincipal_fotoPerfil);
        textoUsuario = (TextView) findViewById(R.id.menuPrincipalUsuario);
        textoUsuario.setText(usuario.getUsuario());
        lalista = (RecyclerView) findViewById(R.id.mp_recycler_view);

        int[] menus = {
                R.drawable.ic_restaurant_,
                R.drawable.ic_pedido_100dp,
                R.drawable.ic_ajustes_100dp,
                R.drawable.ic_info_100dp};
        String[] titulos = {
                getResources().getString(R.string.menu_restaurantes),
                getResources().getString(R.string.menu_pedidosRealizados),
                getResources().getString(R.string.menu_ajustes),
                getResources().getString(R.string.menu_instrucciones)};

        // SE INICIALIZA EL RECLYCLER VIEW CON LAS IMAGENES Y TITULOS
        ElAdaptadorRecycler elAdaptadorRecycler = new ElAdaptadorRecycler(titulos,menus,getSupportFragmentManager(),MenuPrincipal.this,usuario);
        lalista.setAdapter(elAdaptadorRecycler);

        GridLayoutManager elGridLayoutManager = new GridLayoutManager(
                this,2,GridLayoutManager.VERTICAL,false);
        lalista.setLayoutManager(elGridLayoutManager);


       // WORKER NOTIFICACION A TODOS USUARIOS(FCM)
        OneTimeWorkRequest trabajoPuntual = new OneTimeWorkRequest.Builder(WorkerNotificacion.class)
                .setInitialDelay(0, TimeUnit.SECONDS)
                .build();
        WorkManager.getInstance(this).enqueue(trabajoPuntual);


        // WORKER OBTENER FOTO
        Data.Builder data = new Data.Builder();
        data.put("usuario",usuario.getUsuario());

        OneTimeWorkRequest trabajo2 = new OneTimeWorkRequest.Builder(WorkerObtenerFoto.class)
                .setInitialDelay(0, TimeUnit.SECONDS)
                .setInputData(data.build())
                .build();
        WorkManager.getInstance(this).enqueue(trabajo2);

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(trabajo2.getId())
                .observe( this,new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null&& workInfo.getState().isFinished()) {
                            Data data = workInfo.getOutputData();

                            //String foto = data.getString("foto");
                            String foto = AlmacenamientoDatos.getAm().getString64();
                            if(foto !=null){
                                byte[] bytes = Base64.decode(foto, Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                fotoPerfil.setImageBitmap(bitmap);
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
        gestionarPreferencias();

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
     * Se inicia el diálogo de aviso para cerrar sesión. Si el usuario elige cerrar sesión, se vuelve
     * al menú de inicio de sesión, sino se vuelve al menú principal
     * @param view: El ImageView que está situado en la parte superior izquierda
     */
    public void cerrarSesion(View view){
        if(avisoCerrarSesion ==null){
            avisoCerrarSesion = new DialogAvisoCerrarSesion();
        }
        avisoCerrarSesion.show(getSupportFragmentManager(),"cerrarSesion");
    }

    /**
     * Se comprueba en el SharedPreferences el color establecido para el menú y lo cambia. También se comprueba
     * si se a modificado el nombre del usuario
     */
    private void gestionarPreferencias(){

        // SE COMPRUEBA EL COLOR DEL MENU Y CAMBIA
        LinearLayout menuPrincipal = (LinearLayout) findViewById(R.id.linearLayoutparteSuperior_MenuPrincipal);
        String color = sharedPreferences.getString("colormenu","");

        if(color.equals("") || color.equals("blanco")){
            menuPrincipal.setBackgroundColor(getResources().getColor(R.color.fondoPorDefecto));
        }else if(color.equals("azul")){
            menuPrincipal.setBackgroundColor(getResources().getColor(R.color.fondo_menuPrincipal_azul));
        }else if(color.equals("verde")){
            menuPrincipal.setBackgroundColor(getResources().getColor(R.color.fondo_menuPrincipal_verde));
        }else if(color.equals("rojo")){
            menuPrincipal.setBackgroundColor(getResources().getColor(R.color.fondo_menuPrincipal_rojo));
        }

        // SI SE HA MODIFICADO EL NOMBRE DEL USUARIO EN AJUSTES
        String nombreUsuario = sharedPreferences.getString("name",usuario.getUsuario());
        if(!nombreUsuario.equals(usuario.getUsuario())){
            usuario.setUsuario(nombreUsuario);
            textoUsuario.setText(usuario.getUsuario());
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
     * Se crea y se inicia el dialogo para elegir para añadir la foto
     * @param view
     */
    public void anadirFoto(View view){
        if(dialogoFoto ==null){
            dialogoFoto = new DialogEleccionFoto();
        }
        boolean permisos = validaPermisos();

        if(permisos){
            dialogoFoto.show(getSupportFragmentManager(),"ventanaFoto");
        }
    }

    /**
     * Después de acabar la intención se ejecuta este método. Si es correcto y el código es 80 entonces
     * se considerará que la foto es sacada de la galería y almacena la uri en sharespreferences,
     * y si es 81 será considerado como captura de foto y se guardará en sharespreferences el path.
     * Los dos añaden la foto en la ImageView.
     * @param requestCode el código de la intención
     * @param resultCode el resultado
     * @param data la intención
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){

            switch (requestCode){
                case 80:
                    // FOTO DE LA GALERIA
                    try{
                        Uri miPath  = data.getData();

                        anadirFotoGaleria(miPath.toString());
                        path = miPath.toString();
                        insertImageBD(true);
                    }catch(Exception e){
                        //Toast.makeText(getApplicationContext(),"ERROR",Toast.LENGTH_LONG).show();
                    }
                    break;
                case 81:
                    //FOTO DE LA CAMARA
                    MediaScannerConnection.scanFile(this, new String[]{path}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String s, Uri uri) {
                                    Log.i("Ruta almacenamiento","Path:"+path);
                                }
                            });
                    anadirCapturaFoto(path);

                    tipoImagen = 0;
                    insertImageBD(false);

                    break;
            }

        }
    }

    /**
     * Método a ejecutar cuando es elegido la función de elegir una foto de la galeria. Se realiza una
     * intención y te da la posibilidad de escoger una aplicación para coger una foto.
     */
    @Override
    public void galeria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        startActivityForResult(Intent.createChooser(intent, getResources().getText(R.string.dialog_foto_seleccionar)), 80);
    }

    /**
     * Método a ejecutar cuando es elegido la función de capturar una foto. Primero se comprueban los permisos.
     * Si tiene los permisos adecuados entonces se comprobará si existe la carpeta donde irán las imágenes, si
     * no existe la carpeta se crea. Después se manda una intención para que se haga una foto y se espera a que
     * se finalice la intención.
     */
    @Override
    public void capturarFoto() {

        boolean permisos = validaPermisos();

        // SI LOS PERMISOS ESTAN ACTIVADOS SE SACA LA FOTO
        if(permisos) {
            File fileImagen = new File(Environment.getExternalStorageDirectory(), RUTA_IMAGEN);
            if (fileImagen.exists() == false) {
                boolean a = fileImagen.mkdirs();
            }
            String nombreImg = "";
            if (fileImagen.exists()) {
                nombreImg = (System.currentTimeMillis() / 100) + ".jpg";

                path = Environment.getExternalStorageDirectory() + File.separator + RUTA_IMAGEN + File.separator + nombreImg;
                File imagen = new File(path);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    String authorities = getApplicationContext().getPackageName() + ".provider";
                    Uri imageUri = FileProvider.getUriForFile(this, authorities, imagen);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                } else {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagen));
                }
                startActivityForResult(intent, 81);
            }
        }
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

    /**
     * Método a ejecutar cuando es elegido la función de no cambiar de foto. Se cierra el diálogo.
     */
    @Override
    public void cancelar() {
        dialogoFoto.dismiss();
    }

    /**
     * Se añade la captura de foto reescalado al ImageView
     * @param pathFoto el path de la foto sacada
     */
    private void anadirCapturaFoto(String pathFoto){
        Bitmap bitmapCaptura = BitmapFactory.decodeFile(pathFoto);
        bitmapFoto = Bitmap.createScaledBitmap(bitmapCaptura,100,100,true);
        fotoPerfil.setImageBitmap(bitmapFoto);
    }

    /**
     * Se añade la foto de la galeria rescalada al ImageView
     * @param uri la uri de la imagen
     */
    private void anadirFotoGaleria(String uri){
        try{
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),Uri.parse(uri));
            bitmapFoto = Bitmap.createScaledBitmap(bitmap,100,100,true);
            fotoPerfil.setImageBitmap(bitmapFoto);
        }catch (Exception e){
           // Toast.makeText(getApplicationContext(),"ERROR",Toast.LENGTH_LONG).show();
            //e.printStackTrace();
        }
    }

    /**
     * Se inserta el path de la imagen en la BD remota y si es sacado de la galeria o de la cámara.
     */
    private void insertImageBD(boolean esGaleria){

        Data.Builder data = new Data.Builder();
        data.putString(TablasSQLite.USUARIOS_USUARIO,usuario.getUsuario());
        //data.putString("fotoPerfil",foto64);
        data.putBoolean("esGaleria",esGaleria);
        data.putString("path",path);

        OneTimeWorkRequest trabajoPuntual = new OneTimeWorkRequest.Builder(WorkerInsertImagen.class)
                .setInitialDelay(0, TimeUnit.SECONDS)
                .setInputData(data.build())
                .build();
        WorkManager.getInstance(this).enqueue(trabajoPuntual);
    }
}