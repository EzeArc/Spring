package spring.ecosystem.rest_api_template.configSecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import spring.ecosystem.rest_api_template.configSecurity.filter.JwtAutheticateFilter;
import spring.ecosystem.rest_api_template.enums.Role;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class httpSecurityConfig {
    @Autowired
    private AuthenticationProvider daoAuthenticationProvider;

    @Autowired
    private JwtAutheticateFilter jwtAutheticateFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Configuración de OAuth2
        // Configuración de JWT
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sessionMagConfig -> sessionMagConfig
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(daoAuthenticationProvider)
                .addFilterBefore(jwtAutheticateFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authRequestConfig -> {
                    // Rutas privadas ADMIN
                    authRequestConfig.requestMatchers(HttpMethod.GET, "/adminController/listaServiciosAdmin")
                            .hasRole(Role.ADMIN.name());
                    authRequestConfig.requestMatchers(HttpMethod.GET, "/adminController/listaTurnoAdmin")
                            .hasRole(Role.ADMIN.name());
                    authRequestConfig.requestMatchers(HttpMethod.GET, "/api/v1/user").hasRole(Rol.ADMIN.name());
                    authRequestConfig.requestMatchers(HttpMethod.POST, "/adminController/crearServicio")
                            .hasRole(Role.ADMIN.name());
                    authRequestConfig.requestMatchers(HttpMethod.PUT, "/adminController/AltaBajaServicio/{id}")
                            .hasRole(Role.ADMIN.name());
                    authRequestConfig.requestMatchers(HttpMethod.PUT, "/adminController/AltaBaja/{id}")
                            .hasRole(Role.ADMIN.name());
                    authRequestConfig.requestMatchers(HttpMethod.PUT, "/adminController/ModificarServicio")
                            .hasRole(Role.ADMIN.name());
                    // aca es el endpoint para cuando el admin quiere cancerlar el turno especifico de un dia
                    authRequestConfig.requestMatchers(HttpMethod.PUT, "/adminController/suspendeTurnoAdmin/{turnoId}")
                            .permitAll();
                    // endpot para que el admin agregue el feriado a dia en especifico
                    authRequestConfig.requestMatchers(HttpMethod.POST, "/adminController/agregarFeriadoAdmin")
                            .permitAll();
                    authRequestConfig.requestMatchers(HttpMethod.GET, "/adminController/listaTurnoDelMesAdmin/{date}")
                            .permitAll();
                    authRequestConfig.requestMatchers(HttpMethod.GET, "/adminController/filtrarTurnoPor")
                            .permitAll();


                    // Rutas públicas
                    authRequestConfig.requestMatchers(HttpMethod.GET, "/api/v1/auth/is-token-valid").permitAll();
                    authRequestConfig.requestMatchers(HttpMethod.GET, "/api/v1/auth/validate").permitAll();
                    authRequestConfig.requestMatchers(HttpMethod.GET, "/api/v1/servicios").permitAll();
                    authRequestConfig.requestMatchers(HttpMethod.GET, "/portal/listaSerivicios").permitAll();
                    authRequestConfig.requestMatchers(HttpMethod.GET, "/portal/servicioPodo/{id}").permitAll();
                    // Faltaba agrega esta url para que no tie problemas de cors
                    // Esto deberia ser un PUT?
                    authRequestConfig.requestMatchers(HttpMethod.GET, "/Turnos/cancelarTurno/{id}")
                            .hasRole(Role.USER.name());
                    authRequestConfig.requestMatchers(HttpMethod.GET, "/Turnos/listaTurnos/{id}")
                            .hasRole(Role.USER.name());
                    //  authRequestConfig.requestMatchers(HttpMethod.GET, "/Turnos/turnoDelDia/{date}").permitAll();
                    // Faltaba agrega esta url para que no tie problemas de cors
                    authRequestConfig.requestMatchers(HttpMethod.POST, "/api/v1/auth/authenticate").permitAll();
                    authRequestConfig.requestMatchers(HttpMethod.POST, "/api/v1/auth/google").permitAll();
                    authRequestConfig.requestMatchers(HttpMethod.POST, "/api/v1/auth/send-email").permitAll();
                    authRequestConfig.requestMatchers(HttpMethod.POST, "/api/v1/register").permitAll();
                    // Faltaba agrega esta url para que no tie problemas de --> Chekear si solo se
                    // le debe permitir al usuario!
//                    authRequestConfig.requestMatchers(HttpMethod.POST,
//                            "/Turnos/reservarTurno/{turnoId}/{servicioId}/{usuarioid}").permitAll();
                    // authRequestConfig.requestMatchers("/oauth2/**").permitAll();
                    // authRequestConfig.requestMatchers("/login/oauth2/**").permitAll(); ;
                    authRequestConfig.anyRequest().authenticated();// Permitir acceso a todos los endpoints de OAuth2
                });

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("https://podogonnet.netlify.app",
                "https://podofrontdeploy.onrender.com", "http://localhost:5173", "https://accounts.google.com"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Set-Cookie"));
        // configuration.setExposedHeaders(Arrays.asList("Set-Cookie"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
