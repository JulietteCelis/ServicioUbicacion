package tecnm.servcio.ubicacion.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UbicacionRequestDTO {
	@NotNull(message = "La latitud es obligatoria")
    private Double latitud;

    @NotNull(message = "La longitud es obligatoria")
    private Double longitud;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @NotBlank(message = "La colonia es obligatoria")
    private String colonia;

    @NotBlank(message = "La ciudad es obligatoria")
    private String ciudad;
}
