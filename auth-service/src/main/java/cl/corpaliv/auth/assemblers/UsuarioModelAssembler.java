package cl.corpaliv.auth.assemblers;

import cl.corpaliv.auth.dto.UsuarioResponseDTO;
import cl.corpaliv.auth.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioModelAssembler {
    public UsuarioResponseDTO toResponse(Usuario usuario) {
        return UsuarioResponseDTO.fromModel(usuario);
    }
}
