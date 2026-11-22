package com.example.AquaCheck_Backend.Controller;

import com.example.AquaCheck_Backend.Dto.EvidenciaEquipoRequestDTO;
import com.example.AquaCheck_Backend.Model.EvidenciaEquipo;
import com.example.AquaCheck_Backend.Service.EvidenciaEquipoService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evidencias")
public class EvidenciaEquipoController {

    private final EvidenciaEquipoService service;

    public EvidenciaEquipoController(EvidenciaEquipoService service) {
        this.service = service;
    }


    //GET Listar todas las Evidencias
    @GetMapping
    public List<EvidenciaEquipo> listar(){
        return service.listar();
    }

    //GET Buscar por ID
    @GetMapping("/{id}")
    public EvidenciaEquipo buscarPorId(@PathVariable Long id){
        return service.buscarPorId(id);
    }

    //GET buscar por Faena
    @GetMapping("/faena/{idFaena}")
    public List<EvidenciaEquipo> buscarPorFaena(@PathVariable String idFaena){
        return service.buscarPorFaena(idFaena);
    }

    //POST registrar Evidencia de equipo
    @PostMapping
    public ResponseEntity<EvidenciaEquipo> registrar(@Valid @RequestBody EvidenciaEquipoRequestDTO dto){

        EvidenciaEquipo evidencia = convertirDto(dto);

        EvidenciaEquipo nueva = service.crearEvidenciaEquipo(evidencia);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nueva);
    }

    //PUT actualizar evidencias
    @PutMapping("/{id}")
    public ResponseEntity<EvidenciaEquipo> actualizar(@PathVariable Long id, @Valid @RequestBody EvidenciaEquipoRequestDTO dto){

        EvidenciaEquipo evidencia = convertirDto(dto);

        EvidenciaEquipo actualizada = service.actualizarEvidencia(id,evidencia);

        return  ResponseEntity
                .ok(actualizada);

    }


    //DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        service.eliminar(id);

        return ResponseEntity.noContent().build();
    }

    // CONVERTIR DTO → ENTIDAD
    private EvidenciaEquipo convertirDto(
            EvidenciaEquipoRequestDTO dto
    ) {

        EvidenciaEquipo evidencia =
                new EvidenciaEquipo();

        evidencia.setIdEquipo(
                dto.getIdEquipo()
        );

        evidencia.setNombreEquipo(
                dto.getNombreEquipo()
        );

        evidencia.setObligatoria(
                dto.getObligatoria()
        );

        evidencia.setUriFotoActual(
                dto.getUriFotoActual()
        );

        evidencia.setFotoReferencia(
                dto.getFotoReferencia()
        );

        evidencia.setFechaHora(
                dto.getFechaHora()
        );

        evidencia.setIdFaena(
                dto.getIdFaena()
        );

        evidencia.setResponsable(
                dto.getResponsable()
        );

        return evidencia;
    }
}
