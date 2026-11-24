package cl.corpaliv.colaboracion.controller;

import cl.corpaliv.colaboracion.assemblers.SolicitudModelAssembler;
import cl.corpaliv.colaboracion.dto.DonanteResponseDTO;
import cl.corpaliv.colaboracion.repository.SolicitudRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Lista pública para la "Red de donantes" de la app (no requiere iniciar sesión).
@RestController
@RequestMapping("/api/donantes")
public class DonanteController {
    private final SolicitudRepository repository;
    private final SolicitudModelAssembler assembler;

    public DonanteController(SolicitudRepository repository, SolicitudModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping
    public List<DonanteResponseDTO> listar() {
        return repository.findDonantesVisibles().stream().map(assembler::toDonanteResponse).toList();
    }
}