package com.capgemini.opapolicies.security;

import com.styra.opa.OPAClient;
import com.styra.opa.openapi.OpaApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class OPAConfiguration {

    private final String opaUrl;
    private final String[] policyPaths;

    public OPAConfiguration(@Value("${opa.url}") String opaUrl,
                            @Value("${opa.policies}") String[] policyPaths) {
        this.opaUrl = opaUrl;
        this.policyPaths = policyPaths;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakJwtRolesConverter());

        http.authorizeHttpRequests(authorize -> authorize.anyRequest().access(new OPAAuthorizationManager(opaClient(), policyPaths)))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter)));

        return http.build();
    }

    private OPAClient opaClient() {
        return new OPAClient(OpaApiClient.builder().serverURL(opaUrl).build());
    }
}