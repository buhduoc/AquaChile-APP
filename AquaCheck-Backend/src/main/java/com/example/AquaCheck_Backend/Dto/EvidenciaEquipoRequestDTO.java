package com.example.AquaCheck_Backend.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)//Si android manda algo adicional ejemplo "id":5
                                        //El backend simplemente lo ignora aqui en vez de producir un error
public class EvidenciaEquipoRequestDTO {

    @NotBlank(message = "El ID del equipo es obligatorio")
    private String idEquipo;

    @NotBlank(message = "El nombre del equipo es obligatorio")
    private String nombreEquipo;

    @NotNull(message = "Debe indicar si la evidencia es obligatoria")
    private Boolean obligatoria = true;

    private String uriFotoActual;

    private String fotoReferencia;

    private String fechaHora;

    private String idFaena;

    private String responsable;


    // Constructor vacío
    public EvidenciaEquipoRequestDTO() {
    }


    // Constructor completo
    public EvidenciaEquipoRequestDTO(
            String idEquipo,
            String nombreEquipo,
            Boolean obligatoria,
            String uriFotoActual,
            String fotoReferencia,
            String fechaHora,
            String idFaena,
            String responsable
    ) {
        this.idEquipo = idEquipo;
        this.nombreEquipo = nombreEquipo;
        this.obligatoria = obligatoria;
        this.uriFotoActual = uriFotoActual;
        this.fotoReferencia = fotoReferencia;
        this.fechaHora = fechaHora;
        this.idFaena = idFaena;
        this.responsable = responsable;
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