package hr.serverinterceptor;

import org.springframework.stereotype.Component;

@Component
public class AuthorizationService {

    private static final String BEARER_PREFIX = "Bearer ";

    public void checkValidTokenOrThrow(String token) throws Exception {
        if (token == null || !token.startsWith(BEARER_PREFIX)) {
            throw new Exception("Token must be in the format: Bearer ...");
        }
        // simulate login
        if (!"Bearer authOk".equals(token)) {
            throw new Exception("NOK");
        }
    }
}
