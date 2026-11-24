package cl.corpaliv.colaboracion.assemblers;

import cl.corpaliv.colaboracion.dto.HistorialSolicitudResponseDTO;
import cl.corpaliv.colaboracion.dto.SolicitudResponseDTO;
import cl.corpaliv.colaboracion.model.HistorialSolicitud;
import cl.corpaliv.colaboracion.model.Solicitud;
import org.springframework.stereotype.Component;

@Component
public class SolicitudModelAssembler {
    public SolicitudResponseDTO toResponse(Solicitud solicitud) {
        return SolicitudResponseDTO.fromModel(solicitud);
    }

    public HistorialSolicitudResponseDTO toHistorialResponse(HistorialSolicitud evento) {
        return HistorialSolicitudResponseDTO.fromModel(evento);
    }
}
