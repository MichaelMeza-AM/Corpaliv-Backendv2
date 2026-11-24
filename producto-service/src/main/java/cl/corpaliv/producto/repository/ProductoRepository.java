package cl.corpaliv.producto.repository;

import cl.corpaliv.producto.model.Producto;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByActivoTrueOrderByIdAsc();
}
