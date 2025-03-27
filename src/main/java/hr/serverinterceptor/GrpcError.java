package hr.serverinterceptor;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GrpcError {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcError.class);

    public <RespT, ReqT> void handleException(Exception e, ServerCall<ReqT, RespT> serverCall, Metadata metadata) {
        Status status = switch (e) {
            case SecurityException ignored -> Status.PERMISSION_DENIED;
            case RuntimeException ignored -> Status.ABORTED;
            default -> Status.INTERNAL;
        };
        buildResponseAndClose(serverCall, metadata, status, e.getMessage());
    }

    private <RespT, ReqT> void buildResponseAndClose(ServerCall<ReqT, RespT> serverCall,
                                                     Metadata metadata,
                                                     Status status,
                                                     String message) {

        String extraMessage = " - CorrelationId: " + CorrelationId.get();
        Status statusWithDescription = status.withDescription(message).augmentDescription(extraMessage);
        LOG.info("Sending error response to client: {}", statusWithDescription);
        if (serverCall.isReady()) {
            serverCall.close(statusWithDescription, metadata);
            LOG.info("Sent");
        } else {
            LOG.info("Grpc call already closed");
        }
        CorrelationId.remove();
    }
}
