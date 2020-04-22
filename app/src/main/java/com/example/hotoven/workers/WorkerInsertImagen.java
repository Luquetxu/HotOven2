package com.example.hotoven.workers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.hotoven.bdd_hotOven.TablasSQLite;
import com.example.hotoven.utils.GeneradorConexionesSeguras;

import org.json.simple.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class WorkerInsertImagen extends Worker {
    public WorkerInsertImagen(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    /**
     * Worker que añade el path y el tipo de la imagen en la BD
     * @return
     */
    @NonNull
    @Override
    public Result doWork() {

        String usuario = getInputData().getString(TablasSQLite.USUARIOS_USUARIO);
        //String foto = getInputData().getString("fotoPerfil");

        boolean esGaleria = getInputData().getBoolean("esGaleria",false);
        String path = getInputData().getString("path");

        String foto64="";
        Bitmap bitmap=null;
        if(esGaleria){
            try{
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(path));
            }catch (Exception e){
                Log.i("K","KKK");
            }

        }else{
            bitmap = BitmapFactory.decodeFile(path);
        }

        bitmap = Bitmap.createScaledBitmap(bitmap,100,100,true);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,0,stream);
        byte[] bytes = stream.toByteArray();
        foto64 = Base64.encodeToString(bytes,Base64.DEFAULT);

        GeneradorConexionesSeguras con = GeneradorConexionesSeguras.getInstance();
        HttpsURLConnection urlconection = con.crearConexionSegura(getApplicationContext(),
                "https://134.209.235.115/uluque001/WEB/dbInsertImage.php");

        HashMap<String,Object> a = new HashMap<>();

        a.put("usuario",usuario);
        //a.put("tipoImagen",tipo);
        //a.put("pathImagen",path);
        a.put("foto",foto64);
        JSONObject parametros = new JSONObject(a);
        try {
            urlconection.setRequestProperty("Content-Type", "application/json");
            urlconection.setRequestMethod("POST");
            urlconection.setDoOutput(true);

            PrintWriter printWriter = new PrintWriter(urlconection.getOutputStream());
            printWriter.print(parametros.toString());
            printWriter.close();

            int code = urlconection.getResponseCode();

            if (code == 200) {
                BufferedInputStream inputStream= new BufferedInputStream(urlconection.getInputStream());
                BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line, result="";
                while((line = bufferedReader.readLine()) != null){
                    result+= line;
                }
                inputStream.close();
                Log.i("BDD",result+"");
            } else {
                Log.i("BDD",code+"");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
