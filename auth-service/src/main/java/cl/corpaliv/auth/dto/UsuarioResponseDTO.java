package cl.corpaliv.auth.dto;

import cl.corpaliv.auth.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {
    private Long id;

    private String email;

    private String rol;

    private boolean activo;

    public static UsuarioResponseDTO fromModel(Usuario model) {
        if (model == null) return null;
        return new UsuarioResponseDTO(
            model.id, model.email, model.rol,
            model.activo
        );
    }
}
