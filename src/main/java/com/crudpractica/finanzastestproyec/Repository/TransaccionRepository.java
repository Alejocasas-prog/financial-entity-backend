package com.crudpractica.finanzastestproyec.Repository;

/*Repositorio para operacionesde persistencia en la entidad  transacciones
*
* proporciona metodos Crud basicos y consultas personalizadas
* para la gestion  de transacciones financieras en la base de datos
* */

import com.crudpractica.finanzastestproyec.Enums.TipoTrasaccion;
import com.crudpractica.finanzastestproyec.Model.Cuenta;
import com.crudpractica.finanzastestproyec.Model.Transaccion;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {


    /*
    * Busca todos as transacciones realizadas desde una cuenta especifica
    *
    * @param cuentaOringenId identificador de la cuenta origen
    * @return lista de transacciones realizadas desde la cuenta
    *
    * */

    List<Transaccion> findByCuentaOrigenId(Long cuentaOrigenId);

    /*
    * Busca todas las transacciones de una cuenta (origen o destino)
    *
    * @param cuentaId identificador de la cuenta
    * @return lista de toda las transacciones relacionadas con la cuenta
    * */

    @Query("SELECT t FROM Transaccion t WHERE t.cuentaOrigen.id = :cuentaId OR t.cuentaDestino.id = :cuentaId ORDER BY  t.fecha DESC ")
    List<Transaccion>findAllByCuentaId(@Param("cuentaId")Long cuentaId);


    /*Busca transacciones de una cuenta en un rango de fechas
    *
    * @param cuentaId, indetificador de la cuenta
    *@param fechaInicio feche inicial de rango
    * @param fechaFin , fecha final del rango
    * @return listade transacciones en el periodo especificado
    *
    *
    * */

    @Query("SELECT t FROM  Transaccion t WHERE (t.cuentaOrigen.id = :cuentaId OR t.cuentaDestino.id = :cuentaId)" + "AND  t.fecha BETWEEN  : fechaInicio AND : fechaFin ORDER BY t.fecha DESC ")

    List<Transaccion>findByCuentaIdAndFechaBetween(
            @Param("cuentaId")Long CuentId,
            @Param("fechaInicio")LocalDateTime fechaInicio,
            @Param("fechaFin")LocalDateTime fechaFin
            );

    /*busca transacciones por tipo
    *
    * @param tipoTransaccion, tipo  de transaccion(Consignacion, Retiro, Transferencia)
    * @return lista de transacciones del tipo especificado
    *
    * */    

    List<Transaccion>findByTipoTrasaccion(TipoTrasaccion tipoTrasaccion);
    /*
    * busca las ultimas N transacciones de una cuenta
    * @param cuentaId identificador de la cuenta
    * @return lista  de las ultimas transacciones
    *
    **/

    @Query("SELECT t FROM Transaccion  t WHERE t.cuentaOrigen.id = :cuentaId OR t.cuentaDestino.id = :cuentaId" + " ORDER BY t.fecha DESC")

    List<Transaccion> findUltimasTransaccionesByCuenta(@Param("cuentaId")Long cuentaId);
}
