package com.example.hotoven.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.hotoven.R;

public class DialogRegistroDatosVacios extends DialogFragment {

    /**
     * Se crea el diálogo para cuando hay datos no introducidos en la activad de Registro al querer
     * registrar un usuario
     * @param savedInstanceState
     * @return
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View elaspecto = inflater.inflate(R.layout.dialog_registro, null);
        builder.setView(elaspecto);

        return builder.create();
    }
}
