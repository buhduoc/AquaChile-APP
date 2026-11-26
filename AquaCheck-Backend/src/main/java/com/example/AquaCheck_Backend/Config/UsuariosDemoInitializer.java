package com.example.AquaCheck_Backend.Config;

import com.example.AquaCheck_Backend.Model.Usuario;
import com.example.AquaCheck_Backend.Repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.core.env.Environment;

@Component
@Profile("demo-local")
public class UsuariosDemoInitializer implements CommandLineRunner {
    private final UsuarioRepository repository;
    private final Environment env;

    public UsuariosDemoInitializer(UsuarioRepository repository, Environment env) {
        this.repository = repository;
        this.env = env;
    }

    @Override
    @Transactional
    public void run(String... args) {
        crear("buzo@aquachile.cl", "Buzo de prueba", "Buzo", "11111111-1", "AQUA_BUZO1_CLAVE");
        crear("buzo2@aquachile.cl", "Segundo buzo de prueba", "Buzo", "33333333-3", "AQUA_BUZO2_CLAVE");
        crear("admin@aquachile.cl", "Administrador de prueba", "Supervisor", "22222222-2", "AQUA_ADMIN_CLAVE");
    }

    private void crear(String login, String nombre, String rol, String rut, String variable) {
        if (repository.existsByUsuarioIgnoreCase(login)) return;
        String clave = env.getRequiredProperty(variable);
        if (clave.length() < 6) throw new IllegalArgumentException(variable + " requiere al menos 6 caracteres");
        Usuario u = new Usuario();
        u.setUsuario(login);
        u.setClave(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(clave));
        u.setNombre(nombre);
        u.setRol(rol);
        u.setRut(rut);
        u.setTelefono("000000000");
        u.setCorreo(login);
        u.setAprobada(true);
        repository.save(u);
    }
}
