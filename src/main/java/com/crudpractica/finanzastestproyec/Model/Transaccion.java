package com.crudpractica.finanzastestproyec.Model;


import com.crudpractica.finanzastestproyec.Enums.TipoCuenta;
import com.crudpractica.finanzastestproyec.Enums.TipoIdentifiacion;
import com.crudpractica.finanzastestproyec.Enums.TipoTrasaccion;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
* Entidad que representa una transaccion financiera
*
* Tipos de transacciones
*
*
* Consignacion: incrementa el saldo de una cuenta
* Retiro : Disminuye  el saldo  de una cuenta
* Transferencia: mueve  dinero entre cuentas
*
*
* */

@Entity
@Table(name ="trasacciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;


    /*
    * Tipo de trasaccion (Consiganciones, Retiro, Transferencia)*/
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_transaccion", nullable = false, length = 20)
    @NotNull(message = "El tipo de transaccion es obligatorio")
    private TipoTrasaccion tipoTrasaccion;


    /* Monto de la transaccion
    * debe ser mayor a cero*/

    @Column(name = "monto", nullable = false,precision = 15,scale = 2)
    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor cero")
    private BigDecimal monto;

    /*Fecha y horo en que  se realizo la transaccion
    * se calcula  automaticamente al crear la transaccion*/



    @Column(name = "fecha",  nullable = false, updatable = false)
    private LocalDateTime fecha;

            /*
            * Descripcion o concepto  de la transaccion*/
    @Column(name = "descripcion", length = 255)
    private String Descripcion;

/*Cuenta desde la cual  se realiza la transaccion(origen)
* para consignaciones, es la cuenta que recibe  para los retiros,
* es la cuenta  que transfiere para transferencias, es la cuenta que envia*/


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_origen_id", nullable = false)
    @NotNull(message = "La cuenta  de origen es obligatoria")
    private Cuenta cuentaOrigen;

/*Cuenta que recibe  la transferencia (destino)
* solo se usa en transferencias
* es Null  para consignaciones y retiros
* */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_destino_id")
    private  Cuenta cuentaDestino;

/*
* Metodo ejecutado  antes de persistir  la transaccion
* establece la fecha  de la transaccion automaticamente*/
    @PrePersist
    protected void onCreate(){
        this.fecha = LocalDateTime.now();

    }
/*
* Veirifica si la transaccion es una transferencia
*
* @return  true si es  transferencia, false es caso contrario*/


    public boolean esConsiganacion(){
        return TipoTrasaccion.CONSIGNACION.equals(this.tipoTrasaccion);

    }


    /*
    * Verifica si la transaccon es un retiro
    * @return true, si es cierto , false en caso contrario*/
    public boolean esRetiro(){
        return TipoTrasaccion.RETIRO.equals(this.tipoTrasaccion);
    }



}
