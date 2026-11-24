package cl.corpaliv.auth.config;

import cl.corpaliv.auth.model.Usuario;
import cl.corpaliv.auth.repository.UsuarioRepository;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import javax.crypto.SecretKey;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
public class AuthConfig {
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean JwtEncoder jwtEncoder(SecretKey key) { return new NimbusJwtEncoder(new ImmutableSecret<>(key)); }

    @Bean ApplicationRunner crearGestorInicial(UsuarioRepository repository, PasswordEncoder encoder,
        @Value("${corpaliv.admin-email}") String email, @Value("${corpaliv.admin-password}") String password) {
        return args -> {
            if (password.length() < 10) throw new IllegalArgumentException("CORPALIV_ADMIN_PASSWORD debe tener 10 caracteres o más");
            if (!email.contains("@")) throw new IllegalArgumentException("CORPALIV_ADMIN_EMAIL inválido");
            String correoGestor = email.trim().toLowerCase(Locale.ROOT);
            if (repository.findByEmailIgnoreCase(correoGestor).isPresent()) return;

            // Si se inició una versión anterior, cambia solo la cuenta de demostración previa.
            var cuentaAnterior = repository.findByEmailIgnoreCase("gestor@ejemplo.cl");
            if (cuentaAnterior.isPresent()) {
                Usuario gestor = cuentaAnterior.get();
                gestor.email = correoGestor;
                gestor.passwordHash = encoder.encode(password);
                repository.save(gestor);
            } else if (repository.count() == 0) {
                repository.save(new Usuario(correoGestor, encoder.encode(password)));
            }
        };
    }
}
