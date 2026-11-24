package cl.corpaliv.pedido.controller;

import cl.corpaliv.pedido.assemblers.PedidoModelAssembler;
import cl.corpaliv.pedido.dto.CambioEstadoRequestDTO;
import cl.corpaliv.pedido.dto.HistorialPedidoResponseDTO;
import cl.corpaliv.pedido.dto.ObservacionRequestDTO;
import cl.corpaliv.pedido.dto.PedidoRequestDTO;
import cl.corpaliv.pedido.dto.PedidoResponseDTO;
import cl.corpaliv.pedido.service.PedidoService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService service;
    private final PedidoModelAssembler assembler;

    public PedidoController(PedidoService service, PedidoModelAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponseDTO crear(@Valid @RequestBody PedidoRequestDTO entrada) {
        return assembler.toResponse(service.crearPedido(entrada));
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public List<PedidoResponseDTO> listar() {
        return service.listarPedidos().stream()
                .map(assembler::toResponse)
                .toList();
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public PedidoResponseDTO obtener(@PathVariable Long id) {
        return assembler.toResponse(service.obtenerPedido(id));
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}/historial")
    public List<HistorialPedidoResponseDTO> historial(@PathVariable Long id) {
        return service.historialDePedido(id).stream()
                .map(assembler::toHistorialResponse)
                .toList();
    }

    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{id}/estado")
    public PedidoResponseDTO cambiarEstado(@PathVariable Long id,
                                           @Valid @RequestBody CambioEstadoRequestDTO entrada) {
        return assembler.toResponse(
                service.cambiarEstado(id, entrada.getEstado(), entrada.getMotivo()));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{id}/observaciones")
    @ResponseStatus(HttpStatus.CREATED)
    public HistorialPedidoResponseDTO agregarObservacion(@PathVariable Long id,
                                                         @Valid @RequestBody ObservacionRequestDTO entrada) {
        return assembler.toHistorialResponse(service.observar(id, entrada.getTexto()));
    }
}