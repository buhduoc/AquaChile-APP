package com.example.AquaCheck_Backend.Service;


import com.example.AquaCheck_Backend.Model.BitacoraPostInmersion;
import com.example.AquaCheck_Backend.Repository.BitacoraPostInmersionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BitacoraPostInmersionService {

    private final BitacoraPostInmersionRepository repository;

    public  BitacoraPostInmersionService(BitacoraPostInmersionRepository  repository) {
        this.repository = repository;
    }

    //Listar todo
    public List<BitacoraPostInmersion> listar() {
        return repository.findAll();
    }

    //Buscar por ID
    public BitacoraPostInmersion buscarBitacoraPorId(Long id) {
        return repository.findById(id).orElseThrow(()->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Bitacora no encontrado"
                )
        );
    }


    //Crear Bitacora
    public BitacoraPostInmersion crearBitacora(BitacoraPostInmersion bitacora) {

        //El ID lo genera MySQL
        bitacora.setId(null);

        //Validar datos
        validarBitacora(bitacora);


        //Toda Bitacora nueva comienza con PENDIENTE
        bitacora.setEstadoRevision("PENDIENTE");

        //Todavia nadie ha revisado
        bitacora.setResponsableRevision(null);
        bitacora.setFechaRevision(null);


        //Mostrar alertas si existen
        if(bitacora.getTieneAlerta()){
            System.out.println("===== ALERTA POST-INMERSION =====");
            bitacora.getAlertasJefeCentro().forEach(System.out::println);
        }
        return repository.save(bitacora);
    }

    //Actualizar bitacora
    public BitacoraPostInmersion actualizarBitacora(Long id,BitacoraPostInmersion datos ) {

        BitacoraPostInmersion bitacora = buscarBitacoraPorId(id);

        bitacora.setPreChequeoId(datos.getPreChequeoId());
        bitacora.setCentro(datos.getCentro());
        bitacora.setBuzo(datos.getBuzo());
        bitacora.setFechaHora(datos.getFechaHora());
        bitacora.setTiempoFondoMin(datos.getTiempoFondoMin());
        bitacora.setProfundidadMaxM(datos.getProfundidadMaxM());
        bitacora.setVelocidadDescensoMMin(datos.getVelocidadDescensoMMin());
        bitacora.setVelocidadAscensoMMin(datos.getVelocidadAscensoMMin());
        bitacora.setObservaciones(datos.getObservaciones());

        /*
         * No modificamos aquí:
         * estadoRevision
         * responsableRevision
         * fechaRevision
         *
         * porque esos datos se controlan
         * mediante el método revisar().
         */

        validarBitacora(bitacora);

        if(bitacora.getTieneAlerta()){
            System.out.println("===== ALERTA POST-INMERSION =====");
            datos.getAlertasJefeCentro().forEach(System.out::println);
        }


        return  repository.save(bitacora);
    }

    //Eliminar Bitacora

    public void eliminar(Long id) {
        BitacoraPostInmersion bitacora = buscarBitacoraPorId(id);

        // Si la bitácora tiene una alerta,
        // no se permite eliminar para conservar
        // el historial y la trazabilidad.
        if (bitacora.getTieneAlerta()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede eliminar una bitácora que contiene alertas históricas"
            );
        }

        repository.delete(bitacora);
    }

    // PATCH - MARCAR COMO REVISADA

    public BitacoraPostInmersion revisar(
            Long id,
            String responsable
    ) {

        BitacoraPostInmersion bitacora =
                buscarBitacoraPorId(id);


        // Validar responsable
        if (responsable == null ||
                responsable.isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe indicar quién revisó la bitácora"
            );
        }


        bitacora.setEstadoRevision(
                "REVISADA"
        );

        bitacora.setResponsableRevision(
                responsable.trim()
        );

        // Fecha automática del servidor
        bitacora.setFechaRevision(
                LocalDateTime.now().toString()
        );


        return repository.save(bitacora);
    }



    //VALIDACIONES
    private void validarBitacora(
            BitacoraPostInmersion bitacora
    ) {

        if (bitacora.getTiempoFondoMin() != null
                && bitacora.getTiempoFondoMin() < 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El tiempo de fondo no puede ser negativo"
            );
        }


        if (bitacora.getProfundidadMaxM() != null
                && bitacora.getProfundidadMaxM() < 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La profundidad no puede ser negativa"
            );
        }


        if (bitacora.getVelocidadDescensoMMin() != null
                && bitacora.getVelocidadDescensoMMin() < 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La velocidad de descenso no puede ser negativa"
            );
        }


        if (bitacora.getVelocidadAscensoMMin() != null
                && bitacora.getVelocidadAscensoMMin() < 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La velocidad de ascenso no puede ser negativa"
            );
        }
    }


}
