package saeta.contraloria.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import saeta.contraloria.entities.Token;
import saeta.contraloria.entities.Usuario;
import saeta.contraloria.repositories.UsuarioRepository;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

@RequestMapping("auth")
@CrossOrigin("*")
@RestController
@SessionAttributes("token")
public class LoginController {


    @Autowired
    UsuarioRepository usuarioRepository;

    @ResponseBody
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity obtenerUsuario(@RequestParam("user") String usuario, @RequestParam("pass") String pass) {
        HashMap<String, Object> responseMap = new HashMap<>();
        String hashpwd = Hashing.sha256().hashString(pass, StandardCharsets.UTF_8).toString();
        Usuario u = usuarioRepository.findByCorreoAndPassAndEstado(usuario, hashpwd, 1);

        if (u == null) {
            responseMap.put("estaAutenticado", false);
            responseMap.put("mensaje", "Credenciales invalidas");
            return new ResponseEntity(responseMap, HttpStatus.BAD_REQUEST);
        }
        String token = obtenerToken(u);
        responseMap.put("mensaje", "Credenciales correctas");
        responseMap.put("estaAutenticado", true);
        responseMap.put("access_token", token);
        return new ResponseEntity(responseMap, HttpStatus.OK);
    }

    public String obtenerToken(Usuario usuario) {
        String token = "";
        String key = "Saeta";
        Algorithm algorithm = Algorithm.HMAC256(key);
        Date date = new Date();
        token = JWT.create().
                withIssuer("Saeta.Sac").
                withIssuedAt(date).
                withExpiresAt(new Date(date.getTime()+720000*1000L)).//duracion de 30 minutos
                        withClaim("id",usuario.getId()).
                withClaim("nombre",usuario.getNombre()).withClaim("user",usuario.getUser())
                .sign(algorithm);
        return token;
    }

    @ResponseBody
    @PostMapping("/get-credenciales")
    public ResponseEntity obtenerCredenciales(@RequestParam  String token){
        HashMap<String,Object> responseMap = new HashMap<>();
        DecodedJWT origintoken = JWT.decode(token);
        int id = origintoken.getClaim("id").asInt();
        Optional<Usuario> u = usuarioRepository.findById(id);
        responseMap.put("token",token);
        responseMap.put("usuario",u);
        return new ResponseEntity(responseMap,HttpStatus.OK);
    }

    @ResponseBody
    @PostMapping(value = "/validate-token", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity validarToken(@RequestBody Token token){
        HashMap<String, String> responseMap = new HashMap<>();
        String key = "Saeta";
        System.out.println("token: "+token.getToken());
        Algorithm algorithm =  Algorithm.HMAC256(key);
        JWTVerifier verifier = JWT.require(algorithm).withIssuer("Saeta.Sac").build();
        try {
            verifier.verify(token.getToken());
            responseMap.put("estado","ok");
            responseMap.put("mensaje","la verifcación pasó");
            return new ResponseEntity(responseMap,HttpStatus.OK);
        }catch (JWTVerificationException e){
            responseMap.put("estado","error");
            responseMap.put("mensaje","la verificación falló");
            return new ResponseEntity(responseMap,HttpStatus.BAD_REQUEST);
        }
    }
}