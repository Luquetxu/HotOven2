package com.example.hotoven.otros;

public class Distancia {

    private Posicion posUsuario;
    private Posicion posRestaurante;

    private static final double RADIO_TIERRA = 6378.0f;

    /**
     * Constructor
     * @param posUsuario: posicion del usuario
     * @param posRestaurante: posicion del restaurante
     */
    public Distancia(Posicion posUsuario, Posicion posRestaurante) {
        this.posUsuario = posUsuario;
        this.posRestaurante = posRestaurante;
    }

    /**
     * Calcula la distancia entre el usuario y restaurante
     * @return: la distancia
     */
    public double calcularDistancia(){
        /**Basado en el código extraído de una página web
         * Asunto: https://www.genbeta.com/desarrollo/como-calcular-la-distancia-entre-dos-puntos-geograficos-en-c-formula-de-haversine
         * Modificado  por  Unai Luque pasado a código Java **/
        double difLatitud = (Math.PI / 180) * (posRestaurante.getLatitud() - posUsuario.getLatitud());
        double difLongitud = (Math.PI / 180) * (posRestaurante.getLongitud() - posUsuario.getLongitud());

        double a = (Math.pow(Math.sin(difLatitud/2),2)) +
                ((Math.PI / 180) * Math.cos(posRestaurante.getLatitud())) *
                        ((Math.PI / 180) * Math.cos(posUsuario.getLatitud())) *
                        (Math.pow(Math.sin(difLongitud/2),2));

        double c = 2 * Math.atan2(Math.sqrt(a),Math.sqrt(1-a));


        return RADIO_TIERRA * c;
    }

}
