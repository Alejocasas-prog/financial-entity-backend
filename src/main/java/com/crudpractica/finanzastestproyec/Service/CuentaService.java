package com.crudpractica.finanzastestproyec.Service;

import com.crudpractica.finanzastestproyec.Enums.EstadoCuenta;
import com.crudpractica.finanzastestproyec.Excepcion.BuisnessException;
import com.crudpractica.finanzastestproyec.Model.Cliente;
import com.crudpractica.finanzastestproyec.Model.Cuenta;
import com.crudpractica.finanzastestproyec.Repository.ClienteRepository;
import com.crudpractica.finanzastestproyec.Repository.CuentaRepository;
import com.crudpractica.finanzastestproyec.dto.request.CuentaRequest;
import com.crudpractica.finanzastestproyec.dto.response.CuentaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CuentaService {

    private final CuentaRepository cuentaRepository;
    private final ClienteRepository clienteRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public CuentaResponse crear(CuentaRequest request) {
        log.info("Creando nueva cuenta para cliente ID: {}", request.getClienteId());

        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new BuisnessException("Cliente no encontrado con ID: " + request.getClienteId()));

        Cuenta cuenta = Cuenta.builder()
                .tipoCuenta(request.getTipoCuenta())
                .numeroCuenta(generarNumeroCuenta(request.getTipoCuenta()))
                .estado(EstadoCuenta.ACTIVA)
                .saldo(request.getSaldo() != null ? request.getSaldo() : java.math.BigDecimal.ZERO)
                .exentaGMF(request.getExentaGMF() != null ? request.getExentaGMF() : false)
                .cliente(cliente)
                .build();

        if (cuentaRepository.existsByNumeroCuenta(cuenta.getNumeroCuenta())) {
            cuenta.setNumeroCuenta(generarNumeroCuenta(request.getTipoCuenta()));
        }

        Cuenta cuentaGuardada = cuentaRepository.save(cuenta);
        log.info("Cuenta creada: {}", cuentaGuardada.getNumeroCuenta());

        return convertirAResponse(cuentaGuardada);
    }

    @Transactional
    public CuentaResponse actualizar(Long id, CuentaRequest request) {
        log.info("Actualizando cuenta ID: {}", id);

        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new BuisnessException("Cuenta no encontrada con ID: " + id));

        if (request.getSaldo() != null) {
            cuenta.setSaldo(request.getSaldo());
        }
        if (request.getExentaGMF() != null) {
            cuenta.setExentaGMF(request.getExentaGMF());
        }

        Cuenta cuentaActualizada = cuentaRepository.save(cuenta);
        return convertirAResponse(cuentaActualizada);
    }

    @Transactional
    public void cancelar(Long id) {
        log.info("Cancelando cuenta ID: {}", id);

        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new BuisnessException("Cuenta no encontrada con ID: " + id));

        if (!cuenta.puedeCancelase()) {
            throw new BuisnessException("No se puede cancelar la cuenta. El saldo debe ser $0");
        }

        cuenta.setEstado(EstadoCuenta.CANCELADA);
        cuentaRepository.save(cuenta);
        log.info("Cuenta cancelada: {}", cuenta.getNumeroCuenta());
    }

    @Transactional
    public void cambiarEstado(Long id, EstadoCuenta nuevoEstado) {
        log.info("Cambiando estado cuenta ID: {} a {}", id, nuevoEstado);

        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new BuisnessException("Cuenta no encontrada con ID: " + id));

        if (nuevoEstado == EstadoCuenta.CANCELADA && !cuenta.puedeCancelase()) {
            throw new BuisnessException("No se puede cancelar. El saldo debe ser $0");
        }

        cuenta.setEstado(nuevoEstado);
        cuentaRepository.save(cuenta);
    }

    @Transactional(readOnly = true)
    public CuentaResponse buscarPorId(Long id) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new BuisnessException("Cuenta no encontrada con ID: " + id));
        return convertirAResponse(cuenta);
    }

    @Transactional(readOnly = true)
    public CuentaResponse buscarPorNumeroCuenta(String numeroCuenta) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new BuisnessException("Cuenta no encontrada: " + numeroCuenta));
        return convertirAResponse(cuenta);
    }

    @Transactional(readOnly = true)
    public List<CuentaResponse> listarTodas() {
        return cuentaRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CuentaResponse> listarPorCliente(Long clienteId) {
        return cuentaRepository.findByClienteId(clienteId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    private String generarNumeroCuenta(com.crudpractica.finanzastestproyec.Enums.TipoCuenta tipo) {
        String prefijo = tipo.getPrefijo();
        Random random = new Random();
        StringBuilder numero = new StringBuilder(prefijo);
        for (int i = 0; i < 8; i++) {
            numero.append(random.nextInt(10));
        }
        return numero.toString();
    }

    private CuentaResponse convertirAResponse(Cuenta cuenta) {
        CuentaResponse response = modelMapper.map(cuenta, CuentaResponse.class);
        response.setClienteId(cuenta.getCliente().getId());
        response.setNombreCliente(cuenta.getCliente().getNombres() + " " + cuenta.getCliente().getApellido());
        return response;
    }
}