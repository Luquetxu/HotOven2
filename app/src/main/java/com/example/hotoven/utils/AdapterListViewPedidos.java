package com.example.hotoven.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hotoven.R;

import java.util.ArrayList;

/**
 * Se definen los datos que se quieran mostrar en la lista
 */
public class AdapterListViewPedidos extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<String> nombres;
    private ArrayList<Integer> costes;
    private ArrayList<String> fechas;

    /**
     * Constructor
     * @param context: el contexto de la actividad Pedidos.
     * @param nombres: los nombres de los restaurantes
     * @param costes: los costes del pedidos
     * @param fechas: las fechas de cuando se realizaron los pedidos
     */
    public AdapterListViewPedidos(Context context, ArrayList<String> nombres, ArrayList<Integer> costes, ArrayList<String> fechas) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.nombres = nombres;
        this.costes = costes;
        this.fechas=fechas;
    }

    /**
     * Devuelve el número de pedidos
     * @return número de pedidos
     */
    @Override
    public int getCount() {
        return nombres.size();
    }

    /**
     * Devuelve el objeto de la posición i
     * @param i: posición de la lista
     * @return el objeto de la posición i
     */
    @Override
    public Object getItem(int i) {
        return nombres.get(i);
    }

    /**
     * Devuelve el identificador
     * @param i: id
     * @return el id
     */
    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * Devuelve el listView personalizado habiendole asignado los nombres de los restaurantes,
     * los teléfonos y los costes de los pedidos.
     * @param i la posición
     * @param view el listView
     * @param viewGroup todos los elementos
     * @return Devuelve el listView con todos los pedidos.
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.lista_pedidos_realizados,null);
        TextView nombre = (TextView) view.findViewById(R.id.pedidos_lista_titulo_restaurante);
        TextView coste = (TextView) view.findViewById(R.id.pedidos_lista_costeTotal);
        TextView fecha = (TextView) view.findViewById(R.id.pedidos_lista_fecha);

        nombre.setText(nombres.get(i));
        coste.setText(""+costes.get(i));
        fecha.setText(fechas.get(i));

        return view;
    }
}
