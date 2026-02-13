package com.crudpractica.finanzastestproyec.Service;


/*
* Servicio para gestionar operaciones de negocio  relacionadas con clientes
*
*
* implementada la logica de negocio para el crud de clientes
*  incluyendo validaciones de edad, identificacion y emai, y restricciones
* para la eliminacion de clientesocn cuentas activas
* */

import org.springframework.transaction.annotation.Transactional;
import com.crudpractica.finanzastestproyec.Excepcion.BuisnessException;
import com.crudpractica.finanzastestproyec.Model.Cliente;
import com.crudpractica.finanzastestproyec.Repository.ClienteRepository;
import com.crudpractica.finanzastestproyec.dto.request.ClienteRequest;
import com.crudpractica.finanzastestproyec.dto.response.ClienteResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j


public class ClienteService {
    private final ClienteRepository clienteRepository;
    private final ModelMapper modelMapper;



    /*
     *
     * crea un nuevo cliente en el sistema
     *
     * validaciones aplicadas
     *
     * el cliente debe ser mayor de edad de 18 años
     *   el numero de identificacion no debe  estar  resgistrado
     * el correo electronico no debe estar resgistradd
     *
     *
     * @param  request datos del cliente a crear
     * @return clienteResponse con la información del cliente creado
     * @throws BusinnesException si o cumple validaciones de negocio*/

    @Transactional
    public ClienteResponse crear(ClienteRequest request) {
        log.info("creando nuevo cliente con identificacion {}", request.getNumeroIdentificacion());

        //mapear DTO a entidad

        Cliente Cliente = modelMapper.map(request, Cliente.class);


        //validación: cliente debe ser mayor  de edad

        if (!Cliente.esMayorDeEdad()) {
            log.error("Intento de crear cliente menor de edad :{} años", Cliente.calcularEdad());
            throw new BuisnessException("El cliente debe ser mayor de edad (18 años). Edad actual: " + Cliente.calcularEdad() + " años");        }

        //validación:número de identificación único
        if (clienteRepository.existsByNumeroIdentificacion(request.getNumeroIdentificacion())) {
            throw new BuisnessException("ya existe un cliente con el numero de identificación" + request.getNumeroIdentificacion());
        }
        //validación:correo electrónico único
        if (clienteRepository.existsByCorreoElectronico(request.getCorreoElectronico())) {
            log.error("correo electrónico ya existe: {}", request.getCorreoElectronico());
            throw new BuisnessException("ya existe un cliente con el correo electrónico :" + request.getCorreoElectronico());
        }
        //Guardar cliente

        Cliente clienteGuardado = clienteRepository.save(Cliente);
        log.info("cliente creado exitosamente con ID: {} ", clienteGuardado.getId());
        return convertirAResponse(clienteGuardado);
    }
        /*Actualiza la información  de un cliente existente
        *
        * validaciones aplicadas
        *
        * el cliente debe existir
        * el correo no debe estar  usado por otro cliente
        * la fecha  de modificación se actualiza automáticamente
        *
        * @param id identificador del cliente a actualizar
        * @param request nuevos datos del cliente
        * @return  ClienteResponse con  la información actualizada
        * @throws BusinessException si el cliente o no existe o el email esta duplicado
        */
         @Transactional
        public ClienteResponse actualizar(Long id, ClienteRequest request) {
             log.info("Actualizar cliente ID: {}", id);

             //validar que el cliente existe

             Cliente ClienteExistente = clienteRepository.findById(id)
                     .orElseThrow(() -> {
                         log.error("Cliente no encontrado con ID: {}", id);
                         return new BuisnessException("Cliente no encontrado  con ID" + id);
                     });

             //validar que el email no este usado por otro cliente
             clienteRepository.findByCorreoElectronico(request.getCorreoElectronico())
                     .ifPresent(cliente -> {
                         if (!cliente.getId().equals(id)) {
                             log.error("correo electronico ya usado por otro cliente: {}", request.getCorreoElectronico());
                             throw new BuisnessException("El correo electronico ya esta registrado por otro cliente");

                         }
                     });

             //Actualizar campos (mantenimento el ID)
             ClienteExistente.setTipoIdentifiacion(request.getTipoIdentifiacion());
             ClienteExistente.setNumeroIdentificacion(request.getNumeroIdentificacion());
             ClienteExistente.setNombres(request.getNombres());
             ClienteExistente.setApellido(request.getApellido());
             ClienteExistente.setCorreoElectronico(request.getCorreoElectronico());
             ClienteExistente.setFechaNacimiento(request.getFechaNacimiento());

             //La fecha  de modificacion se  actualiza automaticamente con @PreUpdate

             Cliente clienteActualizado = clienteRepository.save(ClienteExistente);
             log.info("cliente actualizado exitoxamente con ID : {}", id);
           return  convertirAResponse(clienteActualizado);

         }

         /*elemina un cliente del sistema
         *
         * validaciones aplicadas
         * el clinete debe existir
         * el cliente no debe tener cuentas financieras vinculadas
         *
         * @param id identificador del cliente
         *@throws BusinnesException si el cliente no existe o tiene  productos  vinculados
          */
        @Transactional
    public void eliminar(Long id){
            log.info("Eliminado cliente con ID: {}", id);
            //validar que el cliente existe

            Cliente cliente = clienteRepository.findById(id)
                    .orElseThrow(()-> {
                        log.error("Cliente no encontrado con ID: {}" , id);
                        return new BuisnessException("cliente no encontrado con ID :" + id);
                    });

            //validar que no tiene cuentas vinculadas

            if(cliente.tieneCuentasVinculadas()){
                log.error("Intento eliminar cliente con cuentas vinculadas. ID: {}", id);
                throw new BuisnessException("no se puede eliminar el cliente porque tiene  productos financieros");

            }
            clienteRepository.deleteById(id);
            log.info("Cliente eliminado exitosamente con ID : {}" , id);
        }
        /*Busca un cliente por su identificador
        *
        * @param id identificador del cliente
        * @return Cliente response con la informacion del cliente
        * throws Businessexceptoin si cliente no existe
        * */

    @Transactional
    public ClienteResponse buscarporId(Long id){
        log.info("Buscando cliente ID:{}", id);

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(()->{
                    log.error("Cliente no encotrado con ID: {}", id);
                    return new BuisnessException("Cliente no encontrado con ID : " + id);

                });
        return convertirAResponse(cliente);
        }

        /*
        * Lista todos los clientes resgistrados  en el sistema
        * @ return lista de clientes con todos los clientes
        * /
         */

    @Transactional(readOnly = true)
    public List<ClienteResponse> listarTodos(){
            log.info("Listado todos los clientes");

            return clienteRepository.findAll().stream()
                    .map(this::convertirAResponse)
                    .collect(Collectors.toList());
    }
    /*
    * convierte una  entidad Cliente a ClienteResponse
    * incluye calculo de edad y cantidad de cuentas
    *
    * @param cliente entidad a convertir
    * @return clienteresponsecon datos mapeados
    *

     */
    private ClienteResponse  convertirAResponse(Cliente cliente){
        ClienteResponse response = modelMapper.map(cliente, ClienteResponse.class);
        response.setEdad(cliente.calcularEdad());
        response.setCantidadCuentas(cliente.getCuentas() != null ? cliente.getCuentas().size() : 0 );
        return response;

    }
}

