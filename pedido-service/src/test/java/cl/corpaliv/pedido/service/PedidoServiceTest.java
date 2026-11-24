package cl.corpaliv.pedido.service;

import cl.corpaliv.pedido.dto.PedidoRequestDTO;
import cl.corpaliv.pedido.exception.BadRequestException;
import cl.corpaliv.pedido.model.HistorialPedido;
import cl.corpaliv.pedido.model.Pedido;
import cl.corpaliv.pedido.repository.HistorialPedidoRepository;
import cl.corpaliv.pedido.repository.PedidoRepository;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PedidoServiceTest {
    private final PedidoRepository pedidos = mock(PedidoRepository.class);
    private final HistorialPedidoRepository historial = mock(HistorialPedidoRepository.class);

    @Test void pedidoNoSeGuardaSinAceptarCondiciones() {
        PedidoService service = new PedidoService(pedidos, historial, "http://localhost:1");
        var d = new PedidoRequestDTO(2L, "Ana", 2, 1795564800000L,
            false, "ana@example.cl", null, false);
        assertThrows(BadRequestException.class, () -> service.crearPedido(d));
        verifyNoInteractions(pedidos);
    }

    @Test void pedidoConservaPrecioYNombreConsultadosAlCatalogo() throws Exception {
        HttpServer servidor = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        servidor.createContext("/api/productos/2", intercambio -> {
            byte[] cuerpo = "{\"id\":2,\"nombre\":\"Cuenco Tierra\",\"precioCLP\":62000,\"activo\":true}"
                .getBytes(StandardCharsets.UTF_8);
            intercambio.getResponseHeaders().add("Content-Type", "application/json");
            intercambio.sendResponseHeaders(200, cuerpo.length);
            try (var salida = intercambio.getResponseBody()) { salida.write(cuerpo); }
        });
        servidor.start();
        try {
            PedidoService service = new PedidoService(pedidos, historial,
                "http://127.0.0.1:" + servidor.getAddress().getPort());
            when(pedidos.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));
            var d = new PedidoRequestDTO(2L, "Ana", 3, 1795564800000L,
                true, "ana@example.cl", "Retiro especial", true);
            Pedido pedido = service.crearPedido(d);
            assertEquals("Cuenco Tierra", pedido.productoNombre);
            assertEquals(62000, pedido.precioUnitarioCLP);
            assertEquals(186000L, pedido.totalCLP);
            assertEquals("RECIBIDA", pedido.estado);
        } finally {
            servidor.stop(0);
        }
    }

    @Test void cambioDeEstadoRegistraHistorialDelPedido() {
        PedidoService service = new PedidoService(pedidos, historial, "http://localhost:1");
        Pedido p = new Pedido(2L, "Cuenco Tierra", 62000, "Ana", 1,
            1795564800000L, false, "ana@example.cl", null);
        when(pedidos.findById(7L)).thenReturn(Optional.of(p));
        when(pedidos.save(p)).thenReturn(p);
        service.cambiarEstado(7L, "EN_REVISION", "Llamar a Ana");
        assertEquals("EN_REVISION", p.estado);
        var captor = ArgumentCaptor.forClass(HistorialPedido.class);
        verify(historial).save(captor.capture());
        assertEquals(7L, captor.getValue().pedidoId);
        assertEquals("RECIBIDA", captor.getValue().estadoAnterior);
        assertEquals("EN_REVISION", captor.getValue().estadoNuevo);
    }
}
