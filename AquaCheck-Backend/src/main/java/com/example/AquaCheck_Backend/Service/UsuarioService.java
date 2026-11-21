package com.example.AquaCheck_Backend.Service;


import com.example.AquaCheck_Backend.Model.Usuario;
import com.example.AquaCheck_Backend.Repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    //Listar
    public List<Usuario> listar() {
        return repository.findAll();
    }

    //Buscar por ID

    public Usuario buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(()->
                        new RuntimeException(
                                "Usuario no encontrado"
                        )
                );
    }


    // Crear
    public Usuario registrar(Usuario usuario) {

        // El ID lo genera MySQL
        usuario.setId(null);

        String usuarioLimpio = limpiarUsuario(usuario.getUsuario());

        if (repository.existsByUsuarioIgnoreCase(usuarioLimpio)){
            throw new IllegalArgumentException(
                    "Ese usuario ya existe"
            );
        }

        if (usuario.getClave().length() < 6) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos 6 caracteres"
            );
        }

        usuario.setUsuario(usuarioLimpio);

        // Una cuenta creada desde registro será Buzo
        usuario.setRol("Buzo");

        // Debe ser aprobada posteriormente,queda pendiente
        usuario.setAprobada(false);

        return repository.save(usuario);
    }


    // Actualizar

    public Usuario actualizar(
            Long id, Usuario datos
    ) {

        Usuario usuario = buscarPorId(id);


        usuario.setNombre(datos.getNombre());
        usuario.setRut(datos.getRut());
        usuario.setTelefono(datos.getTelefono());
        usuario.setCorreo(datos.getCorreo());


        if (datos.getClave() != null &&
                !datos.getClave().isBlank()) {

            if (datos.getClave().length() < 6) {

                throw new IllegalArgumentException(
                        "La contraseña debe tener al menos 6 caracteres"
                );
            }

            usuario.setClave(datos.getClave());
        }


        return repository.save(usuario);
    }

    // Eliminar

    public void eliminar(Long id) {
        Usuario usuario = buscarPorId(id);
        repository.delete(usuario);
    }

    //Aprobar cuenta

    public Usuario aprobar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setAprobada(true);
        return repository.save(usuario);
    }


    //Limpiar usuario

    private String limpiarUsuario(String usuario) {
        String texto = usuario.trim();

        if(texto.contains("@")) {
            return texto;
        }

        return texto.replace(".","");
    }
}
