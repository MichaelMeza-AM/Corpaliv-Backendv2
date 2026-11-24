package cl.corpaliv.colaboracion.dto;

import cl.corpaliv.colaboracion.model.Solicitud;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudResponseDTO {
    private Long id;

    private String tipo;

    private String nombre;

    private String descripcion;

    private String estado;

    private long fechaRegistro;

    private String correo;

    private String telefono;

    private String organizacion;

    private String cargo;

    private String detalleColaboracion;

    private Boolean notificaciones;

    private String donanteTipo;

    private Long montoCLP;

    private Boolean anonimo;

    public static SolicitudResponseDTO fromModel(Solicitud model) {
        if (model == null) return null;
        return new SolicitudResponseDTO(
            model.id, model.tipo, model.nombre,
            model.descripcion, model.estado, model.fechaRegistro,
            model.correo, model.telefono, model.organizacion,
            model.cargo, model.detalleColaboracion, model.notificaciones,
            model.donanteTipo, model.montoCLP, model.anonimo
        );
    }
}
