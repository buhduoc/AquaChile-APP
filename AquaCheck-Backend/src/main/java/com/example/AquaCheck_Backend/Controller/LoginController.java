package com.example.AquaCheck_Backend.Controller;
import com.example.AquaCheck_Backend.Repository.UsuarioRepository;
import com.example.AquaCheck_Backend.Model.Usuario;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
@RestController
@RequestMapping("/api/auth")
public class LoginController {
 private final UsuarioRepository repository;
 private final com.example.AquaCheck_Backend.Config.ApiSecurity security;
 private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
 public LoginController(UsuarioRepository repository, com.example.AquaCheck_Backend.Config.ApiSecurity security) { this.repository = repository; this.security = security; }
 public record Credenciales(String usuario, String clave) {}
 public record Perfil(Long id, String usuario, String nombre, String rol, String rut, String telefono, String correo, Boolean aprobada, String token) {}
 @PostMapping("/login")
 public Perfil login(@RequestBody Credenciales datos) {
  if (datos.usuario() == null || datos.clave() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
  Usuario u = repository.findByUsuarioIgnoreCase(datos.usuario().trim()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
  if (!encoder.matches(datos.clave(), u.getClave())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
  if (!Boolean.TRUE.equals(u.getAprobada())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
  return new Perfil(u.getId(),u.getUsuario(),u.getNombre(),u.getRol(),u.getRut(),u.getTelefono(),u.getCorreo(),u.getAprobada(),security.token(u.getUsuario()));
 }
}
