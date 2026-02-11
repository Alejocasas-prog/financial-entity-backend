package com.crudpractica.finanzastestproyec.Repository;


import com.crudpractica.finanzastestproyec.Enums.TipoIdentifiacion;
import com.crudpractica.finanzastestproyec.Model.Cliente;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

/*Repositorio  para  operaciones de persistencias de  la entidad cliente
*
* Proporciona metodos  CRUD basicos y consultas personalizadas
* pera la gestion  de clientes en la base de datos
*
*
* */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /*
    * Busca un cliente por su tipo  y numero  de identificacion
    *
    * @param tipoIdentificacion, tipo de documenta del cliente
    * @param numeroIdentificacion, numero  de documento  del cliente
    * return Optional, contenido el cliente si existe, vacio en caso contrario
    *
    * */

    Optional<Cliente>findByTipoIdentifiacionAndTipoIdentifiacion(
            @NotNull(message = "El tipo de identificacion es obligatorio") TipoIdentifiacion tipoIdentifiacion, @NotNull(message = "El tipo de identificacion es obligatorio") TipoIdentifiacion tipoIdentifiacion2
    );

    /* Busca  un cliente por su correo  electronico
    *
    * @param correoElectronico correo del cliente
    * @return Optional contenido el cliente  si existe, vacio en caso contrario
    *
    **/

    Optional<Cliente>findByCorreoElectronico(String correElectronico);

    /*Verifica  si existe un cliente  con el numero identificacion dado
    *
    * @param numeroIdentificacion numero de docuemto a verificar
    * @return true si existe un cliente con ese numero,false en caso contrario
    *
    * */

    boolean existsByNumeroIdentificacion(String numeroIdentification);

    /*
    * Verifica si existe un cliente con el  correo  electronico dado
    *
    * @param correoElectronico correo a verificar
    * @return  true  is existe  un cliente con ese correo, false con ese caso contrario
    *
    * */
    boolean existsByCorreoElectronico(String correoElectronico);
}


