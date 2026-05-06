package tecnm.servcio.ubicacion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ayuntamiento.security_lib.jwt.JwtAuthenticationEntryPoint;
import com.ayuntamiento.security_lib.jwt.JwtAuthenticationFilter;

import feign.Request.HttpMethod;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter; 
    private final JwtAuthenticationEntryPoint entryPoint;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter, JwtAuthenticationEntryPoint entryPoint) {
        this.jwtFilter = jwtFilter;
        this.entryPoint = entryPoint;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .exceptionHandling(ex -> ex.authenticationEntryPoint(entryPoint))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                
                // =======================================================
                // 🟢 ZONA PÚBLICA (No piden Token JWT)
                // Agrega aquí todas las rutas que cualquiera puede ver.
                // =======================================================
            		.requestMatchers(
                            org.springframework.http.HttpMethod.OPTIONS, "/**" 
                        ).permitAll()
            		.requestMatchers(
            			    "/api/auth/login",
            			    "/api/auth/registro",
            			    "/api/ubicaciones/**",
            			    "/api/colonias/**"
            			).permitAll()
                // =======================================================
                // 🔴 ZONA PRIVADA (Exigen Token JWT válido obligatoriamente)
                // Cualquier ruta que NO esté en la lista de arriba, cae aquí.
                // =======================================================
                .anyRequest().authenticated() 
            );
            
        // El cadenero de tu librería entra en acción
          http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}