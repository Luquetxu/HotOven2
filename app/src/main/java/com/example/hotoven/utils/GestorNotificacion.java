package com.example.hotoven.utils;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.hotoven.R;
import com.example.hotoven.views.Pedidos;

/**
 * Singleton del gestor de notificaciones
 */
public class GestorNotificacion {

    private static NotificationManager notificationManager;
    private Activity activity;
    private static GestorNotificacion gestorNotificacion;

    private GestorNotificacion() {

    }

    /**
     * Cambia el contexto para cuando se tiene que ejecutar una notificación en distintas actividades
     * @param pActivity actividad
     */
    public void changeContext(Activity pActivity) {
        if (!pActivity.equals(activity)) {
            activity = pActivity;
            notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        }
    }

    /**
     * Devuelve el atributo del GestorNotification
     * @return gestorNotificacion
     */
    public static GestorNotificacion getGestorNotificacion() {
        if (gestorNotificacion == null) {
            gestorNotificacion = new GestorNotificacion();
        }
        return gestorNotificacion;
    }

    /**
     * Se crea el canal de la notificación "Pedidos" solo para los móviles con versión de android Oreo o superior
     */
    public void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = activity.getResources().getString(R.string.notificacionCanal);
            String descripcion = activity.getResources().getString(R.string.notificacion_canal_descripcion);
            int importancia = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("Pedidos",name,importancia);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.setVibrationPattern(new long[]{0, 1000});
            channel.enableVibration(true);
            channel.setDescription(descripcion);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Se crea e inicia la notificación para cuando se realiza un pedido.
     * @param precioTotal el precio del pedido
     * @param infoExtra el pedido
     * @param usuario nombre del usuario
     * @param idPedido id del pedido
     */
    public void notificacionPedidoCompletado(int precioTotal,String infoExtra,String usuario,int idPedido){
        NotificationCompat.Builder builder;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            builder = new NotificationCompat.Builder(activity,"Pedidos");
            builder.setLargeIcon(BitmapFactory.decodeResource(activity.getResources(),R.mipmap.ic_launcher));
        }else{
            builder = new NotificationCompat.Builder(activity);
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        // Intent de que al pulsar la notificación te mande directamente a la actividad de Pedidos.
        Intent intent = new Intent(activity, Pedidos.class);
        intent.putExtra("usuarioNombre",usuario);
        PendingIntent pendingIntent = PendingIntent.getActivity(activity,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        //Intent de que al pulsar la acción de 'Confirmar Pedido' se ejecuta el BroadCast de la clase NotificacionControl
        Intent confirm = new Intent();
        confirm.setAction("CONFIRMAR");
        confirm.putExtra("idNoti",69);
        PendingIntent pendingIntentConfirm = PendingIntent.getBroadcast(activity, 70, confirm, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(android.R.drawable.ic_input_add, activity.getResources().getString(R.string.notificacion_confirmar_pedido), pendingIntentConfirm);

        //Intent de que al pulsar la acción de 'Rechazar Pedido' se ejecuta el BroadCast de la clase NotificacionControl
        Intent rechazar = new Intent();
        rechazar.putExtra("idPedido",idPedido);
        rechazar.putExtra("idNoti",69);
        rechazar.setAction("RECHAZAR");
        PendingIntent pendingIntentRechazar = PendingIntent.getBroadcast(activity, 71, rechazar, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(android.R.drawable.ic_menu_delete, activity.getResources().getString(R.string.notificacion_rechazar_pedido), pendingIntentRechazar);


        //Se prepara la notificación con el título, sub texto y el texto expandible...
        builder.setContentTitle(activity.getResources().getString(R.string.notificacion_pedido_realizado));
        builder.setContentText(activity.getResources().getString(R.string.notificacion_importe1));
        builder.setSmallIcon(R.drawable.ic_pedido_en_camino);
        String notificacionExpandible = infoExtra + "\n\n" + activity.getResources().getString(R.string.notificacion_textoExpandible) + precioTotal+"€";
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(notificacionExpandible));
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        notificationManager.notify(69,builder.build());
    }

}
