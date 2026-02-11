package com.crudpractica.finanzastestproyec.Controller;

import com.crudpractica.finanzastestproyec.Service.TransaccionService;
import com.crudpractica.finanzastestproyec.dto.request.TransaccionRequest;
import com.crudpractica.finanzastestproyec.dto.response.TransaccionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transacciones")
@RequiredArgsConstructor
@Slf4j
public class TransaccionController {

    private final TransaccionService transaccionService;

    @PostMapping
    public ResponseEntity<TransaccionResponse> crear(@Valid @RequestBody TransaccionRequest request) {
        log.info("POST /api/transacciones - Crear transacción");
        TransaccionResponse response = transaccionService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TransaccionResponse>> listarTodas() {
        log.info("GET /api/transacciones - Listar todas");
        return ResponseEntity.ok(transaccionService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransaccionResponse> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/transacciones/{}", id);
        return ResponseEntity.ok(transaccionService.buscarPorId(id));
    }

    @GetMapping("/cuenta/{cuentaId}")
    public ResponseEntity<List<TransaccionResponse>> listarPorCuenta(@PathVariable Long cuentaId) {
        log.info("GET /api/transacciones/cuenta/{} - Estado de cuenta", cuentaId);
        return ResponseEntity.ok(transaccionService.listarPorCuenta(cuentaId));
    }
}
