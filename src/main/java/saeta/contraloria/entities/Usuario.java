package saeta.contraloria.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "usuario")
@Data
public class Usuario implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "int_id", nullable = false)
    private Integer id;

    @Column(name = "var_user")
    private String user;

    @Column(name = "var_correo")
    private String correo;

    @Column(name = "var_pass")
    private String pass;

    @Column(name = "int_estado")
    private Integer estado;

    @Column(name = "var_nombre")
    private String nombre;
}
