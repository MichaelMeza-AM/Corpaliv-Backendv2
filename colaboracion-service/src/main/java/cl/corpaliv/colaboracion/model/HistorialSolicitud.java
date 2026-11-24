package cl.corpaliv.colaboracion.model;

import jakarta.persistence.*;

@Entity
@Table(name="historial_solicitudes")
public class HistorialSolicitud {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Column(name="solicitud_id",nullable=false) public Long solicitudId;
    @Column(nullable=false,length=32) public String tipo;
    @Column(nullable=false,length=2000) public String descripcion;
    @Column(name="estado_anterior",length=32) public String estadoAnterior;
    @Column(name="estado_nuevo",length=32) public String estadoNuevo;
    @Column(name="fecha_registro",nullable=false) public long fechaRegistro=System.currentTimeMillis();
    protected HistorialSolicitud() {}
    public HistorialSolicitud(Long solicitudId, String tipo, String descripcion, String anterior, String nuevo) {
        this.solicitudId=solicitudId; this.tipo=tipo; this.descripcion=descripcion;
        this.estadoAnterior=anterior; this.estadoNuevo=nuevo;
    }
}
