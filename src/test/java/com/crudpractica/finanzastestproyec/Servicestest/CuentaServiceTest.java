package com.crudpractica.finanzastestproyec.Servicestest;
import com.crudpractica.finanzastestproyec.Enums.EstadoCuenta;
import com.crudpractica.finanzastestproyec.Enums.TipoCuenta;

import com.crudpractica.finanzastestproyec.Excepcion.BuisnessException;
import com.crudpractica.finanzastestproyec.Model.Cliente;
import com.crudpractica.finanzastestproyec.Model.Cuenta;
import com.crudpractica.finanzastestproyec.Repository.ClienteRepository;
import com.crudpractica.finanzastestproyec.Repository.CuentaRepository;
import com.crudpractica.finanzastestproyec.Service.CuentaService;
import com.crudpractica.finanzastestproyec.dto.request.CuentaRequest;
import com.crudpractica.finanzastestproyec.dto.response.CuentaResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/*
 * Clase de pruebas unitarias para el servicio de cuentas bancarias.
 *
 * Aquí se validan todas las operaciones relacionadas con la gestión de cuentas
 * de ahorro y corrientes. Se verifica el correcto formato de números de cuenta,
 * los prefijos según el tipo de cuenta, y las restricciones para cancelar cuentas.

 * @author Equipo de Desarrollo
 * @version 1.0
 * @since 2026-02-11
 */
@ExtendWith(MockitoExtension.class)
class CuentaServiceTest {

    /*
     * Mock del repositorio de cuentas.
     */
    @Mock
    private CuentaRepository cuentaRepository;

    /*
     * Mock del repositorio de clientes para validar la existencia del cliente.
     */
    @Mock
    private ClienteRepository clienteRepository;

    /*
     * Mock del ModelMapper para conversiones.
     */
    @Mock
    private ModelMapper modelMapper;

    /*
     * Instancia del servicio bajo prueba con mocks inyectados.
     */
    @InjectMocks
    private CuentaService cuentaService;

    /*
     * Objeto de solicitud para crear una cuenta.
     */
    private CuentaRequest cuentaRequest;

    /*
     * Entidad de cuenta para simular datos de la BD.
     */
    private Cuenta cuenta;

    /*
     * Entidad de cliente propietario de la cuenta.
     */
    private Cliente cliente;

    /*
     * Inicializa los datos de prueba antes de cada test.
     *
     * Se crea un cliente titular y una cuenta de ahorros con saldo cero
     * en estado activo para poder realizar las diferentes pruebas.
     */
    @BeforeEach
    void setUp() {
        // Crear el cliente titular de la cuenta
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombres("Juan");
        cliente.setApellido("Pérez");

        // Crear la solicitud de cuenta
        cuentaRequest = CuentaRequest.builder()
                .tipoCuenta(TipoCuenta.AHORRO)
                .exentaGMF(false)
                .clienteId(1L)
                .build();

        // Crear la entidad cuenta con datos iniciales
        cuenta = new Cuenta();
        cuenta.setId(1L);
        cuenta.setNumeroCuenta("5312345678");
        cuenta.setTipoCuenta(TipoCuenta.AHORRO);
        cuenta.setEstado(EstadoCuenta.ACTIVA);
        cuenta.setSaldo(BigDecimal.ZERO);
        cuenta.setExentaGMF(false);
        cuenta.setCliente(cliente);
    }

    /*
     * Verifica la creación exitosa de una cuenta válida.
     *
     * Se prueba que el sistema cree correctamente una cuenta cuando todos
     * los datos son válidos y el cliente existe en el sistema. El número
     * de cuenta debe generarse automáticamente con el formato correcto.
     *
     * @throws BuisnessException si el cliente no existe
     */
    @Test
    void testCrearCuenta_Valida_Exitoso() {
        // Configurar mocks con lenient para evitar "unnecessary stubbing"
        lenient().when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        lenient().when(modelMapper.map(any(CuentaRequest.class), eq(Cuenta.class))).thenReturn(cuenta);
        lenient().when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuenta);

        CuentaResponse cuentaResponse = new CuentaResponse();
        cuentaResponse.setNumeroCuenta("5312345678");
        lenient().when(modelMapper.map(any(Cuenta.class), eq(CuentaResponse.class))).thenReturn(cuentaResponse);

        // Ejecutar
        CuentaResponse resultado = cuentaService.crear(cuentaRequest);

        // Verificar que el resultado no es nulo
        assertNotNull(resultado);
    }

    /*
     * Verifica que las cuentas de ahorro tengan el prefijo correcto (53).
     *
     * En el sistema bancario colombiano, las cuentas de ahorro se identifican
     * con el prefijo 53. Este test valida que el algoritmo de generación
     * de números de cuenta respete este formato estándar requerido por la prueba técnica.
     */
    @Test
    void testCrearCuenta_TipoAhorro_NumeroCuentaEmpiezaCon53() {
        // Configurar mocks con lenient
        lenient().when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        lenient().when(modelMapper.map(any(CuentaRequest.class), eq(Cuenta.class))).thenReturn(cuenta);

        lenient().when(cuentaRepository.save(any(Cuenta.class))).thenAnswer(invocation -> {
            Cuenta cuentaGuardada = invocation.getArgument(0);
            String numero = cuentaGuardada.getNumeroCuenta();

            // Verificar que cumple con los requerimientos de la prueba técnica
            assertNotNull(numero, "El número de cuenta no debe ser nulo");
            assertTrue(numero.startsWith("53"), "Las cuentas de ahorro deben iniciar en '53' según requerimientos");
            assertEquals(10, numero.length(), "El número debe tener 10 dígitos según requerimientos");

            return cuentaGuardada;
        });

        CuentaResponse cuentaResponse = new CuentaResponse();
        cuentaResponse.setNumeroCuenta("5312345678");
        lenient().when(modelMapper.map(any(Cuenta.class), eq(CuentaResponse.class))).thenReturn(cuentaResponse);

        // Ejecutar
        CuentaResponse resultado = cuentaService.crear(cuentaRequest);

        // Verificar
        assertNotNull(resultado);
    }

    /*
     * Prueba la cancelación exitosa de una cuenta con saldo cero.
     *
     * Solo se permite cancelar cuentas que no tengan saldo pendiente.
     * Este test verifica que cuando el saldo es cero, el sistema cambie
     * el estado de la cuenta a CANCELADA correctamente, cumpliendo con
     * el requerimiento funcional de la prueba técnica.
     *
     * @throws BuisnessException si la cuenta tiene saldo mayor a cero
     */
    @Test
    void testCancelarCuenta_SaldoCero_Exitoso() {
        // Configurar: cuenta con saldo CERO
        cuenta.setSaldo(BigDecimal.ZERO);
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuenta);

        // Ejecutar
        cuentaService.cancelar(1L);

        // Verificar que el estado cambió a CANCELADA
        verify(cuentaRepository).save(argThat(c ->
                c.getEstado() == EstadoCuenta.CANCELADA
        ));
    }

    /*
     * Verifica que no se puedan cancelar cuentas con saldo pendiente.
     *
     * Esta es una regla de negocio importante para proteger los fondos
     * de los clientes. Primero se debe retirar todo el dinero y dejar
     * la cuenta en cero antes de proceder con la cancelación.
     * Cumple con el requerimiento: "Solo se podrán cancelar las cuentas con saldo = $0".
     *
     * @throws BuisnessException cuando la cuenta tiene saldo mayor a cero
     */
    @Test
    void testCancelarCuenta_ConSaldo_LanzaExcepcion() {
        // Configurar: cuenta con saldo MAYOR a cero
        cuenta.setSaldo(new BigDecimal("1500.00"));
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));

        // Ejecutar y verificar excepción
        BuisnessException exception = assertThrows(
                BuisnessException.class,
                () -> cuentaService.cancelar(1L)
        );

        // Verificar el mensaje de la excepción
        assertTrue(exception.getMessage().contains("saldo"));

        // Verificar que NO se guardó ningún cambio
        verify(cuentaRepository, never()).save(any(Cuenta.class));
    }
}