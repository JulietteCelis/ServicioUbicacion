package tecnm.servcio.ubicacion.Repository;

import tecnm.servcio.ubicacion.Entity.ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UbicacionRepository extends JpaRepository<ubicacion, Long> {

    List<ubicacion> findByColoniaContainingIgnoreCase(String colonia);
    List<ubicacion> findByCiudadContainingIgnoreCase(String ciudad);
}