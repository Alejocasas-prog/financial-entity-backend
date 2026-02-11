package com.crudpractica.finanzastestproyec.Excepcion;





/*Manejar global de excepciones para la aplicacion
* captura y procesa  todas las  excepciones lanzadas por los controladoes
* proporcionando respuestas Json consistentes y amigables al cliente*/

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@Slf4j
public class GlobalExceptinoHandler {

    /* Maneja  excepcion de la logica de negocio (BussinesException)
     *
     * @param ex excepcion  de negocio lanza
     * @return responseEntity con detalles del error y codigo 400
     *
     * */

    @ExceptionHandler(BuisnessException.class)
    public ResponseEntity<ErrorResponse> handlerBusinessException(BuisnessException ex) {
        log.error("Error de negocio: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Error de negocio")
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

    }

    /*
     *
     * Menaja  errores de validacion de campos(@valid)
     * @param ex excepcion  de validacion
     * @return ResponseEntity  con mapa  de errores  por campo  y codigo 400
     * */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String campo = ((FieldError) error).getField();
            String mensaje = error.getDefaultMessage();
            errores.put(campo, mensaje);

        });
        log.error("Errores de validacion : {}", errores);

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("errors", "Error de validacion");
        response.put("errores", errores);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

    }
    /*Maneja cualquier otra excepcion no completada
     *
     * @param ex excepcion generica
     * @return ResponseEntity con mensaje generico y codigo 500
     *
     * */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Error inesperado", ex);

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Error Interno del Servidor")
                .message("Ha ocurrido un error inesperado. Por favor contacte al administrador.")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);

    }

    /*Clase interna  para estructurar respuetas de error
    * */
    @lombok.Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    static class ErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
    }


}

