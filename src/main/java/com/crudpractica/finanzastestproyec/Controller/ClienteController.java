package com.crudpractica.finanzastestproyec.Controller;


import com.crudpractica.finanzastestproyec.Service.ClienteService;
import com.crudpractica.finanzastestproyec.dto.request.ClienteRequest;
import com.crudpractica.finanzastestproyec.dto.response.ClienteResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Slf4j
public class ClienteController {
    private final ClienteService clienteService;
    private final ResourceUrlProvider resourceUrlProvider;

    /*
    * crea un nuevo cliente
    * Endpoint: POst /api/clientes
    *
    * @param request datos  cliente a crear
    * @return ResponseEntity con el cliente  creado y codigo 201
    * */

    @PostMapping
    public ResponseEntity<ClienteResponse>crear(@Valid @RequestBody ClienteRequest request){
        log.info("POST /api/clientes - crear nuevo cliente");
        ClienteResponse response = clienteService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /*Lista todos los clientes registrados
    * Enpoints: GET /api/clientes
    * @return ResponseEntity con lista de clientes y codigo 200
    *
    * */

    @GetMapping
    public ResponseEntity<List<ClienteResponse>>listarTodos(){
        log.info("GET /api/clientes - listar todos los clientes");
        List<ClienteResponse>clientes =  clienteService.listarTodos();
        return ResponseEntity.ok(clientes);

    }

    /*Busca un clienta por  su ID
    * Endpoints : Get /api/clientes/{id}}
    * @param id identificador  del cliente
    * @return ResponseEntity con cliente encontrado y codigo  200
    * */

    @GetMapping
    public ResponseEntity<ClienteResponse>buscarPorId(@PathVariable Long id){
        log.info("GET/api/clientes/{} - buscar cliente por id", id);
        ClienteResponse response = clienteService.buscarporId(id);
        return ResponseEntity.ok(response);

    }

    /*Actualiza  la informacion  de un cliente  existente
    * Endpoint: PUT/api/clientes/{id}
    *
    * @param id  identificador  del cliente a actualziar
    * @param request nuevos datos  del cliente
    * @return Responsability con el cliente actualizado  y codigo 200
    * */

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse>actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequest request){
        log.info("PUT /api/clientes/{} - actualizar cliente", id);
        ClienteResponse response = clienteService.actualizar(id,request);
        return  ResponseEntity.ok(response);
    }
    /*Elimina  un cliente del sistema
    *
    * Endpoint: Delete/api/clientes/{id}
    * el cliente no puede tener productos financieros  vinculados
    *
    * @param id  identificador del cliente a eliminar
    * @return ResponseEntity vacio con codigo 204
    *  */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void>eliminar(@PathVariable Long id){
        log.info("DELETE /api/clientes/{} - eliminar cliente" , id);
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
