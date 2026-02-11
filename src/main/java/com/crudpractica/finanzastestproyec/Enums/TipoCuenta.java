package com.crudpractica.finanzastestproyec.Enums;


/*

    //Enumeracion que define los tipos  de cuentas financieras disponibles

    @author Sistema de Gestiona  Financiera
    @version 1.0.0
*/



public enum TipoCuenta {


    //Cuenta de Ahorros - Inicia con prefijo "53"

    AHORRO("53", "Cuenta de Ahorros"),

    //Cuenta Corriente  - Inicia  con prefijo "33"


    CORRIENTE("33", "Cuenta Corriente");

    private final String prefijo;
    private final String descrpicion;


    /*

    @param prefijo  prefijo numerico que identifica el tipo de cuenta (53 o 33)
    @param descripcion descripcion  completa del tipo de cuenta

    */

    TipoCuenta(String prefijo, String descrpicion) {
        this.prefijo = prefijo;
        this.descrpicion = descrpicion;
    }


    /*

    Obtiene el prefijo del tipo de cuenta

    @return prefijo numerico el tipo de cuenta

    */


    public String getPrefijo(){
        return prefijo;
    }

    /*

    Obtiene la descripcion del tipo de cuenta
    @return descripcion del tipo de cuenta

    */

    public String getDescrpicion(){
        return descrpicion;
    }
}
