package com.example.demospringbatch.processor;

import com.example.demospringbatch.model.Persona;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;


import java.util.Locale;

public class PersonaItemProcessor implements ItemProcessor<Persona, Persona> {

    private static final Logger LOGGER= LoggerFactory.getLogger(PersonaItemProcessor.class);


    @Override
    public Persona process(Persona persona) throws Exception {
        String primerNombre = persona.getPrimerNombre().toUpperCase(Locale.ROOT);
        String segundoNombre = persona.getSegundoNombre().toUpperCase(Locale.ROOT);
        String telefono = persona.getTelefono().toUpperCase(Locale.ROOT);

        Persona nuevaPersona = new Persona(primerNombre,segundoNombre,telefono);
        LOGGER.info("Convirtiendo " + persona + " a " + nuevaPersona);

        return nuevaPersona;

    }
}
