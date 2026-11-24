package cl.corpaliv.colaboracion.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudRequestDTO {
    @NotBlank
    private String tipo;

    @NotBlank @Size(max = 160)
    private String nombre;

    @NotBlank @Email
    private String correo;

    @Size(max = 32)
    private String telefono;

    @Size(max = 160)
    private String organizacion;

    @Size(max = 160)
    private String cargo;

    @Size(max = 2000)
    private String detalleColaboracion;

    private Boolean notificaciones;

    private String donanteTipo;

    private Long montoCLP;

    private Boolean anonimo;

    @NotNull
    private Boolean aceptaPrivacidad;

}
