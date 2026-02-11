package com.crudpractica.finanzastestproyec.Controller;

import com.crudpractica.finanzastestproyec.Enums.EstadoCuenta;
import com.crudpractica.finanzastestproyec.Service.CuentaService;
import com.crudpractica.finanzastestproyec.dto.request.CuentaRequest;
import com.crudpractica.finanzastestproyec.dto.response.CuentaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cuentas")
@RequiredArgsConstructor
@Slf4j
public class CuentaController {

    private final CuentaService cuentaService;

    @PostMapping
    public ResponseEntity<CuentaResponse> crear(@Valid @RequestBody CuentaRequest request) {
        log.info("POST /api/cuentas - Crear cuenta");
        CuentaResponse response = cuentaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CuentaResponse>> listarTodas() {
        log.info("GET /api/cuentas - Listar todas");
        return ResponseEntity.ok(cuentaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaResponse> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/cuentas/{}", id);
        return ResponseEntity.ok(cuentaService.buscarPorId(id));
    }

    @GetMapping("/numero/{numeroCuenta}")
    public ResponseEntity<CuentaResponse> buscarPorNumero(@PathVariable String numeroCuenta) {
        log.info("GET /api/cuentas/numero/{}", numeroCuenta);
        return ResponseEntity.ok(cuentaService.buscarPorNumeroCuenta(numeroCuenta));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<CuentaResponse>> listarPorCliente(@PathVariable Long clienteId) {
        log.info("GET /api/cuentas/cliente/{}", clienteId);
        return ResponseEntity.ok(cuentaService.listarPorCliente(clienteId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CuentaResponse> actualizar(@PathVariable Long id, @Valid @RequestBody CuentaRequest request) {
        log.info("PUT /api/cuentas/{}", id);
        return ResponseEntity.ok(cuentaService.actualizar(id, request));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long id, @RequestParam EstadoCuenta estado) {
        log.info("PATCH /api/cuentas/{}/estado - {}", id, estado);
        cuentaService.cambiarEstado(id, estado);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        log.info("DELETE /api/cuentas/{}", id);
        cuentaService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}