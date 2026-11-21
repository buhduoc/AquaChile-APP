package com.example.AquaCheck_Backend.Repository;

import com.example.AquaCheck_Backend.Model.EvidenciaEquipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvidenciaEquipoRepository extends JpaRepository<EvidenciaEquipo, Long> {

    List<EvidenciaEquipo> findByIdFaena(String idFaena);

}
