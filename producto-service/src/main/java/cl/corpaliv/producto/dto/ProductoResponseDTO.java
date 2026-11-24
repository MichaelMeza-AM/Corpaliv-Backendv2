package cl.corpaliv.producto.dto;

import cl.corpaliv.producto.model.Producto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponseDTO {
    private Long id;

    private String nombre;

    private String descripcion;

    private Integer precioCLP;

    private boolean activo;

    public static ProductoResponseDTO fromModel(Producto model) {
        if (model == null) return null;
        return new ProductoResponseDTO(
            model.id, model.nombre, model.descripcion,
            model.precioCLP, model.activo
        );
    }
}
