package com.example.AquaCheck_Backend.Controller;

import com.example.AquaCheck_Backend.Model.Usuario;
import com.example.AquaCheck_Backend.Service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService service;


    public UsuarioController(UsuarioService service) {

        this.service = service;
    }


    // Get Todos los Usuarios

    @GetMapping
    public List<Usuario> listar() {

        return service.listar();
    }



    // Buscar por ID


    @GetMapping("/{id}")
    public Usuario buscar(
            @PathVariable Long id
    ) {

        return service.buscarPorId(id);
    }



    // Post registrar Usuario

    @PostMapping
    public ResponseEntity<Usuario> registrar(
            @RequestBody Usuario usuario
    ) {
        Usuario nuevo = service.registrar(usuario);

        return ResponseEntity.ok(nuevo);
    }

    // Actualizar Usuario


    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(
            @PathVariable Long id,
            @Valid
            @RequestBody Usuario usuario
    ) {

        Usuario actualizado =
                service.actualizar(id, usuario);

        return ResponseEntity.ok(actualizado);
    }



    // Eliminar usuario

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id
    ) {

        service.eliminar(id);

        return ResponseEntity.noContent().build();
    }



    // Aprobar cuenta


    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<Usuario> aprobar(
            @PathVariable Long id
    ) {

        Usuario aprobado =
                service.aprobar(id);

        return ResponseEntity.ok(aprobado);
    }
}