package com.crudpractica.finanzastestproyec.ContollerTest;

import com.crudpractica.finanzastestproyec.Controller.ClienteController;
import com.crudpractica.finanzastestproyec.Enums.TipoIdentifiacion;
import com.crudpractica.finanzastestproyec.Service.ClienteService;
import com.crudpractica.finanzastestproyec.dto.request.ClienteRequest;
import com.crudpractica.finanzastestproyec.dto.response.ClienteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/*
 * Clase de pruebas para el controlador REST de clientes.
 *
 * Verifica que los endpoints del controlador funcionen correctamente
 * y devuelvan los códigos HTTP esperados.
 *
 * @author Equipo de Desarrollo
 * @version 1.0
 * @since 2026-02-11
 */
@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ClienteController clienteController;

    private ClienteRequest clienteRequest;
    private ClienteResponse clienteResponse;

    /*
     * Inicializa los datos de prueba antes de cada test.
     */
    @BeforeEach
    void setUp() {
        // Crear request
        clienteRequest = new ClienteRequest();
        clienteRequest.setTipoIdentifiacion(TipoIdentifiacion.CEDULA_CIUDADANIA);
        clienteRequest.setNumeroIdentificacion("123456789");
        clienteRequest.setNombres("Juan");
        clienteRequest.setApellido("Pérez");
        clienteRequest.setCorreoElectronico("juan.perez@example.com");
        clienteRequest.setFechaNacimiento(LocalDate.of(1990, 1, 1));

        // Crear response
        clienteResponse = new ClienteResponse();
        clienteResponse.setId(1L);
        clienteResponse.setTipoIdentifiacion(TipoIdentifiacion.CEDULA_CIUDADANIA);
        clienteResponse.setNumeroIdentificacion("123456789");
        clienteResponse.setNombres("Juan");
        clienteResponse.setApellidos("Pérez");
        clienteResponse.setCorreElectronico("juan.perez@example.com");
        clienteResponse.setEdad(36);
        clienteResponse.setCantidadCuentas(0);
    }

    /*
     * Verifica que el endpoint de creación devuelva código HTTP 201 Created.
     *
     * El estándar REST indica que al crear un recurso nuevo se debe
     * devolver código 201 junto con los datos del recurso creado.
     *
     */
    @Test
    void testCrearCliente_Retorna201() {
        // Configurar el mock
        when(clienteService.crear(any(ClienteRequest.class))).thenReturn(clienteResponse);

        // Ejecutar el método del controlador
        ResponseEntity<ClienteResponse> response = clienteController.crear(clienteRequest);

        // Verificaciones
        assertNotNull(response, "La respuesta no debe ser nula");
        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Debe devolver código 201");
        assertNotNull(response.getBody(), "El body no debe ser nulo");
        assertEquals("Juan", response.getBody().getNombres(), "El nombre debe ser Juan");

        // Verificar que se llamó al servicio
        verify(clienteService, times(1)).crear(any(ClienteRequest.class));
    }

    /*
     * Verifica que el endpoint de listado devuelva código HTTP 200 OK.
     * <p>
     * El endpoint debe devolver una lista con todos los clientes
     * registrados en el sistema junto con código de éxito 200.
     *
     */
    @Test
    void testListarClientes_Retorna200() {
        // Preparar lista de clientes
        List<ClienteResponse> clientes = Arrays.asList(clienteResponse);
        when(clienteService.listarTodos()).thenReturn(clientes);

        // Ejecutar el método del controlador
        ResponseEntity<List<ClienteResponse>> response = clienteController.listarTodos();

        // Verificaciones
        assertNotNull(response, "La respuesta no debe ser nula");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Debe devolver código 200");
        assertNotNull(response.getBody(), "El body no debe ser nulo");
        assertEquals(1, response.getBody().size(), "Debe haber 1 cliente en la lista");
        assertEquals("Juan", response.getBody().get(0).getNombres(), "El nombre debe ser Juan");

        // Verificar que se llamó al servicio
        verify(clienteService, times(1)).listarTodos();
    }

    /*
     * Verifica que la búsqueda por ID devuelva el cliente correcto.
     *
     * Cuando se busca un cliente por su ID, el endpoint debe devolver
     * código 200 junto con los datos completos del cliente solicitado.
     *
     */
    @Test
    void testBuscarClientePorId_Retorna200() {
        // Configurar el mock
        when(clienteService.buscarporId(1L)).thenReturn(clienteResponse);

        // Ejecutar el método del controlador
        ResponseEntity<ClienteResponse> response = clienteController.buscarPorId(1L);

        // Verificaciones
        assertNotNull(response, "La respuesta no debe ser nula");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Debe devolver código 200");
        assertNotNull(response.getBody(), "El body no debe ser nulo");
        assertEquals(1L, response.getBody().getId(), "El ID debe ser 1");
        assertEquals("Juan", response.getBody().getNombres(), "El nombre debe ser Juan");

        // Verificar que se llamó al servicio
        verify(clienteService, times(1)).buscarporId(1L);
    }
}