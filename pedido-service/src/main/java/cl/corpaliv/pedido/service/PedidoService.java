package cl.corpaliv.pedido.service;

import cl.corpaliv.pedido.dto.PedidoRequestDTO;
import cl.corpaliv.pedido.dto.ProductoRemotoDTO;
import cl.corpaliv.pedido.exception.BadRequestException;
import cl.corpaliv.pedido.exception.ConflictException;
import cl.corpaliv.pedido.exception.ResourceNotFoundException;
import cl.corpaliv.pedido.exception.ServiceUnavailableException;
import cl.corpaliv.pedido.model.EstadoPedido;
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

    private static final int LARGO_MAXIMO_TEXTO = 2000;

    private final PedidoRepository pedidos;
    private final HistorialPedidoRepository historial;
    private final RestClient productos;

    public PedidoService(PedidoRepository pedidos,
                         HistorialPedidoRepository historial,
                         @Value("${corpaliv.producto-url}") String productoUrl) {
        this.pedidos = pedidos;
        this.historial = historial;
        this.productos = RestClient.create(productoUrl);
    }

    // ---------- Consultas ----------

    public List<Pedido> listarPedidos() {
        return pedidos.findAllByOrderByFechaRegistroDesc();
    }

    public Pedido obtenerPedido(Long id) {
        return pedidos.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
    }

    public List<HistorialPedido> historialDePedido(Long id) {
        obtenerPedido(id);
        return historial.findByPedidoIdOrderByFechaRegistroAsc(id);
    }

    // ---------- Comandos ----------

    @Transactional
    public Pedido crearPedido(PedidoRequestDTO datos) {
        validarCondiciones(datos);
        ProductoRemotoDTO producto = consultarProducto(datos.getProductoId());
        validarProductoDisponible(producto, datos.getProductoId());
        return pedidos.save(construirPedido(datos, producto));
    }

    @Transactional
    public Pedido cambiarEstado(Long id, String nuevoEstado, String motivo) {
        Pedido pedido = obtenerPedido(id);

        if (!EstadoPedido.esValido(nuevoEstado)) {
            throw new BadRequestException("Estado no permitido");
        }
        if (pedido.estado.equals(nuevoEstado)) {
            throw new ConflictException("Ya tiene ese estado");
        }
        if (motivo != null && motivo.length() > LARGO_MAXIMO_TEXTO) {
            throw new BadRequestException("Motivo demasiado largo");
        }

        String estadoAnterior = pedido.estado;
        pedido.estado = nuevoEstado;

        String descripcion = (motivo == null) ? "" : motivo.trim();
        historial.save(new HistorialPedido(id, "CAMBIO_ESTADO", descripcion, estadoAnterior, nuevoEstado));

        return pedidos.save(pedido);
    }

    @Transactional
    public HistorialPedido observar(Long id, String texto) {
        obtenerPedido(id);

        if (texto == null || texto.isBlank() || texto.length() > LARGO_MAXIMO_TEXTO) {
            throw new BadRequestException("Observación inválida");
        }

        return historial.save(new HistorialPedido(id, "OBSERVACION", texto.trim(), null, null));
    }

    // ---------- Apoyo para crear un pedido ----------

    private void validarCondiciones(PedidoRequestDTO datos) {
        if (!Boolean.TRUE.equals(datos.getAceptaCondiciones())) {
            throw new BadRequestException("Debe aceptar las condiciones");
        }
    }

    private ProductoRemotoDTO consultarProducto(Long productoId) {
        try {
            return productos.get()
                    .uri("/api/productos/{id}", productoId)
                    .retrieve()
                    .body(ProductoRemotoDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new BadRequestException("Producto no disponible");
        } catch (RestClientException e) {
            throw new ServiceUnavailableException("Catálogo no disponible", e);
        }
    }

    private void validarProductoDisponible(ProductoRemotoDTO producto, Long productoIdPedido) {
        boolean disponible = producto != null
                && productoIdPedido.equals(producto.getId())
                && producto.isActivo()
                && producto.getNombre() != null
                && !producto.getNombre().isBlank()
                && producto.getPrecioCLP() != null
                && producto.getPrecioCLP() > 0;

        if (!disponible) {
            throw new BadRequestException("Producto no disponible");
        }
    }

    private Pedido construirPedido(PedidoRequestDTO datos, ProductoRemotoDTO producto) {
        String notas = (datos.getNotas() == null || datos.getNotas().isBlank())
                ? null
                : datos.getNotas().trim();

        return new Pedido(
                producto.getId(),
                producto.getNombre(),
                producto.getPrecioCLP(),
                datos.getNombreRetira().trim(),
                datos.getCantidad(),
                datos.getFechaRetiro(),
                Boolean.TRUE.equals(datos.getEnvolturaRegalo()),
                datos.getCorreo().trim(),
                notas);
    }
}