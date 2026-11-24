package cl.corpaliv.pedido.repository;

import cl.corpaliv.pedido.model.Pedido;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findAllByOrderByFechaRegistroDesc();
}
