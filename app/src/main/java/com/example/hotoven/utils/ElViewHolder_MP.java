package com.example.hotoven.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotoven.R;
import com.example.hotoven.bdd_hotOven.Usuario;
import com.example.hotoven.dialogs.DialogoInstrucciones;
import com.example.hotoven.views.Ajustes;
import com.example.hotoven.views.Pedidos;
import com.example.hotoven.views.Restaurantes;

/**
 * En ElViewHolder_MP se controla la selección de los elementos
 */
public class ElViewHolder_MP extends RecyclerView.ViewHolder {

    public ImageView laimagen;
    public TextView eltexto;
    public int[] seleccion;
    public FragmentManager fragmentManager;
    public Context context;
    //public Activity activity;
    private DialogoInstrucciones dialogoInstrucciones;
    public Usuario usuario;

    /**
     * Se controla la selección de los elementos. Dependiendo de la posición se hará una cosa distinta:
     * 0 --> Se va a la actividad de Restaurantes
     * 1 --> Se va a la actividad de Pedidos
     * 2 --> Se va a la actividad de Ajustes
     * 3 --> Se inicia el diálogo de Información
     * @param itemView el layout
     */
    public ElViewHolder_MP(@NonNull View itemView) {
        super(itemView);
        laimagen = (ImageView) itemView.findViewById(R.id.cardviewFoto);
        eltexto = (TextView) itemView.findViewById(R.id.cardviewTexto);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int posicionPulsada = seleccion[getAdapterPosition()];

                switch (posicionPulsada){
                    case 0:
                        Log.i("menu","Restaurantes");
                        Intent intentRestaurantes = new Intent(context, Restaurantes.class);
                        intentRestaurantes.putExtra("usuarioNombre",usuario.getUsuario());
                        context.startActivity(intentRestaurantes);
                        break;
                    case 1:
                        Log.i("menu","Pedidos realizados");
                        Intent intentPedidos = new Intent(context, Pedidos.class);
                        intentPedidos.putExtra("usuarioNombre",usuario.getUsuario());
                        context.startActivity(intentPedidos);
                        break;
                    case 2:
                        Log.i("menu","Ajustes");
                        Intent intent = new Intent(context, Ajustes.class);
                        intent.putExtra("usuarioNombre",usuario.getUsuario());
                        context.startActivity(intent);
                        break;
                    case 3:
                        Log.i("menu","Informacion");
                        if(dialogoInstrucciones ==null){
                            dialogoInstrucciones = new DialogoInstrucciones();
                        }
                        dialogoInstrucciones.show(fragmentManager,"ventanaInstrucciones");
                        break;
                }
            }
        });
    }


}
