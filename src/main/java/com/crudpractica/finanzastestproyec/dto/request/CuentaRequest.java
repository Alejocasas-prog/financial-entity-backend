package com.crudpractica.finanzastestproyec.dto.request;


import com.crudpractica.finanzastestproyec.Enums.TipoCuenta;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaRequest {

    @NotNull(message = "El tipo de cuenta es obligatorio")
    private TipoCuenta tipoCuenta;

    @NotNull(message = "El saldo inicial es obligatorio")
    @DecimalMin(value = "0.0", message = "El saldo no puede ser negativo")
    private BigDecimal saldo;

    private Boolean exentaGMF;

    @NotNull(message = "El cliente es obligatorio")
    private Long clienteId;
}