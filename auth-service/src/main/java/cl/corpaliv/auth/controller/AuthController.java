package cl.corpaliv.auth.controller;

import cl.corpaliv.auth.assemblers.UsuarioModelAssembler;
import cl.corpaliv.auth.dto.LoginRequestDTO;
import cl.corpaliv.auth.dto.LoginResponseDTO;
import cl.corpaliv.auth.dto.UsuarioResponseDTO;
import cl.corpaliv.auth.exception.ResourceNotFoundException;
import cl.corpaliv.auth.exception.UnauthorizedException;
import cl.corpaliv.auth.model.Usuario;
import cl.corpaliv.auth.repository.UsuarioRepository;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;
    private final JwtEncoder jwt;
    private final String issuer;
    private final UsuarioModelAssembler assembler;
    public AuthController(UsuarioRepository repository, PasswordEncoder encoder, JwtEncoder jwt,
        @Value("${corpaliv.jwt-issuer}") String issuer, UsuarioModelAssembler assembler) {
        this.repository=repository; this.encoder=encoder; this.jwt=jwt; this.issuer=issuer; this.assembler=assembler;
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO credenciales) {
        Usuario u=repository.findByEmailIgnoreCase(credenciales.getEmail().trim()).orElse(null);
        if (u == null || !u.activo || !encoder.matches(credenciales.getPassword(),u.passwordHash))
            throw new UnauthorizedException("Credenciales inválidas");
        Instant now=Instant.now();
        var claims=JwtClaimsSet.builder().issuer(issuer).issuedAt(now)
            .expiresAt(now.plus(8,ChronoUnit.HOURS)).subject(u.email)
            .claim("roles", List.of(u.rol)).build();
        String token=jwt.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(),claims)).getTokenValue();
        return new LoginResponseDTO(token,"Bearer",28800);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public UsuarioResponseDTO perfil(Authentication auth) {
        return repository.findByEmailIgnoreCase(auth.getName()).map(assembler::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
}
