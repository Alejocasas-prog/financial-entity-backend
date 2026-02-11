package com.crudpractica.finanzastestproyec.Enums;


//Enumeracion que define los estados posibles de una cuenta financiera

//javaDocs Tags

/* @author Sistema de Gestion Financiera
   @version 1.0.0
 */


public enum EstadoCuenta {




    // Valores constantes

    //Cuenta activa  - puede realizar transacciones

    ACTIVA("Cuenta Activa "),

    //Cuenta Inactiva - no puede realizar transacciones

    INACTIVA("Cuenta Inactiva"),

    // Cuenta cancelada - debe tener saldo $0 para cancelarse

    CANCELADA("Cuenta Cancelada");



    private final String descripcion;


    //constructor Enum EstadoCuenta

    EstadoCuenta(String descripcion) {
        this.descripcion = descripcion;
    }



    /*
    Obtiene la descripcion  del estado  de cuenta

    @retunrn descripcion del estado
    * */
    public String getDescripcion(){
        return descripcion;
    }
}
