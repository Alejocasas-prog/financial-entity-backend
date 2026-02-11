package com.crudpractica.finanzastestproyec.Enums;



/*
    //Enumeracion que define los tipos  de cuentas financieras disponibles

    @author Sistema de Gestiona  Financiera
    @version 1.0.0
*/


public enum TipoTrasaccion {

        //Consignacion - Incrementa el saldo  de la cuenta

    CONSIGNACION("Consignacion"),

    //Retiro - Disminuye el saldo de la cuenta

    RETIRO("Retiro"),

    //Transferencia - Mueve  dinero  entre cuentas(debito en origen, credito en disino)

    TRANSFERENCIA("Transferencia");


    private final String descripcion;

    /*
    contructor del enum TipoTransaccion
    */
    TipoTrasaccion(String descripcion) {
        this.descripcion = descripcion;
    }


    /*

    Obtiene la descripcion del tipo de transferencia
    @return descripcion de la transferencia
    */

    public String getDescripcion(){
        return descripcion;
    }
}
