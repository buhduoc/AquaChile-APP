package com.example.AquaCheck_Backend.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Entity
@Table(name="evidencia_equipo")
public class EvidenciaEquipo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El ID del equipo es obligatorio")
    private String idEquipo;

    @NotBlank(message = "El nombre del equipo es Obligatoria")
    private String nombreEquipo;

    @NotNull(message = "Debe indicar si la evidencia es Obligatoria")
    private Boolean obligatoria = true;


    private String uriFotoActual;

    private String fotoReferencia;

    private String fechaHora;

    private String idFaena;

    private String responsable;


    public EvidenciaEquipo() {
    }

    public EvidenciaEquipo(Long id, String idEquipo, String nombreEquipo, String uriFotoActual, Boolean obligatoria, String fotoReferencia, String fechaHora, String idFaena, String responsable) {
        this.id = id;
        this.idEquipo = idEquipo;
        this.nombreEquipo = nombreEquipo;
        this.uriFotoActual = uriFotoActual;
        this.obligatoria = obligatoria;
        this.fotoReferencia = fotoReferencia;
        this.fechaHora = fechaHora;
        this.idFaena = idFaena;
        this.responsable = responsable;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(String idEquipo) {
        this.idEquipo = idEquipo;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }

    public Boolean getObligatoria() {
        return obligatoria;
    }

    public void setObligatoria(Boolean obligatoria) {
        this.obligatoria = obligatoria;
    }

    public String getUriFotoActual() {
        return uriFotoActual;
    }

    public void setUriFotoActual(String uriFotoActual) {
        this.uriFotoActual = uriFotoActual;
    }

    public String getFotoReferencia() {
        return fotoReferencia;
    }

    public void setFotoReferencia(String fotoReferencia) {
        this.fotoReferencia = fotoReferencia;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getIdFaena() {
        return idFaena;
    }

    public void setIdFaena(String idFaena) {
        this.idFaena = idFaena;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }
}
