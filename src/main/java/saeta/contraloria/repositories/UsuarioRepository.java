package saeta.contraloria.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saeta.contraloria.entities.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario,Integer> {
    Usuario findByCorreoAndPassAndEstado(String correo, String pass, Integer estado);
}
