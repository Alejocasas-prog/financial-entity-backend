package com.crudpractica.finanzastestproyec.Service;

import com.crudpractica.finanzastestproyec.Enums.TipoTrasaccion;
import com.crudpractica.finanzastestproyec.Excepcion.BuisnessException;
import com.crudpractica.finanzastestproyec.Model.Cuenta;
import com.crudpractica.finanzastestproyec.Model.Transaccion;
import com.crudpractica.finanzastestproyec.Repository.CuentaRepository;
import com.crudpractica.finanzastestproyec.Repository.TransaccionRepository;
import com.crudpractica.finanzastestproyec.dto.request.TransaccionRequest;
import com.crudpractica.finanzastestproyec.dto.response.TransaccionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final CuentaRepository cuentaRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public TransaccionResponse crear(TransaccionRequest request) {
        log.info("Creando transacción tipo: {}", request.getTipoTransaccion());

        Cuenta cuentaOrigen = cuentaRepository.findById(request.getCuentaOrigenId())
                .orElseThrow(() -> new BuisnessException("Cuenta origen no encontrada"));

        if (!cuentaOrigen.estaActiva()) {
            throw new BuisnessException("La cuenta origen no está activa");
        }

        Transaccion transaccion = new Transaccion();
        transaccion.setTipoTrasaccion(request.getTipoTransaccion());
        transaccion.setMonto(request.getMonto());
        transaccion.setDescripcion(request.getDescripcion());
        transaccion.setCuentaOrigen(cuentaOrigen);

        switch (request.getTipoTransaccion()) {
            case CONSIGNACION:
                procesarConsignacion(cuentaOrigen, request.getMonto());
                break;
            case RETIRO:
                procesarRetiro(cuentaOrigen, request.getMonto());
                break;
            case TRANSFERENCIA:
                if (request.getCuentaDestinoId() == null) {
                    throw new BuisnessException("Se requiere cuenta destino para transferencias");
                }
                Cuenta cuentaDestino = cuentaRepository.findById(request.getCuentaDestinoId())
                        .orElseThrow(() -> new BuisnessException("Cuenta destino no encontrada"));
                procesarTransferencia(cuentaOrigen, cuentaDestino, request.getMonto());
                transaccion.setCuentaDestino(cuentaDestino);
                break;
        }

        Transaccion transaccionGuardada = transaccionRepository.save(transaccion);
        log.info("Transacción creada ID: {}", transaccionGuardada.getId());

        return convertirAResponse(transaccionGuardada);
    }

    private void procesarConsignacion(Cuenta cuenta, BigDecimal monto) {
        cuenta.incrementarSaldo(monto);
        cuentaRepository.save(cuenta);
        log.info("Consignación exitosa: ${} a cuenta {}", monto, cuenta.getNumeroCuenta());
    }

    private void procesarRetiro(Cuenta cuenta, BigDecimal monto) {
        if (cuenta.getSaldo().compareTo(monto) < 0) {
            throw new BuisnessException("Saldo insuficiente");
        }
        cuenta.disminuirSaldo(monto);
        cuentaRepository.save(cuenta);
        log.info("Retiro exitoso: ${} de cuenta {}", monto, cuenta.getNumeroCuenta());
    }

    private void procesarTransferencia(Cuenta origen, Cuenta destino, BigDecimal monto) {
        if (!destino.estaActiva()) {
            throw new BuisnessException("La cuenta destino no está activa");
        }
        if (origen.getSaldo().compareTo(monto) < 0) {
            throw new BuisnessException("Saldo insuficiente en cuenta origen");
        }
        origen.disminuirSaldo(monto);
        destino.incrementarSaldo(monto);
        cuentaRepository.save(origen);
        cuentaRepository.save(destino);
        log.info("Transferencia exitosa: ${} de {} a {}", monto, origen.getNumeroCuenta(), destino.getNumeroCuenta());
    }

    @Transactional(readOnly = true)
    public TransaccionResponse buscarPorId(Long id) {
        Transaccion transaccion = transaccionRepository.findById(id)
                .orElseThrow(() -> new BuisnessException("Transacción no encontrada"));
        return convertirAResponse(transaccion);
    }

    @Transactional(readOnly = true)
    public List<TransaccionResponse> listarTodas() {
        return transaccionRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransaccionResponse> listarPorCuenta(Long cuentaId) {
        return transaccionRepository.findAllByCuentaId(cuentaId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    private TransaccionResponse convertirAResponse(Transaccion transaccion) {
        TransaccionResponse response = modelMapper.map(transaccion, TransaccionResponse.class);
        response.setNumeroCuentaOrigen(transaccion.getCuentaOrigen().getNumeroCuenta());
        if (transaccion.getCuentaDestino() != null) {
            response.setNumeroCuentaDestino(transaccion.getCuentaDestino().getNumeroCuenta());
        }
        return response;
    }
}