package com.example.AquaCheck_Backend.Controller;

import com.example.AquaCheck_Backend.Dto.BitacoraRequestDTO;
import com.example.AquaCheck_Backend.Model.BitacoraPostInmersion;
import com.example.AquaCheck_Backend.Service.BitacoraPostInmersionService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



import java.util.List;

@RestController
@RequestMapping("/api/bitacoras")
public class BitacoraPostInmersionController {

    private final BitacoraPostInmersionService service;

    public BitacoraPostInmersionController(BitacoraPostInmersionService service) {
        this.service = service;
    }

    //GET todas las bitacoras
    @GetMapping()
    public List<BitacoraPostInmersion> listarTodo() {
        return service.listar();
    }

    //GET buscar bitacora por ID
    @GetMapping("/{id}")
    public BitacoraPostInmersion listarPorId(@PathVariable Long id) {
        return service.buscarBitacoraPorId(id);
    }

    //POST crear nueva bitacora
    @PostMapping()
    public ResponseEntity<BitacoraPostInmersion> crearBitacora(@Valid @RequestBody BitacoraRequestDTO dto) {

        BitacoraPostInmersion bitacora = convertirDto(dto);

        BitacoraPostInmersion creada = service.crearBitacora(bitacora);

        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    //PUT actualizar bitacora
    @PutMapping("/{id}")
    public ResponseEntity<BitacoraPostInmersion> actualizarBitacora(@PathVariable Long id, @Valid @RequestBody BitacoraRequestDTO dto) {

        BitacoraPostInmersion bitacora = convertirDto(dto);

        BitacoraPostInmersion  actualizar = service.actualizarBitacora(id, bitacora);
        return ResponseEntity.ok(actualizar);
    }

    //DELETE eliminar Bitacora
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarBitacora(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    //PATCH marca como revisada
    @PatchMapping("/{id}/revisar")
    public ResponseEntity<BitacoraPostInmersion> revisar(@PathVariable Long id, @RequestParam String responsable) {

        BitacoraPostInmersion  revisar = service.revisar(id, responsable);
        return ResponseEntity.ok(revisar);
    }


    //
    private BitacoraPostInmersion convertirDto(
            BitacoraRequestDTO dto
    ) {

        BitacoraPostInmersion bitacora =
                new BitacoraPostInmersion();

        bitacora.setPreChequeoId(
                dto.getPreChequeoId()
        );

        bitacora.setCentro(
                dto.getCentro()
        );

        bitacora.setBuzo(
                dto.getBuzo()
        );

        bitacora.setFechaHora(
                dto.getFechaHora()
        );

        bitacora.setTiempoFondoMin(
                dto.getTiempoFondoMin()
        );

        bitacora.setProfundidadMaxM(
                dto.getProfundidadMaxM()
        );

        bitacora.setVelocidadDescensoMMin(
                dto.getVelocidadDescensoMMin()
        );

        bitacora.setVelocidadAscensoMMin(
                dto.getVelocidadAscensoMMin()
        );

        bitacora.setObservaciones(
                dto.getObservaciones()
        );

        return bitacora;
    }

}
