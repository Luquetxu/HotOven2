package com.example.hotoven.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.hotoven.R;
import com.example.hotoven.bdd_hotOven.BD_HotOven;
import com.example.hotoven.bdd_hotOven.TablasSQLite;
import com.example.hotoven.utils.GestorNotificacion;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class DialogRealizarPedido extends DialogFragment {

    private String nombreRestaurante;
    private String nombreUsuario;
    private int idUsuario;

    private CheckBox hamburguesa;
    private CheckBox pizza;
    private CheckBox plato;
    private CheckBox ensalada;
    private CheckBox cocacola;
    private CheckBox agua;
    private CheckBox fanta;
    private CheckBox cerveza;

    private EditText canthamburguesa;
    private EditText cantpizza;
    private EditText cantplato;
    private EditText cantensalada;
    private EditText cantcocacola;
    private EditText cantagua;
    private EditText cantfanta;
    private EditText cantcerveza;

    private Button boton;

    private EditText[] editTexts;
    private CheckBox[] checkBoxes;
    private BD_HotOven gestorBD;

    /**
     * Constructor
     * @param nombreRestaurante  restaurante
     * @param nombreUsuario nombre del usuario
     * @param id id del usuario
     */
    public DialogRealizarPedido(String nombreRestaurante, String nombreUsuario, int id) {
        this.nombreRestaurante = nombreRestaurante;
        this.idUsuario = id;
        this.nombreUsuario = nombreUsuario;
    }

    /**
     * Se crea el diálogo personalizado al querer realizar un pedido
     * @param savedInstanceState
     * @return
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);


        editTexts = new EditText[8];
        checkBoxes = new CheckBox[8];
        gestorBD = new BD_HotOven(getActivity(), "bd_hot_oven.sqlite", null, 1);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View elaspecto = inflater.inflate(R.layout.dialog_realizarpedido, null);

        //Se inicializan los EditTexts y los CheckBoxes, se muestran los datos en los TexViews y se
        // crean los onClickListeners de los checkboxes y del botón de realizar pedido
        inicializarViews(elaspecto);

        builder.setView(elaspecto);
        return builder.create();
    }

    /**
     * Se inicializan los EditTexts y los CheckBoxes, se muestran los datos en los TexViews y se
     * crean los onClickListeners de los checkboxes y del botón de realizar pedido
     * @param view el layout del diálogo del pedido.
     */
    private void inicializarViews(View view) {
        TextView titulo = (TextView) view.findViewById(R.id.dialog_pedido_titulo);
        titulo.setText(nombreRestaurante);

        hamburguesa = (CheckBox) view.findViewById(R.id.dialog_pedido_checkbox_hamburguesa);
        canthamburguesa = (EditText) view.findViewById(R.id.dialog_pedido_cantidad_hamburguesa);
        checkBoxes[0] = hamburguesa;
        editTexts[0] = canthamburguesa;
        hamburguesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hamburguesa.isChecked()) {
                    canthamburguesa.setVisibility(View.VISIBLE);
                } else {
                    canthamburguesa.setVisibility(View.INVISIBLE);
                }
            }
        });

        ensalada = (CheckBox) view.findViewById(R.id.dialog_pedido_checkbox_ensalada);
        cantensalada = (EditText) view.findViewById(R.id.dialog_pedido_cantidad_ensalada);
        checkBoxes[1] = ensalada;
        editTexts[1] = cantensalada;
        ensalada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ensalada.isChecked()) {
                    cantensalada.setVisibility(View.VISIBLE);
                } else {
                    cantensalada.setVisibility(View.INVISIBLE);
                }
            }
        });
        plato = (CheckBox) view.findViewById(R.id.dialog_pedido_checkbox_plato);
        cantplato = (EditText) view.findViewById(R.id.dialog_pedido_cantidad_plato);
        checkBoxes[2] = plato;
        editTexts[2] = cantplato;
        plato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (plato.isChecked()) {
                    cantplato.setVisibility(View.VISIBLE);
                } else {
                    cantplato.setVisibility(View.INVISIBLE);
                }
            }
        });
        pizza = (CheckBox) view.findViewById(R.id.dialog_pedido_checkbox_pizza);
        cantpizza = (EditText) view.findViewById(R.id.dialog_pedido_cantidad_pizza);
        checkBoxes[3] = pizza;
        editTexts[3] = cantpizza;
        pizza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pizza.isChecked()) {
                    cantpizza.setVisibility(View.VISIBLE);
                } else {
                    cantpizza.setVisibility(View.INVISIBLE);
                }
            }
        });
        agua = (CheckBox) view.findViewById(R.id.dialog_pedido_checkbox_agua);
        cantagua = (EditText) view.findViewById(R.id.dialog_pedido_cantidad_agua);
        checkBoxes[4] = agua;
        editTexts[4] = cantagua;
        agua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (agua.isChecked()) {
                    cantagua.setVisibility(View.VISIBLE);
                } else {
                    cantagua.setVisibility(View.INVISIBLE);
                }
            }
        });
        fanta = (CheckBox) view.findViewById(R.id.dialog_pedido_checkbox_fanta);
        cantfanta = (EditText) view.findViewById(R.id.dialog_pedido_cantidad_fanta);
        checkBoxes[5] = fanta;
        editTexts[5] = cantfanta;
        fanta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fanta.isChecked()) {
                    cantfanta.setVisibility(View.VISIBLE);
                } else {
                    cantfanta.setVisibility(View.INVISIBLE);
                }
            }
        });
        cerveza = (CheckBox) view.findViewById(R.id.dialog_pedido_checkbox_cerveza);
        cantcerveza = (EditText) view.findViewById(R.id.dialog_pedido_cantidad_cerveza);
        checkBoxes[6] = cerveza;
        editTexts[6] = cantcerveza;
        cerveza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cerveza.isChecked()) {
                    cantcerveza.setVisibility(View.VISIBLE);
                } else {
                    cantcerveza.setVisibility(View.INVISIBLE);
                }
            }
        });
        cocacola = (CheckBox) view.findViewById(R.id.dialog_pedido_checkbox_cocacola);
        cantcocacola = (EditText) view.findViewById(R.id.dialog_pedido_cantidad_cocacola);
        checkBoxes[7] = cocacola;
        editTexts[7] = cantcocacola;
        cocacola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cocacola.isChecked()) {
                    cantcocacola.setVisibility(View.VISIBLE);
                } else {
                    cantcocacola.setVisibility(View.INVISIBLE);
                }
            }
        });

        boton = (Button) view.findViewById(R.id.dialog_pedido_boton_pedido);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //realizarPedido();
            }
        });
    }

    /**
     * Se realiza el pedido. Si no se ha seleccionado ningún producto entonces se cerrará directamente
     * el diálogo. Si se ha seleccionado al menos uno se realiza una consulta para obtener el id del restaurante
     * para añadir a la base de datos el pedido en cuestión. Después de haber añadido a la base de datos el pedido
     * se ejeucta la notificación con los datos obtenidos del HashMap y el precio total.
     */
    /*
    private void realizarPedido() {

        Vector<Object> vector = calcularPrecioTotal();

        Double precioTotalObj = (Double) vector.get(0);
        double precioTotal = precioTotalObj.doubleValue();

        if (precioTotal != 0) {

            //--------------PEDIDO BDD
            SQLiteDatabase bdd = gestorBD.getWritableDatabase();

            Cursor cursor = bdd.rawQuery("SELECT " + TablasSQLite.RESTAURANTES_ID
                    + " FROM " + TablasSQLite.TABLA_RESTAURANTES
                    + " WHERE " + TablasSQLite.RESTAURANTES_NOMBRE + "='" + nombreRestaurante + "';", null);
            cursor.moveToFirst();
            int id = cursor.getInt(0);
            cursor.close();
            bdd.execSQL("INSERT INTO " + TablasSQLite.TABLA_PEDIDOS
                    + "(" + TablasSQLite.PEDIDOS_USUARIO + "," + TablasSQLite.PEDIDOS_RESTAURANTE + "," + TablasSQLite.PEDIDOS_FECHA_TIEMPO + "," + TablasSQLite.PEDIDOS_PRECIO
                    + ") VALUES(" + idUsuario + "," + id + ",datetime('now')," + precioTotal + ");");

            String[] devuelve = new String[] {TablasSQLite.PEDIDOS_ID};
            String[] argumentos = new String[] {id+"",idUsuario+"",precioTotal+""};
            String order = TablasSQLite.PEDIDOS_FECHA_TIEMPO+ " DESC";

            Cursor cursor2=  bdd.query(TablasSQLite.TABLA_PEDIDOS,
                    devuelve,
                    TablasSQLite.PEDIDOS_RESTAURANTE+"=? AND " + TablasSQLite.PEDIDOS_USUARIO+"=? AND " + TablasSQLite.PEDIDOS_PRECIO+"=?",
                    argumentos,
                    null,
                    null,
                    order);
            cursor2.moveToFirst();
            int idPedido = cursor2.getInt(0);
            cursor2.close();
            bdd.close();


            // ---------- PEDIDO EN LA NOTIFICACIÓN
            Map<String, Integer> pedido = (HashMap<String, Integer>) vector.get(1);
            StringBuilder sb = new StringBuilder();

            for (Map.Entry<String, Integer> entry : pedido.entrySet()) {
                sb.append(" - " + entry.getKey() + ": " + entry.getValue() + "\n");
            }
            GestorNotificacion gestorNotificacion = GestorNotificacion.getGestorNotificacion();
            gestorNotificacion.changeContext(getActivity());
            gestorNotificacion.notificacionPedidoCompletado(precioTotal, sb.toString(),nombreUsuario,idPedido);
        }
        dismiss();
    }*/

    /**
     * Recorre todos los Checkboxes mirando si está seleccionado o no, y si lo está recoge el número de
     * pedidos. Si no se añadido ningún número se cogerá como un único pedido del producto. Se almacena
     * en un HashMap cada producto seleccionado y su cantidad para mostrarlo en la notificación.
     * @return Vector que contiene el precio total del pedido y los productos seleccionados en un HashMap
     */
    private Vector<Object> calcularPrecioTotal() {
        double precioTotal = 0;
        Map<String, Integer> pedido = new HashMap<String, Integer>();
        for (int i = 0; i < checkBoxes.length; i++) {
            CheckBox check = checkBoxes[i];
            if (check.isChecked()) {
                EditText editText = editTexts[i];
                int cantidad;
                if (editText.getText().toString().equals("")) {
                    cantidad = 1;
                } else {
                    cantidad = Integer.parseInt(editText.getText().toString());
                }
                pedido.put(check.getText().toString(), Integer.valueOf(cantidad));
                precioTotal += (5 * cantidad);
            }
        }
        Vector<Object> vector = new Vector<Object>();
        vector.add(0, Double.valueOf(precioTotal));
        vector.add(1, pedido);
        return vector;
    }
}
