package cl.corpaliv.colaboracion.model;

import jakarta.persistence.*;

@Entity
@Table(name="solicitudes")
public class Solicitud {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long id;
    @Column(nullable=false, length=32) public String tipo;
    @Column(nullable=false, length=160) public String nombre;
    @Column(nullable=false, length=1000) public String descripcion;
    @Column(nullable=false, length=32) public String estado="RECIBIDA";
    @Column(name="fecha_registro",nullable=false) public long fechaRegistro=System.currentTimeMillis();
    @Column(nullable=false,length=254) public String correo;
    @Column(length=32) public String telefono;
    @Column(length=160) public String organizacion;
    @Column(length=160) public String cargo;
    @Column(name="detalle_colaboracion",length=2000) public String detalleColaboracion;
    public Boolean notificaciones;
    @Column(name="donante_tipo",length=20) public String donanteTipo;
    @Column(name="monto_clp") public Long montoCLP;
    public Boolean anonimo;
    protected Solicitud() {}
    public Solicitud(String tipo, String nombre, String descripcion, String correo) {
        this.tipo=tipo; this.nombre=nombre; this.descripcion=descripcion; this.correo=correo;
    }
}
