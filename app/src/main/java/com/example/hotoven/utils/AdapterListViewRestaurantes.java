package com.example.hotoven.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hotoven.R;

/**
 * Se definen los datos que se quieran mostrar en la lista
 */
public class AdapterListViewRestaurantes extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private String[] nombres;
    private int[] imagenes;
    private int[] telefonos;
    private String[] tipos;

    /**
     * Constructor
     * @param context el contexto de la actividad Restaurantes
     * @param nombres nombres de los restaurantes
     * @param imagenes imagenes de los restaurantes
     * @param telefonos telefonos de los restaurantes
     * @param tipos los tipos de los restaurantes.
     */
    public AdapterListViewRestaurantes(Context context, String[] nombres,
                                       int[] imagenes, int[] telefonos,String[] tipos) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.nombres = nombres;
        this.imagenes = imagenes;
        this.telefonos = telefonos;
        this.tipos=tipos;
    }

    /**
     * Devuelve el número de restaurantes
     * @return número de restaurantes
     */
    @Override
    public int getCount() {
        return nombres.length;
    }

    /**
     * Devuelve el valor objeto del elemento i
     * @param i: posición del array
     * @return el objeto
     */
    @Override
    public Object getItem(int i) {
        return nombres[i];
    }

    /**
     * Devuelve el identificador
     * @param i: identificador
     * @return id
     */
    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * Devuelve el listView personalizado habiendole asignado los nombres de los restaurantes,
     * las imágenes, los telefonos y los tipos de los restaurantes
     * @param i la posición
     * @param view el listView
     * @param viewGroup todos los elementos
     * @return Devuelve el listView con todos los pedidos.
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.lista_restaurante,null);
        TextView nombre = (TextView) view.findViewById(R.id.restaurante_lista_nombre);
        ImageView img = (ImageView) view.findViewById(R.id.restaurante_lista_imagen);
        TextView telefono = (TextView) view.findViewById(R.id.restaurante_lista_telefono);
        TextView tipo = (TextView) view.findViewById(R.id.restaurante_lista_tipo);

        nombre.setText(nombres[i]);
        img.setImageResource(imagenes[i]);
        telefono.setText(""+telefonos[i]);
        tipo.setText(tipos[i]);

        return view;
    }
}
