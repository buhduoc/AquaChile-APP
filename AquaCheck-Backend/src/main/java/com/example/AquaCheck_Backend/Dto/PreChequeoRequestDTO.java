package com.example.AquaCheck_Backend.Dto;

import com.example.AquaCheck_Backend.Model.ItemCheckList;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PreChequeoRequestDTO {

    private String uuid;

    public String getUuid() { return uuid; }

    public void setUuid(String uuid) { this.uuid = uuid; }


    @NotBlank(message = "El centro es obligatorio")
    private String centro;

    @NotBlank(message = "La fecha es obligatoria")
    private String fecha;

    @NotBlank(message = "El buzo es obligatorio")
    private String buzo;

    @NotBlank(message = "El supervisor es obligatorio")
    private String supervisor;

    @Valid
    @NotEmpty(message = "Debe existir al menos un item en el checklist")
    private List<ItemCheckList> items;

    private List<String> fotos;

    @NotNull(message = "La presion sistolica es obligatoria")
    @Min(value = 70)
    @Max(value = 250)
    private Integer sistolica;

    @NotNull(message = "La presion diastolica es obligatoria")
    @Min(value = 40)
    @Max(value = 150)
    private Integer diastolica;

    @NotNull(message = "La saturacion es obligatoria")
    @Min(value = 50)
    @Max(value = 100)
    private Integer saturacion;

    private String observaciones;

    private List<String> alertas;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 18)
    private Integer edad;

    @NotNull(message = "El peso es obligatorio")
    @Min(value = 30)
    private Double pesoKg;

    @NotNull(message = "La altura es obligatoria")
    @Min(value = 100)
    private Double alturaCm;

    @NotNull(message = "Debe indicar si el examen preocupacional está vigente")
    private Boolean examenPreocupacionalVigente;

    @NotNull(message = "Debe indicar si el test de esfuerzo está vigente")
    private Boolean testEsfuerzoVigente;

    @NotNull(message = "El puntaje de Calbuco es obligatorio")
    @Min(0)
    @Max(100)
    private Integer puntajeEscuelaCalbuco;


    public PreChequeoRequestDTO() {
    }

    public PreChequeoRequestDTO(String centro, String fecha, String supervisor, String buzo, List<ItemCheckList> items, List<String> fotos, Integer sistolica, Integer diastolica, Integer saturacion, String observaciones, List<String> alertas, Integer edad, Double pesoKg, Double alturaCm, Boolean examenPreocupacionalVigente, Boolean testEsfuerzoVigente, Integer puntajeEscuelaCalbuco) {
        this.centro = centro;
        this.fecha = fecha;
        this.supervisor = supervisor;
        this.buzo = buzo;
        this.items = items;
        this.fotos = fotos;
        this.sistolica = sistolica;
        this.diastolica = diastolica;
        this.saturacion = saturacion;
        this.observaciones = observaciones;
        this.alertas = alertas;
        this.edad = edad;
        this.pesoKg = pesoKg;
        this.alturaCm = alturaCm;
        this.examenPreocupacionalVigente = examenPreocupacionalVigente;
        this.testEsfuerzoVigente = testEsfuerzoVigente;
        this.puntajeEscuelaCalbuco = puntajeEscuelaCalbuco;
    }


    public String getCentro() {
        return centro;
    }

    public void setCentro(String centro) {
        this.centro = centro;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getBuzo() {
        return buzo;
    }

    public void setBuzo(String buzo) {
        this.buzo = buzo;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public List<ItemCheckList> getItems() {
        return items;
    }

    public void setItems(List<ItemCheckList> items) {
        this.items = items;
    }

    public List<String> getFotos() {
        return fotos;
    }

    public void setFotos(List<String> fotos) {
        this.fotos = fotos;
    }

    public Integer getSistolica() {
        return sistolica;
    }

    public void setSistolica(Integer sistolica) {
        this.sistolica = sistolica;
    }

    public Integer getDiastolica() {
        return diastolica;
    }

    public void setDiastolica(Integer diastolica) {
        this.diastolica = diastolica;
    }

    public Integer getSaturacion() {
        return saturacion;
    }

    public void setSaturacion(Integer saturacion) {
        this.saturacion = saturacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<String> getAlertas() {
        return alertas;
    }

    public void setAlertas(List<String> alertas) {
        this.alertas = alertas;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public Double getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(Double pesoKg) {
        this.pesoKg = pesoKg;
    }

    public Double getAlturaCm() {
        return alturaCm;
    }

    public void setAlturaCm(Double alturaCm) {
        this.alturaCm = alturaCm;
    }

    public Boolean getExamenPreocupacionalVigente() {
        return examenPreocupacionalVigente;
    }

    public void setExamenPreocupacionalVigente(Boolean examenPreocupacionalVigente) {
        this.examenPreocupacionalVigente = examenPreocupacionalVigente;
    }

    public Boolean getTestEsfuerzoVigente() {
        return testEsfuerzoVigente;
    }

    public void setTestEsfuerzoVigente(Boolean testEsfuerzoVigente) {
        this.testEsfuerzoVigente = testEsfuerzoVigente;
    }

    public Integer getPuntajeEscuelaCalbuco() {
        return puntajeEscuelaCalbuco;
    }

    public void setPuntajeEscuelaCalbuco(Integer puntajeEscuelaCalbuco) {
        this.puntajeEscuelaCalbuco = puntajeEscuelaCalbuco;
    }
}
