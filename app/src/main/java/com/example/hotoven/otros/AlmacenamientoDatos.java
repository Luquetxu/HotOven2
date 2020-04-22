package com.example.hotoven.otros;

public class AlmacenamientoDatos {

    private String string64;
    private static AlmacenamientoDatos am;

    private AlmacenamientoDatos(){

    }

    public static AlmacenamientoDatos getAm(){
        if(am==null){
            am = new AlmacenamientoDatos();
        }
        return am;
    }

    public String getString64(){
        return string64;
    }

    public void setString64(String s){
        string64 = s;
    }
}
