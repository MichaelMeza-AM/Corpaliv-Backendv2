package cl.corpaliv.colaboracion.repository;

import cl.corpaliv.colaboracion.model.Solicitud;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    List<Solicitud> findAllByOrderByFechaRegistroDesc();
}
