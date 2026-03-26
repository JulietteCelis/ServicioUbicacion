package tecnm.servcio.ubicacion.ServiceImpl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import tecnm.servcio.ubicacion.Dto.UbicacionRequestDTO;
import tecnm.servcio.ubicacion.Dto.UbicacionResponseDTO;
import tecnm.servcio.ubicacion.Entity.ubicacion;
import tecnm.servcio.ubicacion.Exceptions.UbicacionNotFoundException;
import tecnm.servcio.ubicacion.Repository.UbicacionRepository;
import tecnm.servcio.ubicacion.Service.UbicacionService;
import tecnm.servcio.ubicacion.mapper.UbicacionMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class UbicacionServiceImpl implements UbicacionService {

    private final UbicacionRepository ubicacionRepository;
    private final UbicacionMapper ubicacionMapper;

    @Override
    public UbicacionResponseDTO crear(UbicacionRequestDTO dto) {
        log.info("Creando nueva ubicación en colonia: {}", dto.getColonia());
        ubicacion entity = ubicacionMapper.toEntity(dto);
        return ubicacionMapper.toDTO(ubicacionRepository.save(entity));
    }

    @Override
    public UbicacionResponseDTO obtenerPorId(Long id) {
        log.info("Buscando ubicación con id: {}", id);
        ubicacion entity = ubicacionRepository.findById(id)
                .orElseThrow(() -> new UbicacionNotFoundException(id));
        return ubicacionMapper.toDTO(entity);
    }

    @Override
    public List<UbicacionResponseDTO> obtenerTodas() {
        log.info("Obteniendo todas las ubicaciones");
        return ubicacionRepository.findAll()
                .stream()
                .map(ubicacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UbicacionResponseDTO> buscarPorColonia(String colonia) {
        log.info("Buscando ubicaciones en colonia: {}", colonia);
        return ubicacionRepository.findByColoniaContainingIgnoreCase(colonia)
                .stream()
                .map(ubicacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UbicacionResponseDTO actualizar(Long id, UbicacionRequestDTO dto) {
        log.info("Actualizando ubicación con id: {}", id);
        ubicacion existente = ubicacionRepository.findById(id)
                .orElseThrow(() -> new UbicacionNotFoundException(id));

        existente.setLatitud(dto.getLatitud());
        existente.setLongitud(dto.getLongitud());
        existente.setDireccion(dto.getDireccion());
        existente.setColonia(dto.getColonia());
        existente.setCiudad(dto.getCiudad());

        return ubicacionMapper.toDTO(ubicacionRepository.save(existente));
    }

    @Override
    public void eliminar(Long id) {
        log.info("Eliminando ubicación con id: {}", id);
        if (!ubicacionRepository.existsById(id)) {
            throw new UbicacionNotFoundException(id);
        }
        ubicacionRepository.deleteById(id);
        log.info("Ubicación con id {} eliminada correctamente", id);
    }
}