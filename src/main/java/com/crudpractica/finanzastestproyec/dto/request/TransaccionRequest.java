package com.crudpractica.finanzastestproyec.dto.request;

import com.crudpractica.finanzastestproyec.Enums.TipoTrasaccion;
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
public class TransaccionRequest {

    @NotNull(message = "El tipo de transacción es obligatorio")
    private TipoTrasaccion tipoTransaccion;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero")
    private BigDecimal monto;

    private String descripcion;

    @NotNull(message = "La cuenta origen es obligatoria")
    private Long cuentaOrigenId;

    private Long cuentaDestinoId;
}