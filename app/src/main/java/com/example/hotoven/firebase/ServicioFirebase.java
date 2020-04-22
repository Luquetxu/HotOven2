package com.example.hotoven.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.hotoven.MainActivity;
import com.example.hotoven.R;
import com.example.hotoven.utils.GestorNotificacion;
import com.example.hotoven.utils.NotificationControl;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class ServicioFirebase extends FirebaseMessagingService {

    public ServicioFirebase(){

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getData().size()>0){

            //Log.i("FCM", mensaje+ titulo);
            //GestorNotificacion.getGestorNotificacion().notificacionFirebase(titulo,mensaje,getApplicationContext());
        }

        if(remoteMessage.getNotification() !=null){
            Log.i("FCM",remoteMessage.getNotification().getBody()+"");

            RemoteMessage.Notification notification = remoteMessage.getNotification();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 78777, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            NotificationCompat.Builder builder;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                builder = new NotificationCompat.Builder(getApplicationContext(),"Pedidos");
                builder.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.mipmap.ic_launcher));
            }else{
                builder = new NotificationCompat.Builder(getApplicationContext());
                builder.setPriority(NotificationCompat.PRIORITY_HIGH);
            }

            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            builder.setContentTitle(notification.getTitle());
            builder.setContentText(notification.getBody());
            builder.setSmallIcon(R.drawable.ic_pedido_en_camino);
            builder.setAutoCancel(true);
            builder.setSound(defaultSoundUri);
            builder.setContentIntent(pendingIntent);
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(notification.getBody()));

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(6859, builder.build());

        }
    }

}
