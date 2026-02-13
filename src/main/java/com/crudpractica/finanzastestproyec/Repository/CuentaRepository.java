package com.crudpractica.finanzastestproyec.Repository;




/*
*
* Repositorio para operaciones de persistencia  de la entidad Cuenta
*
*Proporciona metodos Crud basicos y consultas personalizadas
* para la gestion de cuentas financieras en la base de datos
*
*/

import com.crudpractica.finanzastestproyec.Enums.EstadoCuenta;
import com.crudpractica.finanzastestproyec.Model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    /*
    * Busca una  cuenta por numero unico
    *
    * @param numeroCuenta numero de cuenta a buscar
    * @return  Optional  conteniendo la cuenta  si existe, vacio en caso contrario
    *
    * */
     Optional<Cuenta>findByNumeroCuenta(String numeroCuenta);


     /*
     * Busca todas las  cuentas asociadas a un cliente
     *
     *
     *
     * @param clienteId identificador del cliente
     * return lista de cuentas del cliente
     * */

    List<Cuenta> findByClienteId(Long clienteId);


    /*Busca todas las cuentas de un cliente con un estado especifico
    *
    * @oaram clienteId identificador  del cliente
    * @param estado, estado de la cuenta (ACTIVA, INACTIVA, CANCELADA)
    * return listade cuentas que cumplen los criterios
    *
    * */

    List<Cuenta>findByClienteIdAndEstado(Long ClienteId, EstadoCuenta estado);

    /*
    * Verifica si existe una cuenta  con el numero  dado
    *
    *
    * @param numeroCuenta, numero de cuenta a verificar
    * @return true si existe la cuenta, false en caso contrario
    *
    *  */

    boolean existsByNumeroCuenta(String numeroCuenta);

    /*
    * cuenta el numero  de cuentas activas de un cliente
    *
    * @param clienteId identificador del cliente
    * @return cantidadde cuentas activas
    * */

    Long countByClienteIdAndEstado(Long clienteId, EstadoCuenta estado);

}
