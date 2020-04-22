package com.example.hotoven.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.hotoven.R;

public class DialogoInstrucciones extends DialogFragment {

    /**
     * Se crea el diálogo personalizado al pulsar el botón de 'Información' del menú principal que
     * contiene la información básica de la aplicación.
     * @param savedInstanceState
     * @return
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
         super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View elaspecto = inflater.inflate(R.layout.dialog_instrucciones, null);
        builder.setView(elaspecto);

        return builder.create();
    }
}
