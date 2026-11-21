package com.example.AquaCheck_Backend.Repository;

import com.example.AquaCheck_Backend.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    //JpaRepository para traer automaticamente
    //findAll()
    //findById()
    //deleteById()
    //existsById()

    boolean existsByUsuarioIgnoreCase(String usuario);

    Optional<Usuario> findByUsuarioIgnoreCase(String usuario);

}
