package com.certificaciones.backend.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final OperatorUserRepository repository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthController(
            OperatorUserRepository repository,
            PasswordEncoder encoder,
            JwtService jwtService
    ) {
        this.repository = repository;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        OperatorUser user = repository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no existe"));

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Clave incorrecta");
        }

        String token = jwtService.generateToken(user.getUsername());

        return new LoginResponse(token);
    }
}
