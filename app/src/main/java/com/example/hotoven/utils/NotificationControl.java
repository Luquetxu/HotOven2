package com.example.hotoven.utils;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.hotoven.MainActivity;
import com.example.hotoven.R;
import com.example.hotoven.bdd_hotOven.BD_HotOven;
import com.example.hotoven.bdd_hotOven.TablasSQLite;
import com.example.hotoven.views.MenuPrincipal;
import com.example.hotoven.workers.WorkerDeletePedido;
import com.example.hotoven.workers.WorkerInicioSesion;

import java.util.concurrent.TimeUnit;

/**
 * Se controla la acción pulsada de la notificación.
 */
public class NotificationControl extends BroadcastReceiver {

    /**
     * Si se pulsa el boton de confirmar pedido de la notificación no hace nada, pero si se pulsa el
     * de cancelar pedido borra el pedido realizado. Al pulsar cualquier de las dos acciones para la
     * notificación
     * @param context el contexto
     * @param intent la intención al pulsar la acción de la notificación
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String action = intent.getAction();

        Bundle bundle = intent.getExtras();
        if(bundle !=null){
            // DESACTIVA LA NOTIFICACIÓN
            int idNoti = bundle.getInt("idNoti");
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(idNoti);
        }


        if(action.equals("CONFIRMAR")){
            // SE SE PULSA EN LA NOTIFICACIÓN "CONFIRMAR"
            Log.i("noti","Confimar");
        }else if(action.equals("RECHAZAR")){
            // SE SE PULSA EN LA NOTIFICACIÓN "RECHAZAR"

            // SE BORRA EL PEDIDO
            int pedido = bundle.getInt("idPedido",0);

            Data.Builder data = new Data.Builder();
            data.putInt("pedido",pedido);

            OneTimeWorkRequest trabajoPuntual = new OneTimeWorkRequest.Builder(WorkerDeletePedido.class)
                    .setInitialDelay(0, TimeUnit.SECONDS)
                    .setInputData(data.build())
                    .build();
            WorkManager.getInstance(context).enqueue(trabajoPuntual);

        }
    }
}
