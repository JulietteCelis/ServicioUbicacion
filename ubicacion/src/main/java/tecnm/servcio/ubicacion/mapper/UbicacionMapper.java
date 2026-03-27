package tecnm.servcio.ubicacion.mapper;

import org.springframework.stereotype.Component;

import tecnm.servcio.ubicacion.Dto.UbicacionRequestDTO;
import tecnm.servcio.ubicacion.Dto.UbicacionResponseDTO;
import tecnm.servcio.ubicacion.Entity.Colonia;
import tecnm.servcio.ubicacion.Entity.Ubicacion;


@Component 
public class UbicacionMapper {
	
    public Ubicacion toEntity(UbicacionRequestDTO dto, Colonia colonia) {
        return Ubicacion.builder()
                .latitud(dto.getLatitud())
                .longitud(dto.getLongitud())
                .direccion(dto.getDireccion())
                .colonia(colonia) // Seteamos el objeto completo, no solo el String
                .build();
    }
	 
    public UbicacionResponseDTO toDTO(Ubicacion entity) {
        return UbicacionResponseDTO.builder()
                .id(entity.getId())
                .latitud(entity.getLatitud())
                .longitud(entity.getLongitud())
                .direccion(entity.getDireccion())
                .colonia(entity.getColonia().getNombre()) // Sacamos el nombre del objeto
                .ciudad(entity.getColonia().getCiudad().getNombre()) // Navegamos por la relación
                .build();
    }
}