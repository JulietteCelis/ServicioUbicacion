package tecnm.servcio.ubicacion.mapper;

import org.springframework.stereotype.Component;

import tecnm.servcio.ubicacion.Dto.UbicacionRequestDTO;
import tecnm.servcio.ubicacion.Dto.UbicacionResponseDTO;
import tecnm.servcio.ubicacion.Entity.ubicacion;

@Component 

public class UbicacionMapper {
	
	 public ubicacion toEntity(UbicacionRequestDTO dto) {
	        return ubicacion.builder()
	                .latitud(dto.getLatitud())
	                .longitud(dto.getLongitud())
	                .direccion(dto.getDireccion())
	                .colonia(dto.getColonia())
	                .ciudad(dto.getCiudad())
	                .build();
	    }
	 
	    public UbicacionResponseDTO toDTO(ubicacion entity) {
	        return UbicacionResponseDTO.builder()
	                .id(entity.getId())
	                .latitud(entity.getLatitud())
	                .longitud(entity.getLongitud())
	                .direccion(entity.getDireccion())
	                .colonia(entity.getColonia())
	                .ciudad(entity.getCiudad())
	                .build();
	    }

}
