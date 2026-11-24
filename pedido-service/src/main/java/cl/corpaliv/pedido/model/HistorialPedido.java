package cl.corpaliv.pedido.model;

import jakarta.persistence.*;

@Entity
@Table(name="historial_pedidos")
public class HistorialPedido {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Column(name="pedido_id",nullable=false) public Long pedidoId;
    @Column(nullable=false,length=32) public String tipo;
    @Column(nullable=false,length=2000) public String descripcion;
    @Column(name="estado_anterior",length=32) public String estadoAnterior;
    @Column(name="estado_nuevo",length=32) public String estadoNuevo;
    @Column(name="fecha_registro",nullable=false) public long fechaRegistro=System.currentTimeMillis();
    protected HistorialPedido() {}
    public HistorialPedido(Long pedidoId, String tipo, String descripcion, String anterior, String nuevo) {
        this.pedidoId=pedidoId; this.tipo=tipo; this.descripcion=descripcion;
        this.estadoAnterior=anterior; this.estadoNuevo=nuevo;
    }
}
