package com.crudpractica.finanzastestproyec.Infrastructure;




/*
* configuracion del Beans de la aplicacion
*
* Define los beans que sera  gestionados por el contenedor de spring
* incluyendo  el ModelMapper para  mapeo  entre DTOS y entidades
*
* */

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

public class Config {

    /*
     * Bean de ModelMapper para conversión automática entre DTOs y entidades.
     *
     * ModelMapper permite mapear automáticamente propiedades con el mismo nombre
     * entre objetos de diferentes clases, reduciendo código boilerplate.
     *
     * @return instancia configurada de ModelMapper
     */

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setSkipNullEnabled(true)
                .setAmbiguityIgnored(true);

        return modelMapper;
    }
}
