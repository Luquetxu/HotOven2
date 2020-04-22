package com.example.hotoven.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.hotoven.utils.GeneradorConexionesSeguras;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.net.ssl.HttpsURLConnection;

public class WorkerListaRestaurantes extends Worker {

    public WorkerListaRestaurantes(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    /**
     * Workerq que obtiene los restaurantes en formato JSON
     * @return: los restaurantes en JSON.
     */
    @NonNull
    @Override
    public Result doWork() {

        GeneradorConexionesSeguras con = GeneradorConexionesSeguras.getInstance();
        HttpsURLConnection urlconection = con.crearConexionSegura(getApplicationContext(),
                "https://134.209.235.115/uluque001/WEB/dbGetListaRestaurantes.php");

        Data.Builder data = new Data.Builder();

        try {
            int code = urlconection.getResponseCode();

            if (code == 200) {
                BufferedInputStream inputStream = new BufferedInputStream(urlconection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line, result = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                inputStream.close();

                data.putString("resultado", result);
            } else {
                data.putString("resultado", "Error");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Result.success(data.build());
    }
}
