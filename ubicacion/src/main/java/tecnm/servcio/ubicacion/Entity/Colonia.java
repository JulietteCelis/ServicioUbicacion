package tecnm.servcio.ubicacion.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "colonia")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Colonia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(name = "codigo_postal", length = 10)
    private String codigoPostal;

    @ManyToOne
    @JoinColumn(name = "ciudad_id")
    private Ciudad ciudad;
}