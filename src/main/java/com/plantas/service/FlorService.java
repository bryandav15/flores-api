package com.plantas.service;

import com.plantas.model.Flor;
import com.plantas.repository.FlorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlorService {

    private final FlorRepository repository;

    public FlorService(FlorRepository repository) {
        this.repository = repository;
    }

    // ---- Obtener todas ----
    public List<Flor> findAll() {
        return repository.findAll();
    }

    // ---- Obtener por ID ----
    public Flor findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flor no encontrada con id: " + id));
    }

    // ---- Buscar por nombre ----
    public List<Flor> findByNombre(String nombre) {
        return repository.findByNombreContainingIgnoreCase(nombre);
    }

    // ---- Buscar por color ----
    public List<Flor> findByColor(String color) {
        return repository.findByColorIgnoreCase(color);
    }

    // ---- Buscar por temporada ----
    public List<Flor> findByTemporada(String temporada) {
        return repository.findByTemporadaIgnoreCase(temporada);
    }

    // ---- Buscar disponibles ----
    public List<Flor> findByDisponible(Boolean disponible) {
        return repository.findByDisponible(disponible);
    }

    // ---- Insertar ----
    public Flor save(Flor flor) {
        return repository.save(flor);
    }

    // ---- Actualizar (solo los campos que vienen en el body) ----
    public Flor update(Long id, Flor updates) {
        Flor existente = findById(id); // lanza excepción si no existe

        if (updates.getNombre() != null)      existente.setNombre(updates.getNombre());
        if (updates.getColor() != null)       existente.setColor(updates.getColor());
        if (updates.getFamilia() != null)     existente.setFamilia(updates.getFamilia());
        if (updates.getDescripcion() != null) existente.setDescripcion(updates.getDescripcion());
        if (updates.getPrecio() != null)      existente.setPrecio(updates.getPrecio());
        if (updates.getTemporada() != null)   existente.setTemporada(updates.getTemporada());
        if (updates.getDisponible() != null)  existente.setDisponible(updates.getDisponible());

        return repository.save(existente);
    }

    // ---- Borrar ----
    public void delete(Long id) {
        findById(id); // valida que exista antes de borrar
        repository.deleteById(id);
    }
}
