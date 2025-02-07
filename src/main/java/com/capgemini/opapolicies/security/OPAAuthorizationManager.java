package com.capgemini.opapolicies.security;

import com.styra.opa.OPAClient;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class OPAAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private static final Logger logger = LoggerFactory.getLogger(OPAAuthorizationManager.class);
    private final OPAClient opa;
    private final String[] policyPaths;

    public OPAAuthorizationManager(OPAClient opa, String... policyPaths) {
        this.opa = opa;
        this.policyPaths = policyPaths;
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        boolean decision = Arrays.stream(policyPaths)
                .map(policyPath -> makeOpaRequest(authentication, context, policyPath))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .anyMatch(OPAResponse::isAllow);
        return new AuthorizationDecision(decision);
    }

    private Optional<OPAResponse> makeOpaRequest(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context, String policyPath) {
        Authentication authentication = authenticationSupplier.get();
        if (!isJwtAuthentication(authentication)) {
            return Optional.empty();
        }

        Map<String, Object> input = createInput(authentication, context);
        try {
            return Optional.of(opa.evaluate(policyPath, input, OPAResponse.class));
        } catch (Exception e) {
            logger.error("Exception from OPA client: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private static boolean isJwtAuthentication(Authentication authentication) {
        return authentication instanceof JwtAuthenticationToken;
    }

    private Map<String, Object> createInput(Authentication authentication, RequestAuthorizationContext context) {
        HttpServletRequest request = context.getRequest();
        Map<String, Object> input = new HashMap<>();

        input.put("subject", createSubject(authentication));
        input.put("path", getPath(request));
        input.put("action", Map.of("name", request.getMethod(), "protocol", request.getProtocol()));
        input.put("context", createContext(request));

        return input;
    }

    private static String getPath(HttpServletRequest request) {
        return StringUtils.isEmpty(request.getServletPath()) ? request.getPathInfo() : request.getServletPath();
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