package cl.corpaliv.colaboracion.dto;

import cl.corpaliv.colaboracion.model.Solicitud;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Datos públicos de un aporte: sin correo ni teléfono.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonanteResponseDTO {
    private Long id;            // id de la solicitud (la app lo usa para unir la foto guardada en el teléfono)
    private String nombre;
    private String donanteTipo; // PERSONA o EMPRESA
    private Long montoCLP;
    private long fechaRegistro;

    public static DonanteResponseDTO fromModel(Solicitud model) {
        if (model == null) return null;
        return new DonanteResponseDTO(
            model.id, model.nombre, model.donanteTipo,
            model.montoCLP, model.fechaRegistro
        );
    }
}