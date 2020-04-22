package com.example.hotoven.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotoven.R;
import com.example.hotoven.bdd_hotOven.Usuario;

/**
 * Define los datos que se quieran mostrar en el CardView
 */
public class ElAdaptadorRecycler extends RecyclerView.Adapter<ElViewHolder_MP> {

    private String[] lostitulos;
    private int[] lasimagenes;
    private static int[] seleccionados;
    private FragmentManager fragmentManager;
    private Context context;
    //private Activity activity;
    private Usuario usuario;

    /**
     * Constructor
     * @param titulos títulos de los menús
     * @param imagenes imágenes de los menús
     * @param fragmentManager el fragment manager de la actividad MenuPrincipal
     * @param pContext el contexto de la actividad MenuPrincipal
     * @param usuario el usuario en el que inicias sesión
     */
    public ElAdaptadorRecycler(String[] titulos,int[] imagenes,FragmentManager fragmentManager,Context pContext, Usuario usuario){
        lostitulos = titulos;
        lasimagenes = imagenes;
        seleccionados = new int[lostitulos.length];
        for(int i=0;i<seleccionados.length;i++){
            seleccionados[i]= i;
        }
        this.fragmentManager=fragmentManager;
        context=pContext;
        //activity=actividad;
        this.usuario = usuario;
    }

    /**
     * Infla el layout definido para cada elemento y crea y devuelve una instancia de la clase que extiende a ViewHolder
     * @param parent el view group
     * @param viewType typo de view
     * @return Devuelve el ViewHolder del menú principal
     */
    @NonNull
    @Override
    public ElViewHolder_MP onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View ellayoutdelafila = LayoutInflater.from(parent.getContext()).inflate(R.layout.mp_cardview,null);
        ElViewHolder_MP evh = new ElViewHolder_MP(ellayoutdelafila);
        evh.seleccion=seleccionados;
        return evh;
    }

    /**
     * Asigna a los atributos del ViewHolder los valores a mostrar para una posición concreta
     * @param holder el viewHolder del menú principal
     * @param position la posición de la lista
     */
    @Override
    public void onBindViewHolder(@NonNull ElViewHolder_MP holder, int position) {
        holder.eltexto.setText(lostitulos[position]);
        holder.laimagen.setImageResource(lasimagenes[position]);
        holder.fragmentManager=fragmentManager;
        holder.context = context;
        //holder.activity = activity;
        holder.usuario = usuario;
    }

    /**
     * Devuelve el número de elementos
     * @return Número de elementos
     */
    @Override
    public int getItemCount() {
        return lostitulos.length;
    }
}
