package cl.corpaliv.pedido.model;

import jakarta.persistence.*;

@Entity
@Table(name="pedidos")
public class Pedido {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Column(name="producto_id",nullable=false) public Long productoId;
    @Column(name="producto_nombre",nullable=false,length=160) public String productoNombre;
    @Column(name="precio_unitario_clp",nullable=false) public int precioUnitarioCLP;
    @Column(name="nombre_retira",nullable=false,length=160) public String nombreRetira;
    @Column(nullable=false) public int cantidad;
    @Column(name="fecha_retiro",nullable=false) public long fechaRetiro;
    @Column(name="envoltura_regalo",nullable=false) public boolean envolturaRegalo;
    @Column(name="acepta_condiciones",nullable=false) public boolean aceptaCondiciones;
    @Column(name="total_clp",nullable=false) public long totalCLP;
    @Column(name="fecha_registro",nullable=false) public long fechaRegistro=System.currentTimeMillis();
    @Column(nullable=false,length=32) public String estado="RECIBIDA";
    @Column(nullable=false,length=254) public String correo;
    @Column(length=1000) public String notas;
    protected Pedido() {}
    public Pedido(Long productoId, String productoNombre, int precioUnitarioCLP, String nombreRetira,
                  int cantidad, long fechaRetiro, boolean envolturaRegalo, String correo, String notas) {
        this.productoId=productoId; this.productoNombre=productoNombre; this.precioUnitarioCLP=precioUnitarioCLP;
        this.nombreRetira=nombreRetira; this.cantidad=cantidad; this.fechaRetiro=fechaRetiro;
        this.envolturaRegalo=envolturaRegalo; this.aceptaCondiciones=true;
        this.totalCLP=Math.multiplyExact((long)precioUnitarioCLP,cantidad);
        this.correo=correo; this.notas=notas;
    }
}
