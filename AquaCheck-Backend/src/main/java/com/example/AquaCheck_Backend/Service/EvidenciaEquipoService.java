package com.example.AquaCheck_Backend.Service;

import com.example.AquaCheck_Backend.Model.EvidenciaEquipo;
import com.example.AquaCheck_Backend.Repository.EvidenciaEquipoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EvidenciaEquipoService {

    private final EvidenciaEquipoRepository repository;

    public EvidenciaEquipoService(EvidenciaEquipoRepository repository) {
        this.repository = repository;
    }

    //Listar todas las evidencias de equipos
    public List<EvidenciaEquipo> listar() {
        return repository.findAll();
    }

    //Buscar por ID
    public EvidenciaEquipo buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(()->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Evidencia de equipo no encontrado"
                ));
    }

    //Buscar por Faena
    public List<EvidenciaEquipo> buscarPorFaena(String idFaena) {
        return repository.findByIdFaena(idFaena);
    }

    //Crear Evidencia de equipo
    public EvidenciaEquipo crearEvidenciaEquipo(EvidenciaEquipo evidencia) {

        evidencia.setId(null);
        return repository.save(evidencia);
    }

    //Actualizar Evidencia
    public EvidenciaEquipo actualizarEvidencia(Long id, EvidenciaEquipo datos) {

        EvidenciaEquipo actualizar = buscarPorId(id);

        actualizar.setIdEquipo(datos.getIdEquipo());
        actualizar.setNombreEquipo(datos.getNombreEquipo());
        actualizar.setObligatoria(datos.getObligatoria());
        actualizar.setUriFotoActual(datos.getUriFotoActual());
        actualizar.setFotoReferencia(datos.getFotoReferencia());
        actualizar.setFechaHora(datos.getFechaHora());
        actualizar.setIdFaena(datos.getIdFaena());
        actualizar.setResponsable(datos.getResponsable());

        return repository.save(actualizar);
    }

    //Eliminar Evidencias de equipo

    public void eliminar(Long id) {
        EvidenciaEquipo evidencia = buscarPorId(id);
        repository.delete(evidencia);
    }


}
