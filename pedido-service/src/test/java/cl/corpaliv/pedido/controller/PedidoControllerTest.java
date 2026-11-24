package cl.corpaliv.pedido.controller;

import cl.corpaliv.pedido.assemblers.PedidoModelAssembler;
import cl.corpaliv.pedido.dto.PedidoRequestDTO;
import cl.corpaliv.pedido.exception.GlobalExceptionHandler;
import cl.corpaliv.pedido.model.Pedido;
import cl.corpaliv.pedido.repository.HistorialPedidoRepository;
import cl.corpaliv.pedido.repository.PedidoRepository;
import cl.corpaliv.pedido.service.PedidoService;
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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PedidoControllerTest {
    @Mock private PedidoRepository pedidos;
    @Mock private HistorialPedidoRepository historial;
    @Mock private PedidoService service;
    private MockMvc mockMvc;

    @BeforeEach
    void preparar() {
        mockMvc = MockMvcBuilders.standaloneSetup(new PedidoController(pedidos, historial, service,
            new PedidoModelAssembler())).setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    void crearPedidoRecibeLosDatosYDevuelve201() throws Exception {
        Pedido guardado = new Pedido(2L, "Cuenco Tierra", 62000, "Ana", 3,
            1795564800000L, true, "ana@ejemplo.cl", null);
        guardado.id = 12L;
        when(service.crearPedido(any())).thenReturn(guardado);

        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"productoId":2,"nombreRetira":"Ana","cantidad":3,"fechaRetiro":1795564800000,
                     "envolturaRegalo":true,"correo":"ana@ejemplo.cl","aceptaCondiciones":true}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(12))
            .andExpect(jsonPath("$.totalCLP").value(186000));

        var entrada = ArgumentCaptor.forClass(PedidoRequestDTO.class);
        verify(service).crearPedido(entrada.capture());
        assertEquals(3, entrada.getValue().getCantidad());
    }

    @Test
    void cambiarEstadoEnviaIdYMotivoAlServicio() throws Exception {
        Pedido actualizado = new Pedido(2L, "Cuenco Tierra", 62000, "Ana", 1,
            1795564800000L, false, "ana@ejemplo.cl", null);
        actualizado.estado = "EN_REVISION";
        when(service.cambiarEstado(12L, "EN_REVISION", "Llamar a Ana")).thenReturn(actualizado);

        mockMvc.perform(patch("/api/pedidos/12/estado")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"estado\":\"EN_REVISION\",\"motivo\":\"Llamar a Ana\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("EN_REVISION"));

        verify(service).cambiarEstado(12L, "EN_REVISION", "Llamar a Ana");
    }

    @Test
    void unPedidoNoTieneRutaParaEliminarse() throws Exception {
        mockMvc.perform(delete("/api/pedidos/12"))
            .andExpect(status().isMethodNotAllowed());

        verifyNoInteractions(service, pedidos, historial);
    }
}
