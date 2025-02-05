package com.capgemini.opapolicies.security;

import com.styra.opa.OPAClient;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;


public class OPAAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private static final Logger logger = LoggerFactory.getLogger(OPAAuthorizationManager.class);
    private final OPAClient opa;

    public OPAAuthorizationManager(OPAClient opa) {
        this.opa = opa;
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        Optional<OPAResponse> response = makeOpaRequest(authentication, context);
        return new AuthorizationDecision(response.map(OPAResponse::isAllow).orElse(false));
    }

    private Optional<OPAResponse> makeOpaRequest(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        Map<String, Object> input = createInput(authentication, context);
        try {
            return Optional.of(opa.evaluate("food_security", input, OPAResponse.class));
        } catch (Exception e) {
            logger.error("Exception from OPA client: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private Map<String, Object> createInput(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        HttpServletRequest request = context.getRequest();
        Map<String, Object> input = new HashMap<>();

        input.put("subject", createSubject(authentication.get()));
        input.put("path", request.getPathInfo());
        input.put("action", Map.of("name", request.getMethod(), "protocol", request.getProtocol()));
        input.put("context", createContext(request));

        return input;
    }

    private Map<String, Object> createSubject(Authentication authentication) {
        JwtAuthenticationToken jwt = (JwtAuthenticationToken) authentication;
        return Map.of(
                "id", jwt.getToken().getClaim("client_id"),
                "authorities", authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet())
        );
    }

    private Map<String, Object> createContext(HttpServletRequest request) {
        return Map.of(
                "type", "http",
                "host", request.getRemoteHost(),
                "ip", request.getRemoteAddr(),
                "port", request.getRemotePort()
        );
    }
}