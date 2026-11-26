package com.example.AquaCheck_Backend.Config;
import com.example.AquaCheck_Backend.Repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@Component
@Profile("demo-local")
public class MigrarClavesLocales implements CommandLineRunner {
    private final UsuarioRepository repository;
    public MigrarClavesLocales(UsuarioRepository repository) { this.repository = repository; }
    @Override @Transactional
    public void run(String... args) {
        var encoder = new BCryptPasswordEncoder();
        for (var u : repository.findAll()) {
            String clave = u.getClave();
            if (clave != null && !clave.matches("^\\$2[aby]\\$[0-9]{2}\\$[./A-Za-z0-9]{53}$")) {
                u.setClave(encoder.encode(clave));
                repository.save(u);
            }
        }
    }
}
