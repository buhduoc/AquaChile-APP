package com.example.AquaCheck_Backend.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;


@Entity
@Table(name="usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El usuario es Obligatorio.")
    @Column(unique=true, nullable=false)
    private String usuario;

    @NotBlank(message= "La contraseña es Obligatoria.")
    @Size(min=6, message="La contraseña debe tener al menos 6 caracteres.")
    private String clave;

    @NotBlank(message="El nombre es obligatorio")
    private String nombre;

    @NotBlank(message="El rol es Obligatorio.")
    private String rol;

    @NotBlank(message="El RUT es Obligatorio.")
    @Column(unique=true)
    private String rut;

    @NotBlank(message="El telefono es Obligatorio.")
    private String telefono;

    @NotBlank(message="El correo es Obligatorio.")
    @Email(message="El correo no es valido")
    private String correo;

    private Boolean aprobada;

    //Constructor vacio obligatorio para JPA

    public Usuario() {
    }

    public Usuario(String clave, String usuario, Long id, String nombre, String rol, String rut, String telefono, String correo, Boolean aprobada) {
        this.clave = clave;
        this.usuario = usuario;
        this.id = id;
        this.nombre = nombre;
        this.rol = rol;
        this.rut = rut;
        this.telefono = telefono;
        this.correo = correo;
        this.aprobada = aprobada;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Boolean getAprobada() {
        return aprobada;
    }

    public void setAprobada(Boolean aprobado) {
        this.aprobada = aprobado;
    }
}
