package com.crudpractica.finanzastestproyec.dto.response;

import com.crudpractica.finanzastestproyec.Enums.EstadoCuenta;
import com.crudpractica.finanzastestproyec.Enums.TipoCuenta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaResponse {

    private Long id;
    private TipoCuenta tipoCuenta;
    private String numeroCuenta;
    private EstadoCuenta estado;
    private BigDecimal saldo;
    private Boolean exentaGMF;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    private Long clienteId;
    private String nombreCliente;
}