package com.crudpractica.finanzastestproyec.Model;




/*
* Entidad que representa una cuenta financiera (ahorro o corriente)
* las cuentas de ahorro inician con "53" y las corrientes con "33"
* el numero  de cuenta debe ser unico y tener  10 digitos
* solo se puede cancelar cuentas con saldo $0
*
* */


import com.crudpractica.finanzastestproyec.Enums.EstadoCuenta;
import com.crudpractica.finanzastestproyec.Enums.TipoCuenta;
import com.crudpractica.finanzastestproyec.Enums.TipoTrasaccion;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "cuentas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;



    /*
    * tipo de cuenta*/

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cuenta", nullable = false,length = 20)
    @NotNull(message = "El tipo de cuenta es obligatoria")
    private TipoCuenta tipoCuenta;

    /*
    * Numero unico de la cuenta (10 digitos)
    * cuentas de ahorro iniciarn con "53"
    * cuentas corrientes inician con "33"
    * */

    @Column(name = "numero_cuenta", nullable = false, unique = true, length = 10)
    @NotBlank(message = "El numero  de cuenta es obligatorio")
    @Pattern(regexp= "^(53|33)\\d{8}$", message = "El numero de cuenta debe tener 10 digitos  y comenzar con 53(ahorro) o 33(corriente)")
    private String numeroCuenta;


    /*
    * Estado actual de la cuenta
    * las cuentas de ahorro no pueden tener saldo negativo*/


    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false,length = 20)
    @NotNull(message = "El saldo de la cuenta obligatorio")
    private EstadoCuenta estado;



    /*
    * Indica si la cuenta esta exenta del gravamen a los  movimientos Financieros (GMF)*/
    @Column(name = "exenta_gmf" , nullable = false)  // ← NOMBRE CORRECTO
    private Boolean exentaGMF;



    /*
     * Saldo actual de la cuenta
     * las cuentas de ahorro  no pueden tener  saldo negativo*/

    @Column(name = "saldo", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "El saldo es obligatorio")
    @DecimalMin(value = "0.0", message = "El saldo no puede ser negativo")
    private BigDecimal saldo;


    /*
    * Fecha de creacion de la cuenta
    * se calcula automaticamente al crear la cuenta*/
    @Column(name = "fecha_creacion" , nullable = false , updatable = false)
    private LocalDateTime fechaCreacion;


    /*
    * Fecha de la ultima modificacion de la cuenta
    * se actualiza automaticamente al modificar la cuenta*/

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    /*
    * Cliente propietariode la cuenta*/

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @NotNull(message = "El cliente es obligatorio")
    private Cliente cliente;



    /*
    * Lista de transacciones realizadas desde esta cuenta
    * */
    @OneToMany(mappedBy = "cuentaOrigen", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaccion> trasaccionesOrigen;


    /*Lista  de transacciones recibidas en esta cuenta*/
    @OneToMany(mappedBy = "cuentaDestino", cascade = CascadeType.ALL, fetch = FetchType.LAZY)    private List<Transaccion> trasaccionesDestino;



    /*Metodo  ejecutado antes de persistir  la cuenta
    * establce  la fecha  de creacion  y estado inicial*/

    @PrePersist
    protected void onCreate(){
        this.fechaCreacion = LocalDateTime.now();
        this.fechaModificacion = LocalDateTime.now();
        if(this.estado == null){
            this.estado = EstadoCuenta.ACTIVA;
        }
        if(this.saldo == null){
            this.saldo = BigDecimal.ZERO;

        }
        if(this.exentaGMF == null){
            this.exentaGMF = false;

        }
    }
    /*
    *
    * Motodo ejecutando antes de actualizar la cuenta
    * Actualizar la fecha  de modificaciones automaticamente*/

    @PreUpdate
    protected void onUpdate(){
        this.fechaModificacion = LocalDateTime.now();
    }

    /*
     * Verifica si la cuenta esta activa
     *
     * @return true si la cuenta  esta activa, false en caso  contrario*/


    public boolean estaActiva(){
        return  EstadoCuenta.ACTIVA.equals(this.estado);
    }

    /*
    * Verifica el saldo  de la cuenta es cero
    * @return true  si  el saldo  es 0, false  en caso  contrario
    * */

    public boolean saldoEscero(){
        return this.saldo.compareTo(BigDecimal.ZERO) == 0;


        /*
        * veirifica si la cuenta  puede ser  cancelada
        * solo se pueden cancelar  cuentas con saldo %0
        * @return true  si puede ser cancelada, false en caso contrario
        *
        * */
    }

    public boolean puedeCancelase(){
        return saldoEscero();
    }


    /*Incrementa el saldo de la cuenta
    *
    * */
    public void incrementarSaldo(BigDecimal monto){
        this.saldo = this.saldo.add(monto);
    }


    /*
    * Disminuye el saldo  de la cuenta
    * @oaram montom, monto a restar del saldo
    *
    */
    public void disminuirSaldo(BigDecimal monto ){
        this.saldo = this.saldo.subtract(monto);
    }
}



