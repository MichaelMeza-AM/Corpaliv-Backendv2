package cl.corpaliv.auth.controller;

import cl.corpaliv.auth.assemblers.UsuarioModelAssembler;
import cl.corpaliv.auth.dto.UsuarioRequestDTO;
import cl.corpaliv.auth.dto.UsuarioResponseDTO;
import cl.corpaliv.auth.exception.ConflictException;
import cl.corpaliv.auth.exception.ResourceNotFoundException;
import cl.corpaliv.auth.model.Usuario;
import cl.corpaliv.auth.repository.UsuarioRepository;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {
    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;
    private final UsuarioModelAssembler assembler;
    public UsuarioController(UsuarioRepository repository, PasswordEncoder encoder, UsuarioModelAssembler assembler) {
        this.repository=repository; this.encoder=encoder; this.assembler=assembler;
    }
    @GetMapping public List<UsuarioResponseDTO> listar() {
        return repository.findAll().stream().map(assembler::toResponse).toList();
    }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponseDTO crear(@Valid @RequestBody UsuarioRequestDTO datos) {
        String email=datos.getEmail().trim().toLowerCase();
        if (repository.findByEmailIgnoreCase(email).isPresent())
            throw new ConflictException("Correo ya registrado");
        return assembler.toResponse(repository.save(new Usuario(email,encoder.encode(datos.getPassword()))));
    }
    @PatchMapping("/{id}/activo")
    public UsuarioResponseDTO activar(@PathVariable Long id, @RequestParam boolean valor) {
        Usuario u=repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        u.activo=valor; return assembler.toResponse(repository.save(u));
    }
}
