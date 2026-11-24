package cl.corpaliv.colaboracion.controller;

import cl.corpaliv.colaboracion.assemblers.SolicitudModelAssembler;
import cl.corpaliv.colaboracion.dto.SolicitudRequestDTO;
import cl.corpaliv.colaboracion.exception.BadRequestException;
import cl.corpaliv.colaboracion.exception.GlobalExceptionHandler;
import cl.corpaliv.colaboracion.model.Solicitud;
import cl.corpaliv.colaboracion.repository.HistorialSolicitudRepository;
import cl.corpaliv.colaboracion.repository.SolicitudRepository;
import cl.corpaliv.colaboracion.service.ColaboracionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SolicitudControllerTest {
    @Mock private SolicitudRepository solicitudes;
    @Mock private HistorialSolicitudRepository historial;
    @Mock private ColaboracionService service;
    private MockMvc mockMvc;

    @BeforeEach
    void preparar() {
        mockMvc = MockMvcBuilders.standaloneSetup(new SolicitudController(solicitudes, historial, service,
            new SolicitudModelAssembler())).setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    void crearAlianzaRecibeLosDatosYDevuelve201() throws Exception {
        Solicitud guardada = new Solicitud("ALIANZA", "Camila", "Programa de Inclusión Laboral", "camila@ejemplo.cl");
        guardada.id = 7L;
        when(service.crearSolicitud(any())).thenReturn(guardada);

        mockMvc.perform(post("/api/solicitudes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"tipo":"ALIANZA","nombre":"Camila","correo":"camila@ejemplo.cl",
                     "organizacion":"Empresa","detalleColaboracion":"Inclusión laboral",
                     "aceptaPrivacidad":true}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(7))
            .andExpect(jsonPath("$.tipo").value("ALIANZA"));

        var entrada = ArgumentCaptor.forClass(SolicitudRequestDTO.class);
        verify(service).crearSolicitud(entrada.capture());
        assertEquals("Empresa", entrada.getValue().getOrganizacion());
    }

    @Test
    void cambiarEstadoEnviaIdYMotivoAlServicio() throws Exception {
        Solicitud actualizada = new Solicitud("ALIANZA", "Camila", "Programa", "camila@ejemplo.cl");
        actualizada.estado = "EN_REVISION";
        when(service.cambiarEstado(7L, "EN_REVISION", "Datos recibidos")).thenReturn(actualizada);

        mockMvc.perform(patch("/api/solicitudes/7/estado")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"estado\":\"EN_REVISION\",\"motivo\":\"Datos recibidos\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("EN_REVISION"));

        verify(service).cambiarEstado(7L, "EN_REVISION", "Datos recibidos");
    }

    @Test
    void eliminarColaboracionDevuelve204() throws Exception {
        mockMvc.perform(delete("/api/solicitudes/7"))
            .andExpect(status().isNoContent());

        verify(service).eliminarColaboracion(7L);
    }

    @Test
    void errorDeNegocioDevuelveMensajeYEstadoSinAlterarRuta() throws Exception {
        when(service.crearSolicitud(any())).thenThrow(new BadRequestException("Tipo no permitido"));
        mockMvc.perform(post("/api/solicitudes").contentType(MediaType.APPLICATION_JSON)
                .content("{\"tipo\":\"OTRO\",\"nombre\":\"Camila\",\"correo\":\"camila@ejemplo.cl\",\"aceptaPrivacidad\":true}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Tipo no permitido"))
            .andExpect(jsonPath("$.path").value("/api/solicitudes"));
    }
}
