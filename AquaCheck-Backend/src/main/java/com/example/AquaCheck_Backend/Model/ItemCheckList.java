package com.example.AquaCheck_Backend.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name="item_checklist")
public class ItemCheckList {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;


    @NotBlank(message="El nombre es Obligatorio")
    private String nombre;

    private Boolean critico=false;

    @NotNull(message="El estado es Obligatorio")
    @Enumerated(EnumType.STRING)//Hace que MySQL guarde los estados:PENDIENTE,NO_CUMPLE etc
    private Estado estado;//Conecta con el enum Estado

    private String observacion;

    private String foto;

    public ItemCheckList() {
    }

    public ItemCheckList(Long id, String nombre, Boolean critico, Estado estado, String observacion, String foto) {
        this.id = id;
        this.nombre = nombre;
        this.critico = critico;
        this.estado = estado;
        this.observacion = observacion;
        this.foto = foto;
    }

    //Getter y Setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getCritico() {
        return critico;
    }

    public void setCritico(Boolean critico) {
        this.critico = critico;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
