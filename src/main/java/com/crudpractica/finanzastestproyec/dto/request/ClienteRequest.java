package com.crudpractica.finanzastestproyec.dto.request;


import com.crudpractica.finanzastestproyec.Enums.TipoIdentifiacion;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
 /*
 *DTO  para crear  o actualizar un cliente
 * Contiene las validaciones de neogocio  para el registro de clientes
 * nombres y apellidos minimo 2 caracteres, email valido, etc
 * */


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ClienteRequest {

// Tipo  de docuemento de identificadion del cliente

    @NotNull(message = "EL tipo de identifiacaion es obligatoria")
    private TipoIdentifiacion tipoIdentifiacion;


    //Numero de documento de identificacion
    //debe tener  entre 5 a 20 caracteres


    @NotBlank(message = "El numero de identificacion es obligatorio")
    @Size(min = 5, max = 20, message = "El numero de identificacion  debe tener  entre 5 y 20 caracteres")
    private String numeroIdentificacion;

    /*
     * NOombres de clientes
     * Debe tener minimo 2 caracteres
     *

   * */
        @NotBlank(message = "Los nombres son obligatorios")
    @Size(min = 2, max = 100, message = "Los nombres deben tener entre 2 y 100 caracteres")
    private String nombres;


        /*
        * APellidos del cliente
        * Debe tener minimo 2 caracteres*/


    @NotBlank(message = "Los apellidos del cliente")
    @Size(min = 2,max = 100,message = "Los apellidos deben tener entre 2 y 100 caracteres")
    private String apellido;


    /*
    *
    * correo electronico del cliente
    * debe tener formato valido (xx@xxxx.xxx)*/

    @NotBlank(message = "El correo electronico es obligatorio")
    @Email(message = "El correo electronico debe tener un formato valido")
    private String correoElectronico;

    /*
    * Fecha de nacimiento del cliente
    * el cliente debe ser mayor  de 18años
    **
     */


    @NotBlank(message = "la fecha de nacimiento es obligatoria")
    @Past(message = "la fecha de nacimiento  debe ser anterior a la fehca actual")
    private LocalDate fechaNacimieto;

}



