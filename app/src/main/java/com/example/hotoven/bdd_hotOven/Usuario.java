package com.example.hotoven.bdd_hotOven;

public class Usuario {

    private String usuario;

    /**
     * Constructor
     * @param usuario nombre del usuario
     */
    public Usuario(String usuario) {
        this.usuario = usuario;
    }

    /**
     * Devuelve el nombre del usuario
     * @return: nombre del usuario
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * Cambia el nombre del usuario
     * @param usuario
     */
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

}
