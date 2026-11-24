package cl.corpaliv.pedido.repository;

import cl.corpaliv.pedido.model.HistorialPedido;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistorialPedidoRepository extends JpaRepository<HistorialPedido, Long> {
    List<HistorialPedido> findByPedidoIdOrderByFechaRegistroAsc(Long pedidoId);
}
