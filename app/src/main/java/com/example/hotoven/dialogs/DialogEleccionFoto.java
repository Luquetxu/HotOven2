package com.example.hotoven.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.hotoven.R;

public class DialogEleccionFoto extends DialogFragment {

    private ListenerFoto listenerFoto;

    public interface ListenerFoto{
        void galeria();
        void capturarFoto();
        void cancelar();
    }
    /**
     * Se crea el diálogo básico al querer cerrar sesión preguntándote si deseas o no cerrarlo.
     *
     * @param savedInstanceState
     * @return
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getText(R.string.dialog_foto_eleccion));

        listenerFoto = (ListenerFoto) getActivity();


        final CharSequence[] opciones =
                {getResources().getText(R.string.dialog_foto_capturar), getResources().getText(R.string.dialog_foto_galeria), getResources().getText(R.string.dialog_foto_cancelar)};

        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals(getResources().getText(R.string.dialog_foto_capturar))) {
                    listenerFoto.capturarFoto();
                } else if (opciones[i].equals(getResources().getText(R.string.dialog_foto_galeria))) {
                    listenerFoto.galeria();
                } else if (opciones[i].equals(getResources().getText(R.string.dialog_foto_cancelar))) {
                    listenerFoto.cancelar();
                }

            }
        });
        return builder.create();
    }

}


