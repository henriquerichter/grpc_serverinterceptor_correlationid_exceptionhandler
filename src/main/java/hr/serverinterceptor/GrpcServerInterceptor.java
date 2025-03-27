package hr.serverinterceptor;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;
import static io.grpc.Metadata.Key;

public class GrpcServerInterceptor implements ServerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcServerInterceptor.class);

    private static final Key<String> AUTHORIZATION_KEY = Key.of("authorization", ASCII_STRING_MARSHALLER);

    @Autowired
    private GrpcError grpcError;

    @Autowired
    private AuthorizationService authorizationService;

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> serverCall,
            Metadata metadata,
            ServerCallHandler<ReqT, RespT> serverCallHandler) {

        CorrelationId.create();
        try {
            String token = metadata.get(AUTHORIZATION_KEY);
            this.authorizationService.checkValidTokenOrThrow(token);
            LOG.info("AUTH OK");
            return createGrpcListener(serverCall, metadata, serverCallHandler);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            this.grpcError.handleException(e, serverCall, metadata);
            return createEmptyListener();
        }
    }

    private <ReqT, RespT> GrcpServerCallListener<ReqT, RespT> createGrpcListener(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        ServerCall.Listener<ReqT> listener = serverCallHandler.startCall(serverCall, metadata);
        return new GrcpServerCallListener<>(this.grpcError, listener, serverCall, metadata);
    }

    private <ReqT> ServerCall.Listener<ReqT> createEmptyListener() {
        return new ServerCall.Listener<>() {
        };
    }
}
