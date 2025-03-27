package hr.serverinterceptor;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CorrelationId {

    private static final String CORRELATION_ID_KEY = "correlationId";

    public static void create() {
        String correlationId = UUID.randomUUID().toString().replace("-", "");
        MDC.put(CORRELATION_ID_KEY, correlationId);
    }

    public static String get() {
        return MDC.get(CORRELATION_ID_KEY);
    }

    public static void remove() {
        MDC.remove(CORRELATION_ID_KEY);
    }
}
