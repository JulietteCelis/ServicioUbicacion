package tecnm.servcio.ubicacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
	    "tecnm.servcio.ubicacion",              // 1. Que escanee su propio proyecto
	    "com.auth.client_sdk",        // 2. Que escanee tu SDK
	    "com.ayuntamiento.security_lib" // 3. Que escanee tu librería de seguridad
	})
public class UbicacionApplication {

	public static void main(String[] args) {
		SpringApplication.run(UbicacionApplication.class, args);
	}

}
