package com.plantas.model;

import jakarta.persistence.*;

@Entity
@Table(name = "flores")
public class Flor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;           // ej: "Rosa Roja", "Tulipán Amarillo"

    @Column(length = 60)
    private String color;            // ej: "rojo", "blanco", "multicolor"

    @Column(length = 100)
    private String familia;          // ej: "Rosaceae", "Liliaceae"

    @Column(length = 255)
    private String descripcion;

    @Column
    private Double precio;           // precio por unidad

    @Column(length = 60)
    private String temporada;        // primavera / verano / otoño / invierno / todo el año

    @Column
    private Boolean disponible;      // ¿está en stock?

    // ---- Getters y Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getFamilia() { return familia; }
    public void setFamilia(String familia) { this.familia = familia; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public String getTemporada() { return temporada; }
    public void setTemporada(String temporada) { this.temporada = temporada; }

    public Boolean getDisponible() { return disponible; }
    public void setDisponible(Boolean disponible) { this.disponible = disponible; }
}
