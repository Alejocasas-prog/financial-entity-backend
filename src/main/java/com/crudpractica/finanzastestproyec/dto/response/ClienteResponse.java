package com.crudpractica.finanzastestproyec.dto.response;

/*
* DTO  de respuesta  con la informacion de un cliente
* Incluye datos  calculados como la edad y la cantidad de cuentas
* vinculadas
* */

import com.crudpractica.finanzastestproyec.Enums.TipoIdentifiacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder




public class ClienteResponse {
    /*
    * Identificador unico del cliente*/

    private  Long Id;


    //Tipo de documento de indetificacion

    private TipoIdentifiacion tipoIdentifiacion;


    //Numero de documento de identificacion

    private String numeroIdentificacion;

    //nombres de clientes

    private String nombres;

    //apellidos de clientes

    private String apellidos;


    //correo electronico
    private String correElectronico;

    //fecha de nacimiento


    private LocalDate fechaNacimiento;

    //Edad calculada del cliente en años

    private Integer Edad;

    //Fecha de creacion del registro

    private LocalDateTime fechaCreacion;
    //Fecha de ultima modificacion del registro


    private LocalDateTime fechaModificacion;

    //cantidad de cuentas financieras viculadas al cliente

    private Integer cantidadCuentas;
}
