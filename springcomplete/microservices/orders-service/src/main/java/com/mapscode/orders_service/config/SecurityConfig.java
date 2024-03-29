package com.mapscode.orders_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                // Define that the whole requests needs to be authenticated
                .authorizeHttpRequests(auth -> auth
                        .anyRequest()
                        .authenticated())
                // Personalize the jwt token according what you need
                .oauth2ResourceServer(configure -> configure.jwt(
                        jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthConverter())));
        return httpSecurity.build();

    }

    private Converter<Jwt,? extends AbstractAuthenticationToken> jwtAuthConverter() {
        // Convert the roles of keycloak in something that spring security can understand
        // Keycloak roles can be mapped to the Spring security roles
        // In this case is the class GrantedAuthority
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());

        return converter;
    }
}

@SuppressWarnings("unchecked")
class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        if(jwt.getClaims() == null) {
            return List.of();
        }
        final Map<String, List<String>> realmAccess = (Map<String, List<String>>) jwt.getClaims().get("realm_access");
        return realmAccess.get("roles").stream()
                .map(rolName -> "ROLE_" + rolName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
