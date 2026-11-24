package cl.corpaliv.colaboracion.service;

import cl.corpaliv.colaboracion.dto.SolicitudRequestDTO;
import cl.corpaliv.colaboracion.exception.BadRequestException;
import cl.corpaliv.colaboracion.exception.ConflictException;
import cl.corpaliv.colaboracion.model.HistorialSolicitud;
import cl.corpaliv.colaboracion.model.Solicitud;
import cl.corpaliv.colaboracion.repository.HistorialSolicitudRepository;
import cl.corpaliv.colaboracion.repository.SolicitudRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ColaboracionServiceTest {
    SolicitudRepository solicitudes=mock(SolicitudRepository.class);
    HistorialSolicitudRepository historial=mock(HistorialSolicitudRepository.class);
    ColaboracionService service=new ColaboracionService(solicitudes,historial);

    @Test void aporteSinMontoNoSeGuarda() {
        var d=new SolicitudRequestDTO("DONACION","María","maria@example.cl",null,null,null,
            null,false,"PERSONA",null,false,true);
        assertThrows(BadRequestException.class,()->service.crearSolicitud(d));
        verifyNoInteractions(solicitudes);
    }

    @Test void cambioDeEstadoRegistraHistorial() {
        Solicitud s=new Solicitud("ALIANZA","Rodrigo","Programa","r@example.cl");
        when(solicitudes.findById(7L)).thenReturn(Optional.of(s));
        when(solicitudes.save(s)).thenReturn(s);
        service.cambiarEstado(7L,"EN_REVISION","Datos recibidos");
        assertEquals("EN_REVISION",s.estado);
        var captor=ArgumentCaptor.forClass(HistorialSolicitud.class);
        verify(historial).save(captor.capture());
        assertEquals("RECIBIDA",captor.getValue().estadoAnterior);
        assertEquals("EN_REVISION",captor.getValue().estadoNuevo);
    }

    @Test void aporteNoSePuedeEliminarComoColaboracion() {
        Solicitud s=new Solicitud("DONACION","María","Aporte","m@example.cl");
        when(solicitudes.findById(3L)).thenReturn(Optional.of(s));
        assertThrows(ConflictException.class,()->service.eliminarColaboracion(3L));
        verify(solicitudes,never()).delete(any());
    }

    @Test void alianzaRecibidaSePuedeEliminar() {
        Solicitud s=new Solicitud("ALIANZA","María","Programa","m@example.cl");
        when(solicitudes.findById(4L)).thenReturn(Optional.of(s));
        service.eliminarColaboracion(4L);
        verify(solicitudes).delete(s);
    }
}
