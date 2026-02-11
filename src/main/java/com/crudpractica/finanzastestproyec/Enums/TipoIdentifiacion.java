package com.crudpractica.finanzastestproyec.Enums;

/*
    Enumeracion que define los tipos de identificacion validos para los clientes
    @author Sistema  de Gestion Financiera
    @version 1.0.0

*/


//Enumeracion que define los tipos de identificacion validos para los clientes


public enum TipoIdentifiacion {

    // Valores constantes

    //cedula de ciudadania

    CEDULA_CIUDADANIA("CC", "Cedula de Ciudadania"),

    //cedula de extranjeria
    CEDULA_EXTRANJERIA("CE", "Cedula de Extranjeria"),
    //pasaporte
    PASAPORTE("PA", "Pasaporte"),

    //numero de indetificacion Tributaria
    NIT("NIT", "Numero de Identificacion Tributaria");

    private final String codigo;
    private final String descripcion;



    /*

    @param codigo, codigo corto del tipo de identificacion
    @param descripcion, descripcion completa del tipo de identificacion

    **/

    TipoIdentifiacion(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }



    /*
    Obtiene  el codigo  del tipo de identifiacion
    @return codigo del tipo de identificacion
     */

    public String getCodigo() {
        return codigo;
    }


    /*
    Obtiene la decripcion del tipo de identificacion
    @return descripcion del tipo de identificacion

    */


    public String getDescripcion(){
            return descripcion;
    }

}
