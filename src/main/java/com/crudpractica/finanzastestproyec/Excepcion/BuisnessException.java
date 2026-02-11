package com.crudpractica.finanzastestproyec.Excepcion;



/*
* Excepcion personalizada para manejar errores de logica de negocio
*
* se lanza cuando se violan reglas de negocio como
* cliente menor de edad
* eliminar cliente con cuentas vinculadas
* cancelar cuentas con saldo diferente de $0
* transferir a cuenta inexistente
* realizar trasancciones en cuenta inactivas
*
* */
public class BuisnessException extends RuntimeException{

    // contructor  con mensaje error

    //@param meessage  mensaje descriptivo  de error  de negocio


    public BuisnessException(String message){
        super(message);
    }
}
