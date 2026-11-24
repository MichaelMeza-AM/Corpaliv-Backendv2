package cl.corpaliv.auth.config;

import cl.corpaliv.auth.model.Usuario;
import cl.corpaliv.auth.repository.UsuarioRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthConfigTest {
    private final UsuarioRepository repository = mock(UsuarioRepository.class);
    private final PasswordEncoder encoder = mock(PasswordEncoder.class);

    @Test void creaElGestorConLasCredencialesConfiguradas() throws Exception {
        when(repository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(repository.count()).thenReturn(0L);
        when(encoder.encode("albalab2026")).thenReturn("hash-nuevo");

        new AuthConfig().crearGestorInicial(repository, encoder,
            "gestor@corpaliv.cl", "albalab2026").run(null);

        verify(repository).save(argThat(usuario ->
            usuario.email.equals("gestor@corpaliv.cl") && usuario.passwordHash.equals("hash-nuevo")));
    }

    @Test void migraSoloElGestorDePruebaAnteriorSinBorrarOtrosUsuarios() throws Exception {
        Usuario anterior = new Usuario("gestor@ejemplo.cl", "hash-antiguo");
        anterior.id = 7L;
        when(repository.findByEmailIgnoreCase("gestor@corpaliv.cl")).thenReturn(Optional.empty());
        when(repository.findByEmailIgnoreCase("gestor@ejemplo.cl")).thenReturn(Optional.of(anterior));
        when(encoder.encode("albalab2026")).thenReturn("hash-nuevo");

        new AuthConfig().crearGestorInicial(repository, encoder,
            "gestor@corpaliv.cl", "albalab2026").run(null);

        assertEquals(7L, anterior.id);
        assertEquals("gestor@corpaliv.cl", anterior.email);
        assertEquals("hash-nuevo", anterior.passwordHash);
        verify(repository).save(anterior);
        verify(repository, never()).count();
    }
}
