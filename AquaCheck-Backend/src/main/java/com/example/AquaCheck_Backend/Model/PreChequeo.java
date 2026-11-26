package com.example.AquaCheck_Backend.Model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

@Entity
@Table(name="pre_chequeo")
public class PreChequeo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", unique = true, length = 36)
    private String uuid;


    @NotBlank(message = "El centro es Obligatorio")
    private String centro;

    @NotBlank(message = "La fecha es Obligatoria")
    private String fecha;

    @NotBlank(message = "El buzo es Obligatorio")
    private String buzo;

    @NotBlank(message = "El supervisor es Obligatorio")
    private String supervisor;


    @Valid
    @NotEmpty(message="Debe existir al menos un item en el checklist")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="pre_chequeo_id")
    private List<ItemCheckList> items;

    @ElementCollection
    @CollectionTable(
            name="pre_chequeo_fotos",
            joinColumns =@JoinColumn(name="pre_chequeo_id")
    )
    @Column(name="foto")
    private List<String> fotos;

    @NotNull(message="La presion sistolica es Obligatoria")
    @Min(value=70, message="La presion sistolica es demasiado baja")
    @Max(value=250, message="La presion sistolica es demasiado alta")
    private Integer sistolica;

    @NotNull(message="La presion diastolica es Obligatoria")
    @Min(value=40, message="La presio diastolica es demasiado baja")
    @Max(value=150, message="La presin diastolica es demasiado alta")
    private Integer diastolica;

    @NotNull(message="La saturacion es Obligatoria")
    @Min(value=50, message="La saturacion es demasiado baja")
    @Max(value=100, message="La saturacion es demasiado alta")
    private Integer saturacion;

    private String observaciones;


    @ElementCollection
    @CollectionTable(
            name = "pre_chequeo_alertas",
            joinColumns = @JoinColumn(name = "pre_chequeo_id")
    )
    @Column(name = "alerta")
    private List<String> alertas;


    @NotNull(message = "La edad es obligatoria")
    @Min(value = 18, message = "La edad no puede ser menor a 18 años")
    private Integer edad;


    @NotNull(message = "El peso es obligatorio")
    @Min(value = 30, message = "El peso ingresado no es válido")
    private Double pesoKg;


    @NotNull(message = "La altura es obligatoria")
    @Min(value = 100, message = "La altura ingresada no es válida")
    private Double alturaCm;


    @NotNull(message = "Debe indicar si el examen preocupacional está vigente")
    private Boolean examenPreocupacionalVigente;


    @NotNull(message = "Debe indicar si el test de esfuerzo está vigente")
    private Boolean testEsfuerzoVigente;


    @NotNull(message = "El puntaje de Calbuco es obligatorio")
    @Min(value = 0)
    @Max(value = 100)
    private Integer puntajeEscuelaCalbuco;


    public PreChequeo() {
    }

    public PreChequeo(Long id, String centro, String fecha, String buzo, String supervisor, List<ItemCheckList> items, List<String> fotos, Integer sistolica, Integer diastolica, Integer saturacion, String observaciones, List<String> alertas, Integer edad, Double pesoKg, Double alturaCm, Boolean examenPreocupacionalVigente, Boolean testEsfuerzoVigente, Integer puntajeEscuelaCalbuco) {
        this.id = id;
        this.centro = centro;
        this.fecha = fecha;
        this.buzo = buzo;
        this.supervisor = supervisor;
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


    public String getUuid() { return uuid; }

    public void setUuid(String uuid) { this.uuid = uuid; }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
