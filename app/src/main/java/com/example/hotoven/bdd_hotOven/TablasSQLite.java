package com.example.hotoven.bdd_hotOven;

public class TablasSQLite {

    public static final String TABLA_USUARIOS = "Usuarios";
    public static final String USUARIOS_ID = "id";
    public static final String USUARIOS_USUARIO = "usuario";
    public static final String USUARIOS_CONTRASENA = "contrasena";

    public static final String TABLA_RESTAURANTES = "Restaurantes";
    public static final String RESTAURANTES_ID = "id";
    public static final String RESTAURANTES_NOMBRE = "nombre";
    public static final String RESTAURANTES_TELEFONO = "telefono";
    public static final String RESTAURANTES_TIPO_RESTAURANTE = "tipo";

    public static final String TABLA_PEDIDOS = "Pedidos";
    public static final String PEDIDOS_ID = "idPedido";
    public static final String PEDIDOS_USUARIO = "idUsuario";
    public static final String PEDIDOS_RESTAURANTE = "idRestaurante";
    public static final String PEDIDOS_FECHA_TIEMPO = "fechaTiempo";
    public static final String PEDIDOS_PRECIO = "precio";

    public static final String CREAR_TABLA_USUARIO =
            "CREATE TABLE " + TABLA_USUARIOS + " (" + USUARIOS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + USUARIOS_USUARIO + " TEXT," + USUARIOS_CONTRASENA + " TEXT);";

    public static final String CREAR_TABLA_RESTAURANTES =
            "CREATE TABLE " + TABLA_RESTAURANTES + " ( " + RESTAURANTES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + RESTAURANTES_NOMBRE + " TEXT," + RESTAURANTES_TELEFONO + " INTEGER, " + RESTAURANTES_TIPO_RESTAURANTE + " INTEGER);";

    public static final String CREAR_TABLA_PEDIDOS =
            "CREATE TABLE " + TablasSQLite.TABLA_PEDIDOS + " (" + PEDIDOS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + PEDIDOS_USUARIO + " INTEGER, " + PEDIDOS_RESTAURANTE + " INTEGER, " + PEDIDOS_FECHA_TIEMPO + " TEXT,"+ PEDIDOS_PRECIO +" INTEGER, FOREIGN KEY(" + PEDIDOS_USUARIO + ") REFERENCES " + TABLA_USUARIOS + "(" + USUARIOS_ID + "),FOREIGN KEY(" + PEDIDOS_RESTAURANTE + ") REFERENCES " + TABLA_RESTAURANTES + "(" + RESTAURANTES_ID + "));";


    public static final String INSERT_RESTAURANTE_TELEPIZZA =
            "INSERT INTO " + TablasSQLite.TABLA_RESTAURANTES + "(" + RESTAURANTES_NOMBRE + "," + RESTAURANTES_TELEFONO + "," + RESTAURANTES_TIPO_RESTAURANTE + ") VALUES('Telepizza',902122122,0);";
    public static final String INSERT_RESTAURANTE_BURGUER_KING =
            "INSERT INTO " + TablasSQLite.TABLA_RESTAURANTES + "(" + RESTAURANTES_NOMBRE + "," + RESTAURANTES_TELEFONO + "," + RESTAURANTES_TIPO_RESTAURANTE + ") VALUES('Burguer King',917091260,0);";
    public static final String INSERT_RESTAURANTE_NAM =
            "INSERT INTO " + TablasSQLite.TABLA_RESTAURANTES + "(" + RESTAURANTES_NOMBRE + "," + RESTAURANTES_TELEFONO + "," + RESTAURANTES_TIPO_RESTAURANTE + ") VALUES('Nam',944950516,1);";

    public static final String INSERT_RESTAURANTE_MONTADITOS =
            "INSERT INTO " + TablasSQLite.TABLA_RESTAURANTES + "(" + RESTAURANTES_NOMBRE + "," + RESTAURANTES_TELEFONO + "," + RESTAURANTES_TIPO_RESTAURANTE + ") VALUES('100 Montaditos',902197494,1);";

    public static final String INSERT_RESTAURANTE_BAR_LONTANZANZA =
            "INSERT INTO " + TablasSQLite.TABLA_RESTAURANTES + "(" + RESTAURANTES_NOMBRE + "," + RESTAURANTES_TELEFONO + "," + RESTAURANTES_TIPO_RESTAURANTE + ") VALUES('Bar Lontananza',964357532,2);";

}
