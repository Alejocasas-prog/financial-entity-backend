package com.crudpractica.finanzastestproyec.dto.response;

import com.crudpractica.finanzastestproyec.Enums.TipoTrasaccion;
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
public class TransaccionResponse {

    private Long id;
    private TipoTrasaccion tipoTransaccion;
    private BigDecimal monto;
    private LocalDateTime fecha;
    private String descripcion;
    private String numeroCuentaOrigen;
    private String numeroCuentaDestino;
}