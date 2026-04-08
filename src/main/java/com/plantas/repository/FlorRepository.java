package com.plantas.repository;

import com.plantas.model.Flor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlorRepository extends JpaRepository<Flor, Long> {

    List<Flor> findByNombreContainingIgnoreCase(String nombre);

    List<Flor> findByColorIgnoreCase(String color);

    List<Flor> findByTemporadaIgnoreCase(String temporada);

    List<Flor> findByDisponible(Boolean disponible);
}
