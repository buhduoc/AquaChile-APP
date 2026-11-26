package com.example.AquaCheck_Backend.Controller;


import com.example.AquaCheck_Backend.Dto.PreChequeoRequestDTO;
import com.example.AquaCheck_Backend.Model.PreChequeo;
import com.example.AquaCheck_Backend.Service.PreChequeoService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/prechequeos")
public class PreChequeoController {

    private final PreChequeoService service;

    public PreChequeoController(PreChequeoService service) {
        this.service = service;
    }

    //GET todos los preChequeos
    @GetMapping()
    public List<PreChequeo> listarTodo(){
        return service.listar();

    }

    //GET Buscar por ID
    @GetMapping("/{id}")
    public PreChequeo buscar(@PathVariable Long id){
        return service.buscarPorId(id);
    }

    //POST Registrar preChequeo

    @PostMapping
    public ResponseEntity<PreChequeo> registrar(@Valid @RequestBody PreChequeoRequestDTO dto){

        PreChequeo preChequeo = convertirDto(dto);
        PreChequeo nuevo = service.crearPreChequeo(preChequeo);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevo);
    }

    //PUT actualizar preChequeo
    @PutMapping("/{id}")
    public ResponseEntity<PreChequeo> actualizar(@PathVariable Long id,@Valid @RequestBody PreChequeoRequestDTO dto){

        PreChequeo preChequeo = convertirDto(dto);
        PreChequeo actualizado = service.actualizar(id,preChequeo);
        return ResponseEntity
                .ok(actualizado);
    }

    //DELETE eliminar preChequeo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        service.eliminar(id);
        return  ResponseEntity.noContent().build();
    }

    // CONVERTIR DTO → ENTIDAD
    private PreChequeo convertirDto(
            PreChequeoRequestDTO dto
    ) {

        PreChequeo preChequeo =
                new PreChequeo();

        preChequeo.setUuid(dto.getUuid());

        preChequeo.setCentro(
                dto.getCentro()
        );

        preChequeo.setFecha(
                dto.getFecha()
        );

        preChequeo.setBuzo(
                dto.getBuzo()
        );

        preChequeo.setSupervisor(
                dto.getSupervisor()
        );

        preChequeo.setItems(
                dto.getItems()
        );

        preChequeo.setFotos(
                dto.getFotos()
        );

        preChequeo.setSistolica(
                dto.getSistolica()
        );

        preChequeo.setDiastolica(
                dto.getDiastolica()
        );

        preChequeo.setSaturacion(
                dto.getSaturacion()
        );

        preChequeo.setObservaciones(
                dto.getObservaciones()
        );

        preChequeo.setAlertas(
                dto.getAlertas()
        );

        preChequeo.setEdad(
                dto.getEdad()
        );

        preChequeo.setPesoKg(
                dto.getPesoKg()
        );

        preChequeo.setAlturaCm(
                dto.getAlturaCm()
        );

        preChequeo.setExamenPreocupacionalVigente(
                dto.getExamenPreocupacionalVigente()
        );

        preChequeo.setTestEsfuerzoVigente(
                dto.getTestEsfuerzoVigente()
        );

        preChequeo.setPuntajeEscuelaCalbuco(
                dto.getPuntajeEscuelaCalbuco()
        );

        return preChequeo;
    }








}
