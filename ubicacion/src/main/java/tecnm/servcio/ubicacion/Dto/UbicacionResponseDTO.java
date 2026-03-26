package tecnm.servcio.ubicacion.Dto;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UbicacionResponseDTO {
	private Long id;
    private Double latitud;
    private Double longitud;
    private String direccion;
    private String colonia;
    private String ciudad;
}
