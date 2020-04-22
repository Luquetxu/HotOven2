package com.example.hotoven.workers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.hotoven.otros.AlmacenamientoDatos;
import com.example.hotoven.utils.GeneradorConexionesSeguras;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.net.ssl.HttpsURLConnection;

public class WorkerObtenerFoto extends Worker {

    public WorkerObtenerFoto(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }


    @NonNull
    @Override
    public Result doWork() {

        Data.Builder builder = new Data.Builder();
        String usuario = getInputData().getString("usuario");

        GeneradorConexionesSeguras con = GeneradorConexionesSeguras.getInstance();
        HttpsURLConnection urlconection = con.crearConexionSegura(getApplicationContext(),
                "https://134.209.235.115/uluque001/WEB/dbObtenerFoto.php");

        JSONObject parametros = new JSONObject();
        try {
            parametros.put("usuario", usuario);

            urlconection.setRequestProperty("Content-Type", "application/json");
            urlconection.setRequestMethod("POST");
            urlconection.setDoOutput(true);

            PrintWriter printWriter = new PrintWriter(urlconection.getOutputStream());
            printWriter.print(parametros.toString());
            printWriter.close();

            int code = urlconection.getResponseCode();

            if (code == 200) {
                Bitmap bitmap = BitmapFactory.decodeStream(urlconection.getInputStream());

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,0,stream);
                byte[] bytes = stream.toByteArray();
                String foto64 = Base64.encodeToString(bytes,Base64.DEFAULT);
                //builder.putString("foto",foto64);

                AlmacenamientoDatos.getAm().setString64(foto64);

            } else {
                Log.i("Foto","JJJJJJJJJJJJJJJ");

                Log.i("BD","ERROR DELETE");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Result.success(builder.build());
    }
}
