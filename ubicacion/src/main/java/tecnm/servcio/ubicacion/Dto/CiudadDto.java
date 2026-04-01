package tecnm.servcio.ubicacion.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CiudadDto {

    private Long id;
    private String nombre;
}