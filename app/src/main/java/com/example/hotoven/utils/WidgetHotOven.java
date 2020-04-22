package com.example.hotoven.utils;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.hotoven.R;
import com.example.hotoven.bdd_hotOven.TablasSQLite;
import com.example.hotoven.bdd_hotOven.Usuario;
import com.example.hotoven.workers.WorkerListaPedidos;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of App Widget functionality.
 */
public class WidgetHotOven extends AppWidgetProvider {



    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {


        //SE RECOGE EL ULTIMO USUARIO INICIADO SESION Y SE AÑADE AL WIDGET
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String usuario = pref.getString("name","");
        RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget_hot_oven);
        //views.setTextViewText(R.id.appwidget_text, widgetText);
        if(usuario.equals("")){
            views.setTextViewText(R.id.widget_usuarioIniciado,context.getResources().getString(R.string.widget_usuarioNoIniciado));
        }else{
            views.setTextViewText(R.id.widget_usuarioIniciado,usuario);
        }
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        //SE RECOGE EL ULTIMO USUARIO INICIADO SESION Y SE AÑADE AL WIDGET
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String usuario = pref.getString("usuario","");
        RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget_hot_oven);
        //views.setTextViewText(R.id.appwidget_text, widgetText);
        if(usuario.equals("")){
            views.setTextViewText(R.id.widget_usuarioIniciado,context.getResources().getString(R.string.widget_usuarioNoIniciado));
        }else{
            views.setTextViewText(R.id.widget_usuarioIniciado,usuario);
        }
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


}

