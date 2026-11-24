package cl.corpaliv.colaboracion.controller;

import cl.corpaliv.colaboracion.assemblers.SolicitudModelAssembler;
import cl.corpaliv.colaboracion.dto.CambioEstadoRequestDTO;
import cl.corpaliv.colaboracion.dto.HistorialSolicitudResponseDTO;
import cl.corpaliv.colaboracion.dto.ObservacionRequestDTO;
import cl.corpaliv.colaboracion.dto.SolicitudRequestDTO;
import cl.corpaliv.colaboracion.dto.SolicitudResponseDTO;
import cl.corpaliv.colaboracion.repository.HistorialSolicitudRepository;
import cl.corpaliv.colaboracion.repository.SolicitudRepository;
import cl.corpaliv.colaboracion.service.ColaboracionService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {
    private final SolicitudRepository repository;
    private final HistorialSolicitudRepository historial;
    private final ColaboracionService service;
    private final SolicitudModelAssembler assembler;
    public SolicitudController(SolicitudRepository repository, HistorialSolicitudRepository historial,
                               ColaboracionService service, SolicitudModelAssembler assembler) {
        this.repository=repository; this.historial=historial; this.service=service; this.assembler=assembler;
    }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public SolicitudResponseDTO crear(@Valid @RequestBody SolicitudRequestDTO entrada) {
        return assembler.toResponse(service.crearSolicitud(entrada));
    }
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping public List<SolicitudResponseDTO> listar() {
        return repository.findAllByOrderByFechaRegistroDesc().stream().map(assembler::toResponse).toList();
    }
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}") public SolicitudResponseDTO obtener(@PathVariable Long id) {
        return assembler.toResponse(service.obtenerSolicitud(id));
    }
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}/historial") public List<HistorialSolicitudResponseDTO> historial(@PathVariable Long id) {
        service.obtenerSolicitud(id);
        return historial.findBySolicitudIdOrderByFechaRegistroAsc(id).stream()
            .map(assembler::toHistorialResponse).toList();
    }
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{id}/estado")
    public SolicitudResponseDTO estado(@PathVariable Long id, @Valid @RequestBody CambioEstadoRequestDTO entrada) {
        return assembler.toResponse(service.cambiarEstado(id,entrada.getEstado(),entrada.getMotivo()));
    }
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{id}/observaciones") @ResponseStatus(HttpStatus.CREATED)
    public HistorialSolicitudResponseDTO observacion(@PathVariable Long id, @Valid @RequestBody ObservacionRequestDTO entrada) {
        return assembler.toHistorialResponse(service.observar(id,entrada.getTexto()));
    }
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) { service.eliminarColaboracion(id); }
}
