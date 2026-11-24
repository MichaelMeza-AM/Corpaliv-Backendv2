package cl.corpaliv.auth.controller;

import cl.corpaliv.auth.assemblers.UsuarioModelAssembler;
import cl.corpaliv.auth.dto.LoginRequestDTO;
import cl.corpaliv.auth.exception.UnauthorizedException;
import cl.corpaliv.auth.model.Usuario;
import cl.corpaliv.auth.repository.UsuarioRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {
    @Test void claveIncorrectaNoEntregaToken() {
        UsuarioRepository usuarios=mock(UsuarioRepository.class);
        PasswordEncoder encoder=mock(PasswordEncoder.class);
        JwtEncoder jwt=mock(JwtEncoder.class);
        when(usuarios.findByEmailIgnoreCase("gestor@corpaliv.cl"))
            .thenReturn(Optional.of(new Usuario("gestor@corpaliv.cl","hash")));
        AuthController auth=new AuthController(usuarios,encoder,jwt,"corpaliv-local",new UsuarioModelAssembler());
        assertThrows(UnauthorizedException.class,
            ()->auth.login(new LoginRequestDTO("gestor@corpaliv.cl","incorrecta")));
        verifyNoInteractions(jwt);
    }
}
