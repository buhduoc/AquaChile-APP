package com.example.AquaCheck_Backend.Dto;


public class BitacoraRequestDTO {


    private Long preChequeoId;

    private String centro;

    private String buzo;

    private String fechaHora;

    private Integer tiempoFondoMin;

    private Double profundidadMaxM;

    private Double velocidadDescensoMMin;

    private Double velocidadAscensoMMin;

    private String observaciones;

    private String estadoRevision = "PENDIENTE";

    private String responsableRevision;

    private String fechaRevision;

    public BitacoraRequestDTO() {
    }

    public BitacoraRequestDTO(Long preChequeoId, String centro, String buzo, String fechaHora, Integer tiempoFondoMin, Double profundidadMaxM, Double velocidadDescensoMMin, Double velocidadAscensoMMin, String observaciones, String estadoRevision, String responsableRevision, String fechaRevision) {
        this.preChequeoId = preChequeoId;
        this.centro = centro;
        this.buzo = buzo;
        this.fechaHora = fechaHora;
        this.tiempoFondoMin = tiempoFondoMin;
        this.profundidadMaxM = profundidadMaxM;
        this.velocidadDescensoMMin = velocidadDescensoMMin;
        this.velocidadAscensoMMin = velocidadAscensoMMin;
        this.observaciones = observaciones;
        this.estadoRevision = estadoRevision;
        this.responsableRevision = responsableRevision;
        this.fechaRevision = fechaRevision;
    }

    public Long getPreChequeoId() {
        return preChequeoId;
    }

    public void setPreChequeoId(Long preChequeoId) {
        this.preChequeoId = preChequeoId;
    }

    public String getCentro() {
        return centro;
    }

    public void setCentro(String centro) {
        this.centro = centro;
    }

    public String getBuzo() {
        return buzo;
    }

    public void setBuzo(String buzo) {
        this.buzo = buzo;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Integer getTiempoFondoMin() {
        return tiempoFondoMin;
    }

    public void setTiempoFondoMin(Integer tiempoFondoMin) {
        this.tiempoFondoMin = tiempoFondoMin;
    }

    public Double getProfundidadMaxM() {
        return profundidadMaxM;
    }

    public void setProfundidadMaxM(Double profundidadMaxM) {
        this.profundidadMaxM = profundidadMaxM;
    }

    public Double getVelocidadDescensoMMin() {
        return velocidadDescensoMMin;
    }

    public void setVelocidadDescensoMMin(Double velocidadDescensoMMin) {
        this.velocidadDescensoMMin = velocidadDescensoMMin;
    }

    public Double getVelocidadAscensoMMin() {
        return velocidadAscensoMMin;
    }

    public void setVelocidadAscensoMMin(Double velocidadAscensoMMin) {
        this.velocidadAscensoMMin = velocidadAscensoMMin;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getEstadoRevision() {
        return estadoRevision;
    }

    public void setEstadoRevision(String estadoRevision) {
        this.estadoRevision = estadoRevision;
    }

    public String getResponsableRevision() {
        return responsableRevision;
    }

    public void setResponsableRevision(String responsableRevision) {
        this.responsableRevision = responsableRevision;
    }

    public String getFechaRevision() {
        return fechaRevision;
    }

    public void setFechaRevision(String fechaRevision) {
        this.fechaRevision = fechaRevision;
    }


}