package com.crudpractica.finanzastestproyec.Servicestest;

import com.crudpractica.finanzastestproyec.Enums.TipoIdentifiacion;
import com.crudpractica.finanzastestproyec.Excepcion.BuisnessException;
import com.crudpractica.finanzastestproyec.Model.Cliente;
import com.crudpractica.finanzastestproyec.Repository.ClienteRepository;
import com.crudpractica.finanzastestproyec.Service.ClienteService;
import com.crudpractica.finanzastestproyec.dto.request.ClienteRequest;
import com.crudpractica.finanzastestproyec.dto.response.ClienteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/*
 * Clase de pruebas unitarias para el servicio de clientes.
 *
 * Esta clase contiene las pruebas que verifican el correcto funcionamiento
 * de las operaciones relacionadas con la gestión de clientes en la entidad financiera.
 * Se validan reglas de negocio como la mayoría de edad, unicidad de correos electrónicos
 * y restricciones para eliminar clientes con cuentas asociadas.
 *
 *
 * @author Equipo de Desarrollo
 * @version 1.0
 * @since 2026-02-11
 */
@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    /*
     * Mock del repositorio de clientes.
     * Simula las operaciones de base de datos sin conexión real.
     */
    @Mock
    private ClienteRepository clienteRepository;

    /*
     * Mock del ModelMapper para conversiones entre entidades y DTOs.
     */
    @Mock
    private ModelMapper modelMapper;

    /*
     * Instancia del servicio bajo pruebas.
     * Se inyectan automáticamente los mocks declarados arriba.
     */
    @InjectMocks
    private ClienteService clienteService;

    /*
     * Objeto de solicitud para crear un cliente.
     */
    private ClienteRequest clienteRequest;

    /*
     * Entidad de cliente para simular datos de la base de datos.
     */
    private Cliente cliente;

    /*
        Metodo que se ejecuta antes de cada test
     * Inicializa los objetos de prueba con datos válidos.
     *
     * Se crea un cliente mayor de edad (nacido en 1990) con todos los datos requeridos
     * para poder realizar las pruebas de creación, actualización y eliminación.
     *
     */
    @BeforeEach
    void setUp() {
        // Crear la solicitud de cliente con datos válidos
        clienteRequest = ClienteRequest.builder()
                .tipoIdentifiacion(TipoIdentifiacion.CEDULA_CIUDADANIA)
                .numeroIdentificacion("123456789")
                .nombres("Juan")
                .apellido("Pérez")
                .correoElectronico("juan.perez@example.com")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .build();

        // Crear la entidad cliente que simula lo que devuelve la BD
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setTipoIdentifiacion(TipoIdentifiacion.CEDULA_CIUDADANIA);
        cliente.setNumeroIdentificacion("123456789");
        cliente.setNombres("Juan");
        cliente.setApellido("Pérez");
        cliente.setCorreoElectronico("juan.perez@example.com");
        cliente.setFechaNacimiento(LocalDate.of(1990, 1, 1));
    }

    /*
     * Prueba la creación exitosa de un cliente mayor de edad.
     *
     * Este test verifica que cuando todos los datos son correctos y el cliente
     * tiene 18 años o más, el sistema permita su registro sin problemas.
     * Se valida que no existan duplicados de identificación o correo electrónico.
     *
     *
     * @throws Exception si ocurre algún error inesperado durante la prueba
     */
    @Test
    void testCrearCliente_MayorDeEdad_Exitoso() {
        // Configurar el comportamiento de los mocks
        when(modelMapper.map(any(ClienteRequest.class), eq(Cliente.class))).thenReturn(cliente);
        when(clienteRepository.existsByNumeroIdentificacion(anyString())).thenReturn(false);
        when(clienteRepository.existsByCorreoElectronico(anyString())).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        when(modelMapper.map(any(Cliente.class), eq(ClienteResponse.class))).thenReturn(new ClienteResponse());

        // Ejecutar el método bajo prueba
        ClienteResponse response = clienteService.crear(clienteRequest);

        // Verificar que la respuesta no sea nula
        assertNotNull(response, "La respuesta no debe ser nula");
        // Verificar que se haya llamado al método save exactamente una vez
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    /*
     * Prueba que el sistema rechace la creación de clientes menores de 18 años.
     *
     * Esta es una regla de negocio crítica para proteger a los menores de edad.
     * El sistema debe validar la fecha de nacimiento y lanzar una excepción
     * si el cliente no cumple con la mayoría de edad requerida.
     *
     *
     * @throws BusinessException cuando el cliente es menor de 18 años
     */
    @Test
    void testCrearCliente_MenorDeEdad_LanzaExcepcion() {
        // Modificar la fecha de nacimiento para que sea de un menor de edad
        clienteRequest.setFechaNacimiento(LocalDate.now().minusYears(15));
        cliente.setFechaNacimiento(LocalDate.now().minusYears(15));

        // Configurar el mock para devolver el cliente menor de edad
        when(modelMapper.map(any(ClienteRequest.class), eq(Cliente.class))).thenReturn(cliente);

        // Verificar que se lance la excepción esperada
        BuisnessException exception = assertThrows(BuisnessException.class,
                () -> clienteService.crear(clienteRequest),
                "Debe lanzar BusinessException para menores de edad");

        // Verificar que el mensaje de error sea el correcto
        assertTrue(exception.getMessage().contains("mayor de edad"),
                "El mensaje debe indicar que se requiere ser mayor de edad");
        // Verificar que nunca se intentó guardar en la BD
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    /*
     * Prueba que el sistema rechace correos electrónicos duplicados.
     *
     * Cada cliente debe tener un correo electrónico único en el sistema.
     * Esta prueba verifica que si ya existe un cliente con el mismo correo,
     * se lance una excepción y no se permita el registro duplicado.
     *
     *
     * @throws BusinessException cuando el correo ya está registrado
     */
    @Test
    void testCrearCliente_CorreoDuplicado_LanzaExcepcion() {
        // Configurar los mocks
        when(modelMapper.map(any(ClienteRequest.class), eq(Cliente.class))).thenReturn(cliente);
        when(clienteRepository.existsByNumeroIdentificacion(anyString())).thenReturn(false);
        // Simular que el correo ya existe
        when(clienteRepository.existsByCorreoElectronico(anyString())).thenReturn(true);

        // Verificar que se lance la excepción esperada
        BuisnessException exception = assertThrows(BuisnessException.class,
                () -> clienteService.crear(clienteRequest),
                "Debe lanzar BusinessException cuando el correo está duplicado");

        // Verificar que el mensaje mencione el correo electrónico
        assertTrue(exception.getMessage().contains("correo electrónico"),
                "El mensaje debe mencionar el correo electrónico duplicado");
        // Verificar que no se guardó nada en la BD
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    /*
     * Prueba la eliminación exitosa de un cliente sin cuentas asociadas.
     *
     * Solo se permite eliminar clientes que no tengan productos financieros
     * (cuentas) vinculados. Esta prueba verifica que cuando un cliente no tiene
     * cuentas, el sistema permita su eliminación correctamente.
     *
     *
     * @throws BusinessException si el cliente tiene cuentas asociadas
     */
    @Test
    void testEliminarCliente_SinCuentas_Exitoso() {
        // Configurar el mock para devolver un cliente sin cuentas
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        // Verificar que no se lance ninguna excepción
        assertDoesNotThrow(() -> clienteService.eliminar(1L),
                "No debe lanzar excepción si el cliente no tiene cuentas");

        // Verificar que se llamó al método delete
        verify(clienteRepository, times(1)).deleteById(1L);
    }

    /*
     * Prueba la búsqueda de un cliente por su ID.
     *
     * Esta operación es fundamental para consultar la información de un cliente.
     * Se verifica que el servicio devuelva correctamente los datos del cliente
     * cuando se proporciona un ID válido y existente.
     *
     *
     * @throws BusinessException si el cliente no existe
     */
    @Test
    void testBuscarPorId_Exitoso() {
        // Configurar los mocks para simular la búsqueda exitosa
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(modelMapper.map(any(Cliente.class), eq(ClienteResponse.class))).thenReturn(new ClienteResponse());

        // Ejecutar la búsqueda
        ClienteResponse response = clienteService.buscarporId(1L);

        // Verificar que la respuesta no sea nula
        assertNotNull(response, "La respuesta no debe ser nula");
        // Verificar que se haya llamado al repositorio
        verify(clienteRepository, times(1)).findById(1L);
    }
}
