package com.crudpractica.finanzastestproyec.Servicestest;

import com.crudpractica.finanzastestproyec.Enums.EstadoCuenta;
import com.crudpractica.finanzastestproyec.Enums.TipoCuenta;
import com.crudpractica.finanzastestproyec.Enums.TipoTrasaccion;
import com.crudpractica.finanzastestproyec.Excepcion.BuisnessException;
import com.crudpractica.finanzastestproyec.Model.Cuenta;
import com.crudpractica.finanzastestproyec.Model.Transaccion;
import com.crudpractica.finanzastestproyec.Repository.CuentaRepository;
import com.crudpractica.finanzastestproyec.Repository.TransaccionRepository;
import com.crudpractica.finanzastestproyec.Service.TransaccionService;
import com.crudpractica.finanzastestproyec.dto.request.TransaccionRequest;
import com.crudpractica.finanzastestproyec.dto.response.TransaccionResponse;
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
 * Clase de pruebas unitarias para el servicio de transacciones bancarias.
 *
 * Se validan las tres operaciones principales: consignaciones (depósitos),
 * retiros y transferencias entre cuentas. Se verifican reglas de negocio
 * como saldos suficientes, estados de cuentas activas, y restricciones
 * para evitar transferencias a la misma cuenta.
 *
 *
 * @author Equipo de Desarrollo
 * @version 1.0
 * @since 2026-02-11
 */
@ExtendWith(MockitoExtension.class)
class TransaccionServiceTest {

    /*
     * Mock del repositorio de transacciones.
     */
    @Mock
    private TransaccionRepository transaccionRepository;

    /*
     * Mock del repositorio de cuentas para validar y actualizar saldos.
     */
    @Mock
    private CuentaRepository cuentaRepository;

    /*
     * Mock del ModelMapper para conversiones entre DTOs y entidades.
     */
    @Mock
    private ModelMapper modelMapper;

    /*
     * Instancia del servicio bajo prueba con mocks inyectados.
     */
    @InjectMocks
    private TransaccionService transaccionService;

    /*
     * Cuenta de origen para las transacciones de prueba.
     */
    private Cuenta cuentaOrigen;

    /*
     * Cuenta de destino para las transferencias de prueba.
     */
    private Cuenta cuentaDestino;

    /*
     * Objeto de solicitud para realizar transacciones.
     */
    private TransaccionRequest transaccionRequest;

    /*
     * Inicializa las cuentas de prueba antes de cada test.
     *
     * Se crean dos cuentas: una de ahorros con $1000 de saldo y otra
     * corriente con $500. Ambas están en estado activo para poder
     * realizar operaciones sobre ellas.
     *
     */
    @BeforeEach
    void setUp() {
        // Crear cuenta de origen (Ahorros con $1000)
        cuentaOrigen = new Cuenta();
        cuentaOrigen.setId(1L);
        cuentaOrigen.setNumeroCuenta("5312345678");
        cuentaOrigen.setTipoCuenta(TipoCuenta.AHORRO);
        cuentaOrigen.setEstado(EstadoCuenta.ACTIVA);
        cuentaOrigen.setSaldo(new BigDecimal("1000.00"));

        // Crear cuenta de destino (Corriente con $500)
        cuentaDestino = new Cuenta();
        cuentaDestino.setId(2L);
        cuentaDestino.setNumeroCuenta("3387654321");
        cuentaDestino.setTipoCuenta(TipoCuenta.CORRIENTE);
        cuentaDestino.setEstado(EstadoCuenta.ACTIVA);
        cuentaDestino.setSaldo(new BigDecimal("500.00"));
    }

    /*
     * Verifica que una consignación aumente correctamente el saldo.
     *
     * La consignación es un depósito de dinero a una cuenta. Esta operación
     * es la más simple ya que solo incrementa el saldo sin validaciones
     * complejas. Se verifica que el saldo final sea la suma del saldo
     * inicial más el monto consignado.
     *
     */
    @Test
    void testProcesarConsignacion_Exitosa() {
        // Crear solicitud de consignación de $500
        transaccionRequest = TransaccionRequest.builder()
                .tipoTransaccion(TipoTrasaccion.CONSIGNACION)
                .monto(new BigDecimal("500.00"))
                .cuentaOrigenId(1L)
                .build();

        // Configurar los mocks
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaOrigen));
        when(transaccionRepository.save(any(Transaccion.class))).thenAnswer(invocation -> {
            Transaccion t = invocation.getArgument(0);
            t.setId(1L);
            t.setCuentaOrigen(cuentaOrigen);
            return t;
        });
        when(modelMapper.map(any(Transaccion.class), eq(TransaccionResponse.class)))
                .thenReturn(new TransaccionResponse());

        // Ejecutar la consignación
        TransaccionResponse response = transaccionService.crear(transaccionRequest);

        // Verificaciones
        assertNotNull(response, "La respuesta no debe ser nula");
        assertEquals(new BigDecimal("1500.00"), cuentaOrigen.getSaldo(),
                "El saldo debe incrementarse en $500");
        verify(cuentaRepository, times(1)).save(cuentaOrigen);
    }

    /*
     * Verifica que no se permitan retiros con saldo insuficiente.
     *
     * Esta es una regla de negocio fundamental para proteger contra sobregiros.
     * El sistema debe validar que el saldo disponible sea mayor o igual al
     * monto que se desea retirar. Si no hay fondos suficientes, debe lanzar
     * una excepción y no permitir la operación.
     *
     *
     * @throws BusinessException cuando el saldo es insuficiente
     */
    @Test
    void testProcesarRetiro_SaldoInsuficiente_LanzaExcepcion() {
        // Intentar retirar $1500 cuando solo hay $1000
        transaccionRequest = TransaccionRequest.builder()
                .tipoTransaccion(TipoTrasaccion.RETIRO)
                .monto(new BigDecimal("1500.00"))
                .cuentaOrigenId(1L)
                .build();

        // Configurar el mock
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaOrigen));

        // Verificar que se lance la excepción esperada
        BuisnessException exception = assertThrows(BuisnessException.class,
                () -> transaccionService.crear(transaccionRequest),
                "Debe lanzar BusinessException cuando el saldo es insuficiente");

        // Verificar el mensaje de error
        assertTrue(exception.getMessage().contains("Saldo insuficiente"),
                "El mensaje debe indicar que no hay fondos suficientes");
        // Verificar que no se guardó la transacción
        verify(transaccionRepository, never()).save(any(Transaccion.class));
    }

    /*
     * Verifica que no se permitan retiros de cuentas inactivas.
     *
     * Solo las cuentas en estado ACTIVA pueden realizar operaciones de débito.
     * Esta regla protege contra operaciones en cuentas suspendidas o canceladas.
     * Se valida que si la cuenta no está activa, se rechace el retiro.
     *
     *
     * @throws BusinessException cuando la cuenta no está en estado ACTIVA
     */
    @Test
    void testProcesarRetiro_CuentaInactiva_LanzaExcepcion() {
        // Cambiar el estado de la cuenta a INACTIVA
        cuentaOrigen.setEstado(EstadoCuenta.INACTIVA);

        // Intentar hacer un retiro válido en monto pero cuenta inactiva
        transaccionRequest = TransaccionRequest.builder()
                .tipoTransaccion(TipoTrasaccion.RETIRO)
                .monto(new BigDecimal("100.00"))
                .cuentaOrigenId(1L)
                .build();

        // Configurar el mock
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaOrigen));

        // Verificar que se lance la excepción
        BuisnessException exception = assertThrows(BuisnessException.class,
                () -> transaccionService.crear(transaccionRequest),
                "Debe lanzar BusinessException cuando la cuenta está inactiva");

        // Verificar que el mensaje mencione el estado
        assertTrue(exception.getMessage().contains("activa"),
                "El mensaje debe indicar que la cuenta debe estar activa");
    }

    /*
     * Verifica que una transferencia actualice ambas cuentas correctamente.
     *
     * La transferencia es la operación más compleja porque involucra dos cuentas.
     * Se debe restar el monto de la cuenta origen y sumarlo a la cuenta destino
     * de forma atómica. Este test valida que ambos saldos se actualicen
     * correctamente y que se guarden ambas cuentas en la base de datos.
     *
     */
    @Test
    void testProcesarTransferencia_Exitosa() {
        // Transferir $300 de cuenta origen a destino
        transaccionRequest = TransaccionRequest.builder()
                .tipoTransaccion(TipoTrasaccion.TRANSFERENCIA)
                .monto(new BigDecimal("300.00"))
                .cuentaOrigenId(1L)
                .cuentaDestinoId(2L)
                .build();

        // Configurar los mocks
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaOrigen));
        when(cuentaRepository.findById(2L)).thenReturn(Optional.of(cuentaDestino));
        when(transaccionRepository.save(any(Transaccion.class))).thenAnswer(invocation -> {
            Transaccion t = invocation.getArgument(0);
            t.setId(1L);
            t.setCuentaOrigen(cuentaOrigen);
            t.setCuentaDestino(cuentaDestino);
            return t;
        });
        when(modelMapper.map(any(Transaccion.class), eq(TransaccionResponse.class)))
                .thenReturn(new TransaccionResponse());

        // Ejecutar la transferencia
        TransaccionResponse response = transaccionService.crear(transaccionRequest);

        // Verificaciones
        assertNotNull(response, "La respuesta no debe ser nula");
        assertEquals(new BigDecimal("700.00"), cuentaOrigen.getSaldo(),
                "El saldo origen debe disminuir a $700 ($1000 - $300)");
        assertEquals(new BigDecimal("800.00"), cuentaDestino.getSaldo(),
                "El saldo destino debe aumentar a $800 ($500 + $300)");
        // Verificar que se guardaron ambas cuentas
        verify(cuentaRepository, times(2)).save(any(Cuenta.class));
    }

    /*
     * Verifica que no se permitan transferencias a la misma cuenta.
     *
     * Esta validación previene operaciones sin sentido donde el origen
     * y destino sean la misma cuenta. El sistema debe rechazar estas
     * transacciones para mantener la integridad de los datos.
     *
     *
     * @throws BusinessException cuando origen y destino son la misma cuenta
     */
    @Test
    void testProcesarTransferencia_MismaCuenta_LanzaExcepcion() {
        // Intentar transferir de la cuenta 1 a la cuenta 1
        transaccionRequest = TransaccionRequest.builder()
                .tipoTransaccion(TipoTrasaccion.TRANSFERENCIA)
                .monto(new BigDecimal("100.00"))
                .cuentaOrigenId(1L)
                .cuentaDestinoId(1L) // Mismo ID que origen
                .build();

        // Configurar el mock
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaOrigen));

        // Verificar que se lance la excepción
        BuisnessException exception = assertThrows(BuisnessException.class,
                () -> transaccionService.crear(transaccionRequest),
                "Debe lanzar BusinessException cuando se intenta transferir a la misma cuenta");

        // Verificar el mensaje
        assertTrue(exception.getMessage().contains("misma cuenta"),
                "El mensaje debe indicar que no se puede transferir a la misma cuenta");
    }
}