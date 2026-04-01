package tecnm.servcio.ubicacion.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tecnm.servcio.ubicacion.Entity.Ubicacion;

import java.util.List;

@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Long> {

    List<Ubicacion> findByColoniaNombreContainingIgnoreCase(String nombreColonia);
}