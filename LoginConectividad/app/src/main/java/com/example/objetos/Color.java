package com.example.objetos;


public  enum Color {
    AMARILLO(0xfffed713), VERDE(0xff008000 ), CELESTE(0xff13c8f5), TURQUESA(0xff67e2aa),
    AZUL(0xff0f3bee),  VIOLETA(0xff3D00FF),GRIS(0xff808080), ROJO(0xffC21807), ROSA(0xffE63C8E), NARANJA(0xffF04A00),
    NEGRO(0xff1b1bbf), AZULOSCURO(0xff262262);

//    private String nombre;
    private final int codigo;

    Color(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }


}
