package tecnm.servcio.ubicacion.ServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth.client_sdk.client.UsuarioClient;
import com.auth.client_sdk.dto.UsuarioAuthDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tecnm.servcio.ubicacion.Dto.UbicacionRequestDTO;
import tecnm.servcio.ubicacion.Dto.UbicacionResponseDTO;
import tecnm.servcio.ubicacion.Entity.Ciudad;
import tecnm.servcio.ubicacion.Entity.Colonia;
import tecnm.servcio.ubicacion.Entity.Ubicacion;
import tecnm.servcio.ubicacion.Exceptions.UbicacionNotFoundException;
import tecnm.servcio.ubicacion.Repository.CiudadRepository;
import tecnm.servcio.ubicacion.Repository.ColoniaRepository;
import tecnm.servcio.ubicacion.Repository.UbicacionRepository;
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
    private final UsuarioClient usuarioClient;

    @Override
    @Transactional
    public UbicacionResponseDTO crear(UbicacionRequestDTO dto) {
        log.info("Procesando ubicación en: {}, {}", dto.getColonia(), dto.getCiudad());

        Ciudad ciudad = ciudadRepository.findByNombreIgnoreCase(dto.getCiudad())
                .orElseGet(() -> ciudadRepository.save(
                        Ciudad.builder()
                                .nombre(dto.getCiudad())
                                .build()
                ));

        String nombreColonia = resolverNombreColonia(dto, ciudad);

        Colonia colonia = coloniaRepository.findByNombreIgnoreCaseAndCiudad(nombreColonia, ciudad)
                .orElseGet(() -> coloniaRepository.save(
                        Colonia.builder()
                                .nombre(nombreColonia)
                                .codigoPostal(obtenerCodigoPostalValido(dto.getCodigoPostal()))
                                .ciudad(ciudad)
                                .build()
                ));

        Ubicacion entity = ubicacionMapper.toEntity(dto, colonia);

        return ubicacionMapper.toDTO(ubicacionRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public UbicacionResponseDTO obtenerPorId(Long id) {
        log.info("Buscando ubicación con id: {}", id);

        return ubicacionRepository.findById(id)
                .map(ubicacionMapper::toDTO)
                .orElseThrow(() -> new UbicacionNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UbicacionResponseDTO> obtenerTodas() {
        log.info("Obteniendo todas las ubicaciones");

        return ubicacionRepository.findAll()
                .stream()
                .map(ubicacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
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

        Ciudad ciudad = ciudadRepository.findByNombreIgnoreCase(dto.getCiudad())
                .orElseGet(() -> ciudadRepository.save(
                        Ciudad.builder()
                                .nombre(dto.getCiudad())
                                .build()
                ));

        String nombreColonia = resolverNombreColonia(dto, ciudad);

        Colonia colonia = coloniaRepository.findByNombreIgnoreCaseAndCiudad(nombreColonia, ciudad)
                .orElseGet(() -> coloniaRepository.save(
                        Colonia.builder()
                                .nombre(nombreColonia)
                                .codigoPostal(obtenerCodigoPostalValido(dto.getCodigoPostal()))
                                .ciudad(ciudad)
                                .build()
                ));

        existente.setLatitud(dto.getLatitud());
        existente.setLongitud(dto.getLongitud());
        existente.setDireccion(dto.getDireccion());
        existente.setColonia(colonia);

        return ubicacionMapper.toDTO(ubicacionRepository.save(existente));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando ubicación con id: {}", id);

        if (!ubicacionRepository.existsById(id)) {
            throw new UbicacionNotFoundException(id);
        }

        ubicacionRepository.deleteById(id);
    }

    @Override
    public String pruebaUsuario(Long id) {
        try {
            UsuarioAuthDto usuario = usuarioClient.obtenerUsuario(id);

            return "El nombre de este usuario es: " + usuario.nombre()
                    + " con el correo: " + usuario.email();

        } catch (Exception e) {
            log.error("❌ Error de red al intentar obtener el nombre: {}", e.getMessage());
            return "Usuario Desconocido";
        }
    }

    private String resolverNombreColonia(UbicacionRequestDTO dto, Ciudad ciudad) {
        String coloniaRecibida = limpiarTexto(dto.getColonia());
        String direccion = limpiarTexto(dto.getDireccion());
        String codigoPostal = limpiarTexto(dto.getCodigoPostal());

        if (!esColoniaInvalida(coloniaRecibida)) {
            log.info("Colonia recibida válida desde frontend: {}", coloniaRecibida);
            return coloniaRecibida;
        }

        Optional<Colonia> coloniaPorCodigoPostal = buscarColoniaPorCodigoPostal(codigoPostal, direccion, ciudad);

        if (coloniaPorCodigoPostal.isPresent()) {
            String nombre = coloniaPorCodigoPostal.get().getNombre();
            log.info("Colonia encontrada por código postal {}: {}", codigoPostal, nombre);
            return nombre;
        }

        Optional<Colonia> coloniaPorDireccion = buscarColoniaEnDireccion(direccion, ciudad);

        if (coloniaPorDireccion.isPresent()) {
            String nombre = coloniaPorDireccion.get().getNombre();
            log.info("Colonia encontrada en BD por dirección: {}", nombre);
            return nombre;
        }

        log.warn("No se pudo determinar colonia para la dirección: {}", dto.getDireccion());
        return "No determinada";
    }

    private Optional<Colonia> buscarColoniaPorCodigoPostal(String codigoPostal, String direccion, Ciudad ciudad) {
        if (codigoPostal == null || codigoPostal.isBlank()
                || codigoPostal.equalsIgnoreCase("No disponible")) {
            return Optional.empty();
        }

        List<Colonia> colonias = coloniaRepository.findByCodigoPostalAndCiudad(codigoPostal, ciudad);

        if (colonias.isEmpty()) {
            log.warn("No hay colonias registradas con CP {} para la ciudad {}", codigoPostal, ciudad.getNombre());
            return Optional.empty();
        }

        if (colonias.size() == 1) {
            return Optional.of(colonias.get(0));
        }

        if (direccion != null && !direccion.isBlank()) {
            Optional<Colonia> coincidenciaPorDireccion = colonias.stream()
                    .filter(colonia -> direccion.toLowerCase()
                            .contains(colonia.getNombre().toLowerCase()))
                    .findFirst();

            if (coincidenciaPorDireccion.isPresent()) {
                return coincidenciaPorDireccion;
            }
        }

        log.warn("Hay varias colonias con CP {}, se usará la primera encontrada: {}", 
                codigoPostal, colonias.get(0).getNombre());

        return Optional.of(colonias.get(0));
    }

    private Optional<Colonia> buscarColoniaEnDireccion(String direccion, Ciudad ciudad) {
        if (direccion == null || direccion.isBlank()) {
            return Optional.empty();
        }

        return coloniaRepository.findAll()
                .stream()
                .filter(colonia -> colonia.getCiudad() != null)
                .filter(colonia -> colonia.getCiudad().getId().equals(ciudad.getId()))
                .filter(colonia -> direccion.toLowerCase().contains(
                        colonia.getNombre().toLowerCase()
                ))
                .findFirst();
    }

    private boolean esColoniaInvalida(String colonia) {
        if (colonia == null || colonia.isBlank()) {
            return true;
        }

        String valor = colonia.toLowerCase();

        return valor.equals("no disponible")
                || valor.equals("sin colonia")
                || valor.equals("no determinada")
                || valor.startsWith("calle ")
                || valor.startsWith("avenida ")
                || valor.startsWith("av. ")
                || valor.startsWith("boulevard ")
                || valor.startsWith("blvd ")
                || valor.startsWith("carretera ")
                || valor.contains("calle presa")
                || valor.contains("andador ")
                || valor.contains("privada ");
    }

    private String obtenerCodigoPostalValido(String codigoPostal) {
        String valor = limpiarTexto(codigoPostal);

        if (valor == null || valor.isBlank()
                || valor.equalsIgnoreCase("No disponible")) {
            return null;
        }

        return valor;
    }

    private String limpiarTexto(String texto) {
        if (texto == null) {
            return null;
        }

        return texto.trim();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<String> obtenerColoniasPorCodigoPostal(String codigoPostal) {
        log.info("Buscando colonias por código postal: {}", codigoPostal);

        return coloniaRepository.findByCodigoPostal(codigoPostal)
                .stream()
                .map(Colonia::getNombre)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}