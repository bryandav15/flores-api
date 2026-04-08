package com.plantas.controller;

import com.plantas.dto.ApiResponse;
import com.plantas.model.Flor;
import com.plantas.service.FlorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flores")
public class FlorController {

    private final FlorService service;

    public FlorController(FlorService service) {
        this.service = service;
    }

    // ---- GET /api/flores ----
    @GetMapping
    public ResponseEntity<ApiResponse<List<Flor>>> findAll() {
        try {
            List<Flor> flores = service.findAll();
            return ResponseEntity.ok(new ApiResponse<>(true, "OK", flores));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // ---- GET /api/flores/{id} ----
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Flor>> findById(@PathVariable Long id) {
        try {
            Flor flor = service.findById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "OK", flor));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // ---- GET /api/flores/buscar?nombre=xxx ----
    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<List<Flor>>> buscarPorNombre(@RequestParam String nombre) {
        try {
            if (nombre == null || nombre.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "El parámetro 'nombre' es requerido", null));
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "OK", service.findByNombre(nombre)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // ---- GET /api/flores/color?color=rojo ----
    @GetMapping("/color")
    public ResponseEntity<ApiResponse<List<Flor>>> buscarPorColor(@RequestParam String color) {
        try {
            if (color == null || color.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "El parámetro 'color' es requerido", null));
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "OK", service.findByColor(color)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // ---- GET /api/flores/temporada?temporada=primavera ----
    @GetMapping("/temporada")
    public ResponseEntity<ApiResponse<List<Flor>>> buscarPorTemporada(@RequestParam String temporada) {
        try {
            if (temporada == null || temporada.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "El parámetro 'temporada' es requerido", null));
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "OK", service.findByTemporada(temporada)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // ---- POST /api/flores ----
    @PostMapping
    public ResponseEntity<ApiResponse<Flor>> create(@RequestBody Flor flor) {
        try {
            if (flor.getNombre() == null || flor.getNombre().isBlank()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "El campo 'nombre' es requerido", null));
            }
            Flor guardada = service.save(flor);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Flor creada exitosamente", guardada));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // ---- PUT /api/flores/{id} ----
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Flor>> update(@PathVariable Long id,
                                                     @RequestBody Flor updates) {
        try {
            Flor actualizada = service.update(id, updates);
            return ResponseEntity.ok(new ApiResponse<>(true, "Flor actualizada exitosamente", actualizada));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // ---- DELETE /api/flores/{id} ----
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Flor eliminada exitosamente", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}
