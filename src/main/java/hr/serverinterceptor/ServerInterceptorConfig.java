package hr.serverinterceptor;

import io.grpc.ServerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.server.GlobalServerInterceptor;

@Configuration
public class ServerInterceptorConfig {

    @Bean
    @GlobalServerInterceptor
    public ServerInterceptor grpcServerInterceptor() {
        return new GrpcServerInterceptor();
    }
}
