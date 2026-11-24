package cl.corpaliv.producto.controller;

import cl.corpaliv.producto.assemblers.ProductoModelAssembler;
import cl.corpaliv.producto.dto.ProductoRequestDTO;
import cl.corpaliv.producto.dto.ProductoResponseDTO;
import cl.corpaliv.producto.exception.ResourceNotFoundException;
import cl.corpaliv.producto.model.Producto;
import cl.corpaliv.producto.repository.ProductoRepository;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    private final ProductoRepository repository;
    private final ProductoModelAssembler assembler;
    public ProductoController(ProductoRepository repository, ProductoModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping
    public List<ProductoResponseDTO> listar() {
        return repository.findByActivoTrueOrderByIdAsc().stream().map(assembler::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ProductoResponseDTO obtener(@PathVariable Long id) {
        Producto p = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        if (!p.activo) throw new ResourceNotFoundException("Producto no encontrado");
        return assembler.toResponse(p);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoResponseDTO crear(@Valid @RequestBody ProductoRequestDTO datos) {
        return assembler.toResponse(repository.save(new Producto(datos.getNombre(), datos.getDescripcion(), datos.getPrecioCLP())));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ProductoResponseDTO actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequestDTO datos) {
        Producto p = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        p.nombre = datos.getNombre(); p.descripcion = datos.getDescripcion(); p.precioCLP = datos.getPrecioCLP();
        return assembler.toResponse(repository.save(p));
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void retirar(@PathVariable Long id) {
        Producto p = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        p.activo = false; repository.save(p); // Histórico de pedidos conserva nombre y precio.
    }
}
