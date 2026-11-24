package cl.corpaliv.producto.assemblers;

import cl.corpaliv.producto.dto.ProductoResponseDTO;
import cl.corpaliv.producto.model.Producto;
import org.springframework.stereotype.Component;

@Component
public class ProductoModelAssembler {
    public ProductoResponseDTO toResponse(Producto producto) {
        return ProductoResponseDTO.fromModel(producto);
    }
}
