package com.example.objetos;


public enum Color {
    AMARILLO(0xfffed713), VERDE(0xff8dc63f ), CELESTE(0xff00aeef), TURQUESA(0xff00a79e),
    AZUL(0xff1c75bc),  VIOLETA(0xff92278f),GRIS(0xff808080), ROJO(0xffed1d24), ROSA(0xffee2a7b),
    NARANJA(0xffF7941E), NEGRO(0xff000000), AZULOSCURO(0xff262262), MARRON(0xff804000), GRANATE(0xff3f0e1e);

//    private String nombre;
    private final int codigo;

    Color(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }


}
