package cl.corpaliv.colaboracion.dto;

import cl.corpaliv.colaboracion.model.HistorialSolicitud;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialSolicitudResponseDTO {
    private Long id;

    private Long solicitudId;

    private String tipo;

    private String descripcion;

    private String estadoAnterior;

    private String estadoNuevo;

    private long fechaRegistro;

    public static HistorialSolicitudResponseDTO fromModel(HistorialSolicitud model) {
        if (model == null) return null;
        return new HistorialSolicitudResponseDTO(
            model.id, model.solicitudId, model.tipo,
            model.descripcion, model.estadoAnterior, model.estadoNuevo,
            model.fechaRegistro
        );
    }
}
