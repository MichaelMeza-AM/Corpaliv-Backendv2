package cl.corpaliv.auth.model;

import jakarta.persistence.*;

@Entity
@Table(name="usuarios")
public class Usuario {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long id;
    @Column(nullable=false, unique=true, length=254)
    public String email;
    @Column(name="password_hash", nullable=false, length=100)
    public String passwordHash;
    @Column(nullable=false, length=40)
    public String rol = "ROLE_GESTOR";
    @Column(nullable=false)
    public boolean activo = true;
    protected Usuario() {}
    public Usuario(String email, String passwordHash) {
        this.email=email; this.passwordHash=passwordHash;
    }
}
