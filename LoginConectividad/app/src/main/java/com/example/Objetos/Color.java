package com.example.Objetos;


public  enum Color {
    AMARILLO(0xffffff00), VERDE(0xff008000 ), CELESTE(0xff13c8f5), TURQUESA(0xff67e2aa),
    AZUL(0xff0f3bee),  VIOLETA(0xff3D00FF),GRIS(0xff808080), ROJO(0xffC21807), ROSA(0xffE63C8E), NARANJA(0xffF04A00),
    NEGRO(0xff1b1bbf);

//    private String nombre;
    private final int codigo;

    Color(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }


}
