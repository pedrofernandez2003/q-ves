package com.example.Objetos;


public  enum Color {
    AMARILLO("HOLA"), VERDE(""), CELESTE("F"), TURQUESA(""),
    AZUL(""),  VIOLETA(""),GRIS(""), ROJO(""), ROSA(""), NARANJA(""),
    NEGRO("");

//    private String nombre;
    private String codigo;

    Color(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(java.lang.String codigo) {
        this.codigo = codigo;
    }

}
