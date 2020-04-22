package com.example.hotoven.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.hotoven.bdd_hotOven.TablasSQLite;
import com.example.hotoven.utils.GeneradorConexionesSeguras;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.net.ssl.HttpsURLConnection;

public class WorkerPedido extends Worker {

    public WorkerPedido(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    /***
     * Worker que añade un pedido a la BD
     * @return: si se ha realizado o no.
     */
    @NonNull
    @Override
    public Result doWork() {
        String usuario = getInputData().getString(TablasSQLite.USUARIOS_USUARIO);
        int idRestaurante = getInputData().getInt("idRestaurante",0);
        int precio = getInputData().getInt("precio",0);

        GeneradorConexionesSeguras con = GeneradorConexionesSeguras.getInstance();
        HttpsURLConnection urlconection = con.crearConexionSegura(getApplicationContext(),
                "https://134.209.235.115/uluque001/WEB/dbPedido.php");

        Data.Builder data = new Data.Builder();

        JSONObject parametros = new JSONObject();
        try {
            parametros.put("usuario",usuario);
            parametros.put("idRestaurante",idRestaurante);
            parametros.put("precio",precio);

            urlconection.setRequestProperty("Content-Type","application/json");
            urlconection.setRequestMethod("POST");
            urlconection.setDoOutput(true);

            PrintWriter printWriter = new PrintWriter(urlconection.getOutputStream());
            printWriter.print(parametros.toString());
            printWriter.close();

            int code = urlconection.getResponseCode();

            if(code == 200){
                BufferedInputStream inputStream= new BufferedInputStream(urlconection.getInputStream());
                BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line, result="";
                while((line = bufferedReader.readLine()) != null){
                    result+= line;
                }
                inputStream.close();

                data.putString("resultado",result);
                Log.i("BD",result);

            }else{
                data.putString("resultado","Error");
            }
            Log.i("BD",code+"");

        } catch (Exception e){
            e.printStackTrace();
        }

        return Result.success(data.build());
    }
}
