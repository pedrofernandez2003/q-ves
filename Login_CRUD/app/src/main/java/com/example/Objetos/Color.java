package com.example.Objetos;


public  enum Color {
    AMARILLO("HOLA"), VERDE("1"), CELESTE("F"), TURQUESA("2"),
    AZUL("3"),  VIOLETA("4"),GRIS("5"), ROJO("6"), ROSA("7"), NARANJA("8"),
    NEGRO("9");

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
