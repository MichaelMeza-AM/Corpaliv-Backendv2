package cl.corpaliv.colaboracion.repository;

import cl.corpaliv.colaboracion.model.HistorialSolicitud;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistorialSolicitudRepository extends JpaRepository<HistorialSolicitud, Long> {
    List<HistorialSolicitud> findBySolicitudIdOrderByFechaRegistroAsc(Long solicitudId);
}
