package cl.corpaliv.producto.model;

import jakarta.persistence.*;

@Entity
@Table(name = "productos")
public class Producto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(nullable = false, length = 160)
    public String nombre;
    @Column(nullable = false, length = 1000)
    public String descripcion;
    @Column(name = "precio_clp", nullable = false)
    public Integer precioCLP;
    @Column(nullable = false)
    public boolean activo = true;
    protected Producto() {}
    public Producto(String nombre, String descripcion, int precioCLP) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioCLP = precioCLP;
    }
}
