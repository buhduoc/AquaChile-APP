package com.example.AquaCheck_Backend.Model;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name="bitacora_post_inmersion")
public class BitacoraPostInmersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    public BitacoraPostInmersion() {
    }


    //Propiedades calculadas

    //@Transient -->
    @Transient
    public boolean getExcesoDescenso(){
        return velocidadDescensoMMin !=null && velocidadDescensoMMin >24.0;
    }

    @Transient
    public boolean getExcesoAscenso(){
        return velocidadAscensoMMin  !=null && velocidadAscensoMMin >9.0;
    }

    @Transient
    public boolean getTieneAlerta(){
        return getExcesoDescenso() || getExcesoAscenso();
    }

    @Transient
    public List<String> getAlertasJefeCentro(){
        List<String> lista = new ArrayList<>();

        if(getExcesoDescenso()){
            lista.add(
                    "ALERTA JEFE DE CENTRO: " +
                    "Velocidad de descenso excede el limite (" +velocidadDescensoMMin +" m/min, max. 24 m/min)"

            );
        }

        if(getExcesoAscenso()){
            lista.add(
                    "ALERTA CRITICA JEFE DE CENTRO: " +
                    "Velocidad de ascenso excede el limite seguro (" + velocidadAscensoMMin +" m/min, max 9 m/min)"
            );
        }

        return lista;
    }

    public BitacoraPostInmersion(Long id, Long preChequeoId, String centro, String buzo, String fechaHora, Integer tiempoFondoMin, Double profundidadMaxM, Double velocidadDescensoMMin, Double velocidadAscensoMMin, String observaciones, String estadoRevision, String responsableRevision, String fechaRevision) {
        this.id = id;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
