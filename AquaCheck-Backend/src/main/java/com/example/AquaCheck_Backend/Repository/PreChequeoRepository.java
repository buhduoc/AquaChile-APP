package com.example.AquaCheck_Backend.Repository;

import com.example.AquaCheck_Backend.Model.PreChequeo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreChequeoRepository extends JpaRepository<PreChequeo, Long> {
    java.util.Optional<PreChequeo> findByUuid(String uuid);


}
