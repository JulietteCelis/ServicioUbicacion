package tecnm.servcio.ubicacion.Exceptions;

public class UbicacionNotFoundException extends RuntimeException {
	public UbicacionNotFoundException(Long id) {
        super("Ubicación con id " + id + " no encontrada.");
    }

}
