package com.example.hotoven.bdd_hotOven;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import static com.example.hotoven.bdd_hotOven.TablasSQLite.TABLA_RESTAURANTES;

public class BD_HotOven extends SQLiteOpenHelper {


    /**
     * Constructor
     * @param context contexto de la actividad
     * @param name: nombre de la base de datos sqlite
     * @param factory
     * @param version versión de la BDD
     */
    public BD_HotOven(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * Se crean las tablas y se insertan algunas instancias a la base de datos al crearla.
     * @param sqLiteDatabase base de datos
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
       /* sqLiteDatabase.execSQL(TablasSQLite.CREAR_TABLA_RESTAURANTES);

        sqLiteDatabase.execSQL(TablasSQLite.INSERT_RESTAURANTE_TELEPIZZA);
        sqLiteDatabase.execSQL(TablasSQLite.INSERT_RESTAURANTE_MONTADITOS);
        sqLiteDatabase.execSQL(TablasSQLite.INSERT_RESTAURANTE_BAR_LONTANZANZA);
        sqLiteDatabase.execSQL(TablasSQLite.INSERT_RESTAURANTE_BURGUER_KING);
        sqLiteDatabase.execSQL(TablasSQLite.INSERT_RESTAURANTE_NAM);

        sqLiteDatabase.execSQL(TablasSQLite.CREAR_TABLA_USUARIO);
        sqLiteDatabase.execSQL(TablasSQLite.CREAR_TABLA_PEDIDOS);*/


    }

    /**
     * Al actualizar de versión, se borrarán las tablas y se volverá a crear las tablas.
     * @param sqLiteDatabase bases de datos
     * @param i versión antigua
     * @param i1 versión nueva
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
       /* sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TablasSQLite.TABLA_USUARIOS+";");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ TABLA_RESTAURANTES+";");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TablasSQLite.TABLA_PEDIDOS+";");
        onCreate(sqLiteDatabase);*/
    }
}
