package tecnm.servcio.ubicacion.Service;

import java.util.List;

import tecnm.servcio.ubicacion.Dto.UbicacionRequestDTO;
import tecnm.servcio.ubicacion.Dto.UbicacionResponseDTO;

public interface UbicacionService {
	
	UbicacionResponseDTO crear(UbicacionRequestDTO dto);

    UbicacionResponseDTO obtenerPorId(Long id);

    List<UbicacionResponseDTO> obtenerTodas();

    List<UbicacionResponseDTO> buscarPorColonia(String colonia);

    UbicacionResponseDTO actualizar(Long id, UbicacionRequestDTO dto);

    void eliminar(Long id);

}
