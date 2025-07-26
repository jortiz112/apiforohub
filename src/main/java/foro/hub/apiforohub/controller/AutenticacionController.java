package foro.hub.apiforohub.controller;

import foro.hub.apiforohub.domain.usuario.DatosAutenticacion;
import foro.hub.apiforohub.domain.usuario.Usuario;
import foro.hub.apiforohub.infra.security.DatosTokenJWT;
import foro.hub.apiforohub.infra.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AutenticacionController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager manager;

    @PostMapping
    public ResponseEntity<DatosTokenJWT> iniciarSesion(@RequestBody @Valid DatosAutenticacion datos) {
        var authenticationToken = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(datos.correo_electronico(), datos.contrasenia());
        var autenticacion = manager.authenticate(authenticationToken);

        var tokenJWT = tokenService.generateToken((Usuario) autenticacion.getPrincipal());
        return ResponseEntity.ok(new DatosTokenJWT(tokenJWT));
    }
}
