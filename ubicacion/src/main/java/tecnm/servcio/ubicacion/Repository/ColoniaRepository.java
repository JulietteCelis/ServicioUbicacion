package tecnm.servcio.ubicacion.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tecnm.servcio.ubicacion.Entity.Ciudad;
import tecnm.servcio.ubicacion.Entity.Colonia;

@Repository
public interface ColoniaRepository extends JpaRepository<Colonia, Long> {

    Optional<Colonia> findByNombreIgnoreCaseAndCiudad(String nombre, Ciudad ciudad);

    List<Colonia> findByCodigoPostalAndCiudad(String codigoPostal, Ciudad ciudad);

    List<Colonia> findByCodigoPostal(String codigoPostal);
}