package cl.corpaliv.pedido.service;

import cl.corpaliv.pedido.dto.PedidoRequestDTO;
import cl.corpaliv.pedido.dto.ProductoRemotoDTO;
import cl.corpaliv.pedido.exception.BadRequestException;
import cl.corpaliv.pedido.exception.ConflictException;
import cl.corpaliv.pedido.exception.ResourceNotFoundException;
import cl.corpaliv.pedido.exception.ServiceUnavailableException;
import cl.corpaliv.pedido.model.HistorialPedido;
import cl.corpaliv.pedido.model.Pedido;
import cl.corpaliv.pedido.repository.HistorialPedidoRepository;
import cl.corpaliv.pedido.repository.PedidoRepository;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class PedidoService {
    private final PedidoRepository pedidos;
    private final HistorialPedidoRepository historial;
    private final RestClient productos;

    public PedidoService(PedidoRepository pedidos, HistorialPedidoRepository historial,
                         @Value("${corpaliv.producto-url}") String productoUrl) {
        this.pedidos = pedidos;
        this.historial = historial;
        this.productos = RestClient.create(productoUrl);
    }

    @Transactional
    public Pedido crearPedido(PedidoRequestDTO d) {
        if (!Boolean.TRUE.equals(d.getAceptaCondiciones()))
            throw new BadRequestException("Debe aceptar las condiciones");
        ProductoRemotoDTO producto;
        try {
            producto = productos.get().uri("/api/productos/{id}", d.getProductoId())
                .retrieve().body(ProductoRemotoDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new BadRequestException("Producto no disponible");
        } catch (RestClientException e) {
            throw new ServiceUnavailableException("Catálogo no disponible", e);
        }
        if (producto == null || !d.getProductoId().equals(producto.getId()) || !producto.isActivo()
            || producto.getNombre() == null || producto.getNombre().isBlank()
            || producto.getPrecioCLP() == null || producto.getPrecioCLP() <= 0)
            throw new BadRequestException("Producto no disponible");

        return pedidos.save(new Pedido(producto.getId(), producto.getNombre(), producto.getPrecioCLP(),
            d.getNombreRetira().trim(), d.getCantidad(), d.getFechaRetiro(), Boolean.TRUE.equals(d.getEnvolturaRegalo()),
            d.getCorreo().trim(), d.getNotas() == null || d.getNotas().isBlank() ? null : d.getNotas().trim()));
    }

    public Pedido obtenerPedido(Long id) {
        return pedidos.findById(id).orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
    }

    @Transactional
    public Pedido cambiarEstado(Long id, String nuevo, String motivo) {
        Pedido p = obtenerPedido(id);
        if (!List.of("RECIBIDA", "EN_REVISION", "CONTACTADO", "CONCRETADO", "DESCARTADO").contains(nuevo))
            throw new BadRequestException("Estado no permitido");
        if (p.estado.equals(nuevo)) throw new ConflictException("Ya tiene ese estado");
        if (motivo != null && motivo.length() > 2000)
            throw new BadRequestException("Motivo demasiado largo");
        String anterior = p.estado;
        p.estado = nuevo;
        historial.save(new HistorialPedido(id, "CAMBIO_ESTADO", motivo == null ? "" : motivo.trim(), anterior, nuevo));
        return pedidos.save(p);
    }

    @Transactional
    public HistorialPedido observar(Long id, String texto) {
        obtenerPedido(id);
        if (texto == null || texto.isBlank() || texto.length() > 2000)
            throw new BadRequestException("Observación inválida");
        return historial.save(new HistorialPedido(id, "OBSERVACION", texto.trim(), null, null));
    }
}
