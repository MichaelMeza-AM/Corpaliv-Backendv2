package cl.corpaliv.producto.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoRequestDTO {
    @NotBlank @Size(max = 160)
    private String nombre;

    @NotBlank @Size(max = 1000)
    private String descripcion;

    @NotNull @Min(1)
    private Integer precioCLP;

}
