package cl.corpaliv.pedido.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoRemotoDTO {
    private Long id;
    private String nombre;
    private Integer precioCLP;
    private boolean activo;
}
