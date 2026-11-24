package cl.corpaliv.colaboracion.repository;

import cl.corpaliv.colaboracion.model.Solicitud;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    List<Solicitud> findAllByOrderByFechaRegistroDesc();

    // Aportes que se pueden mostrar en la "Red de donantes".
    @Query("select s from Solicitud s where s.tipo = 'DONACION' and s.anonimo = false "
         + "and s.estado <> 'DESCARTADO' order by s.fechaRegistro desc")
    List<Solicitud> findDonantesVisibles();
}