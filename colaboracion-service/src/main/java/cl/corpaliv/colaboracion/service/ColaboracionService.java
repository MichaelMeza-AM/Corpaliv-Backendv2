package cl.corpaliv.colaboracion.service;

import cl.corpaliv.colaboracion.dto.SolicitudRequestDTO;
import cl.corpaliv.colaboracion.exception.BadRequestException;
import cl.corpaliv.colaboracion.exception.ConflictException;
import cl.corpaliv.colaboracion.exception.ResourceNotFoundException;
import cl.corpaliv.colaboracion.model.HistorialSolicitud;
import cl.corpaliv.colaboracion.model.Solicitud;
import cl.corpaliv.colaboracion.repository.HistorialSolicitudRepository;
import cl.corpaliv.colaboracion.repository.SolicitudRepository;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ColaboracionService {
    private final SolicitudRepository solicitudes;
    private final HistorialSolicitudRepository historial;
    public ColaboracionService(SolicitudRepository solicitudes, HistorialSolicitudRepository historial) {
        this.solicitudes=solicitudes; this.historial=historial;
    }

    @Transactional
    public Solicitud crearSolicitud(SolicitudRequestDTO d) {
        String tipo=d.getTipo().trim().toUpperCase();
        if (!List.of("DONACION","ALIANZA","CAPACITACION").contains(tipo))
            throw new BadRequestException("Tipo no permitido");
        if (!Boolean.TRUE.equals(d.getAceptaPrivacidad()))
            throw new BadRequestException("Debe aceptar la política de privacidad");
        if (tipo.equals("DONACION") && (d.getMontoCLP()==null || d.getMontoCLP()<=0))
            throw new BadRequestException("El aporte requiere monto positivo");
        if (!tipo.equals("DONACION") && (d.getOrganizacion()==null || d.getOrganizacion().isBlank()
            || d.getDetalleColaboracion()==null || d.getDetalleColaboracion().trim().length()<10))
            throw new BadRequestException("Organización y detalle de al menos 10 caracteres requeridos");
        String descripcion=switch(tipo) {
            case "DONACION" -> "Aporte solidario";
            case "ALIANZA" -> "Programa de Inclusión Laboral";
            default -> "Capacitación en diversidad, inclusión y buen trato";
        };
        Solicitud s=new Solicitud(tipo,d.getNombre().trim(),descripcion,d.getCorreo().trim());
        s.telefono=limpiar(d.getTelefono()); s.organizacion=limpiar(d.getOrganizacion());
        s.cargo=limpiar(d.getCargo()); s.detalleColaboracion=limpiar(d.getDetalleColaboracion());
        s.notificaciones=Boolean.TRUE.equals(d.getNotificaciones());
        if (tipo.equals("DONACION")) {
            s.donanteTipo=d.getDonanteTipo(); s.montoCLP=d.getMontoCLP(); s.anonimo=Boolean.TRUE.equals(d.getAnonimo());
        }
        return solicitudes.save(s);
    }

    private String limpiar(String valor) { return valor==null || valor.isBlank()?null:valor.trim(); }

    @Transactional
    public Solicitud cambiarEstado(Long id, String nuevo, String motivo) {
        Solicitud s=obtenerSolicitud(id);
        if (!List.of("RECIBIDA","EN_REVISION","CONTACTADO","CONCRETADO","DESCARTADO").contains(nuevo))
            throw new BadRequestException("Estado no permitido");
        if (s.estado.equals(nuevo)) throw new ConflictException("Ya tiene ese estado");
        if (motivo!=null && motivo.length()>2000)
            throw new BadRequestException("Motivo demasiado largo");
        String anterior=s.estado; s.estado=nuevo;
        historial.save(new HistorialSolicitud(id,"CAMBIO_ESTADO",motivo==null?"":motivo.trim(),anterior,nuevo));
        return solicitudes.save(s);
    }

    @Transactional
    public HistorialSolicitud observar(Long id, String texto) {
        obtenerSolicitud(id);
        if (texto==null || texto.isBlank() || texto.length()>2000)
            throw new BadRequestException("Observación inválida");
        return historial.save(new HistorialSolicitud(id,"OBSERVACION",texto.trim(),null,null));
    }

    public Solicitud obtenerSolicitud(Long id) {
        return solicitudes.findById(id).orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));
    }

    @Transactional
    public void eliminarColaboracion(Long id) {
        Solicitud s=obtenerSolicitud(id);
        if (!(s.tipo.equals("ALIANZA") || s.tipo.equals("CAPACITACION")) || !s.estado.equals("RECIBIDA"))
            throw new ConflictException("Solo se elimina una colaboración recibida");
        solicitudes.delete(s); // FK ON DELETE CASCADE elimina el historial de la misma solicitud.
    }
}
