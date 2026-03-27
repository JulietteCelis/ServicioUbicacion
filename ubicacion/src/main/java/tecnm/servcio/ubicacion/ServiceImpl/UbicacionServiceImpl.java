package tecnm.servcio.ubicacion.ServiceImpl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importante para el rollback
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import tecnm.servcio.ubicacion.Dto.UbicacionRequestDTO;
import tecnm.servcio.ubicacion.Dto.UbicacionResponseDTO;
import tecnm.servcio.ubicacion.Entity.Ubicacion; // Asegúrate que empiece con Mayúscula
import tecnm.servcio.ubicacion.Entity.Colonia;
import tecnm.servcio.ubicacion.Entity.Ciudad;
import tecnm.servcio.ubicacion.Exceptions.UbicacionNotFoundException;
import tecnm.servcio.ubicacion.Repository.UbicacionRepository;
import tecnm.servcio.ubicacion.Repository.ColoniaRepository;
import tecnm.servcio.ubicacion.Repository.CiudadRepository;
import tecnm.servcio.ubicacion.Service.UbicacionService;
import tecnm.servcio.ubicacion.mapper.UbicacionMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class UbicacionServiceImpl implements UbicacionService {

    private final UbicacionRepository ubicacionRepository;
    private final ColoniaRepository coloniaRepository; 
    private final CiudadRepository ciudadRepository;   
    private final UbicacionMapper ubicacionMapper;

    @Override
    @Transactional
    public UbicacionResponseDTO crear(UbicacionRequestDTO dto) {
        log.info("Procesando ubicación en: {}, {}", dto.getColonia(), dto.getCiudad());
        
        // Lógica de normalización: Buscar o crear Ciudad
        Ciudad ciudad = ciudadRepository.findByNombreIgnoreCase(dto.getCiudad())
                .orElseGet(() -> ciudadRepository.save(Ciudad.builder()
                        .nombre(dto.getCiudad())
                        .build()));

        // Buscar o crear Colonia vinculada a la ciudad
        Colonia colonia = coloniaRepository.findByNombreIgnoreCaseAndCiudad(dto.getColonia(), ciudad)
                .orElseGet(() -> coloniaRepository.save(Colonia.builder()
                        .nombre(dto.getColonia())
                        .ciudad(ciudad)
                        .build()));

        Ubicacion entity = ubicacionMapper.toEntity(dto, colonia);
        return ubicacionMapper.toDTO(ubicacionRepository.save(entity));
    }

    @Override
    public UbicacionResponseDTO obtenerPorId(Long id) {
        log.info("Buscando ubicación con id: {}", id);
        return ubicacionRepository.findById(id)
                .map(ubicacionMapper::toDTO)
                .orElseThrow(() -> new UbicacionNotFoundException(id));
    }

    @Override
    public List<UbicacionResponseDTO> obtenerTodas() {
        log.info("Obteniendo todas las ubicaciones");
        return ubicacionRepository.findAll().stream()
                .map(ubicacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UbicacionResponseDTO> buscarPorColonia(String colonia) {
        log.info("Filtrando ubicaciones por colonia: {}", colonia);
        return ubicacionRepository.findByColoniaNombreContainingIgnoreCase(colonia)
                .stream()
                .map(ubicacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UbicacionResponseDTO actualizar(Long id, UbicacionRequestDTO dto) {
        log.info("Actualizando ubicación con id: {}", id);
        Ubicacion existente = ubicacionRepository.findById(id)
                .orElseThrow(() -> new UbicacionNotFoundException(id));

        // Actualizamos la lógica de ciudad/colonia para mantener la normalización
        Ciudad ciudad = ciudadRepository.findByNombreIgnoreCase(dto.getCiudad())
                .orElseGet(() -> ciudadRepository.save(Ciudad.builder().nombre(dto.getCiudad()).build()));

        Colonia colonia = coloniaRepository.findByNombreIgnoreCaseAndCiudad(dto.getColonia(), ciudad)
                .orElseGet(() -> coloniaRepository.save(Colonia.builder().nombre(dto.getColonia()).ciudad(ciudad).build()));

        existente.setLatitud(dto.getLatitud());
        existente.setLongitud(dto.getLongitud());
        existente.setDireccion(dto.getDireccion());
        existente.setColonia(colonia);

        return ubicacionMapper.toDTO(ubicacionRepository.save(existente));
    }

    @Override
    public void eliminar(Long id) {
        log.info("Eliminando ubicación con id: {}", id);
        if (!ubicacionRepository.existsById(id)) {
            throw new UbicacionNotFoundException(id);
        }
        ubicacionRepository.deleteById(id);
    }
}