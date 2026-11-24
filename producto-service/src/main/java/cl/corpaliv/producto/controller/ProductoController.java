package cl.corpaliv.producto.controller;

import cl.corpaliv.producto.assemblers.ProductoModelAssembler;
import cl.corpaliv.producto.dto.ProductoRequestDTO;
import cl.corpaliv.producto.dto.ProductoResponseDTO;
import cl.corpaliv.producto.service.ProductoService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    private final ProductoService service;
    private final ProductoModelAssembler assembler;
    public ProductoController(ProductoService service, ProductoModelAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @GetMapping
    public List<ProductoResponseDTO> listar() {
        return service.listar().stream().map(assembler::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ProductoResponseDTO obtener(@PathVariable Long id) {
        return assembler.toResponse(service.obtener(id));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoResponseDTO crear(@Valid @RequestBody ProductoRequestDTO datos) {
        return assembler.toResponse(service.crear(datos));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ProductoResponseDTO actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequestDTO datos) {
        return assembler.toResponse(service.actualizar(id, datos));
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void retirar(@PathVariable Long id) {
        service.retirar(id);
    }
}
