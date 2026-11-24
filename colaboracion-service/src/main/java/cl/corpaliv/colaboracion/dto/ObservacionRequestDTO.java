package cl.corpaliv.colaboracion.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObservacionRequestDTO {
    @NotBlank
    private String texto;

}
