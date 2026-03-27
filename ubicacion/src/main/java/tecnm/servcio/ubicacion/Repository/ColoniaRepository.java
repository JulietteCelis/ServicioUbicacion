package tecnm.servcio.ubicacion.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tecnm.servcio.ubicacion.Entity.Ciudad;
import tecnm.servcio.ubicacion.Entity.Colonia;

import java.util.Optional;

@Repository
public interface ColoniaRepository extends JpaRepository<Colonia, Long> {

    Optional<Colonia> findByNombreIgnoreCaseAndCiudad(String nombre, Ciudad ciudad);
}