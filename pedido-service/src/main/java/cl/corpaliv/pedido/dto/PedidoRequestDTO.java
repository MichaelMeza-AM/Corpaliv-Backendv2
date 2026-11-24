package cl.corpaliv.pedido.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequestDTO {
    @NotNull @Positive
    private Long productoId;

    @NotBlank @Size(max = 160)
    private String nombreRetira;

    @NotNull @Min(1) @Max(100)
    private Integer cantidad;

    @NotNull @Positive
    private Long fechaRetiro;

    private Boolean envolturaRegalo;

    @NotBlank @Email
    private String correo;

    @Size(max = 1000)
    private String notas;

    @NotNull
    private Boolean aceptaCondiciones;

}
