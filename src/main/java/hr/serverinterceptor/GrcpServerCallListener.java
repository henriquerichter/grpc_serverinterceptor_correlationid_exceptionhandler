package hr.serverinterceptor;

import io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrcpServerCallListener<ReqT, RespT> extends SimpleForwardingServerCallListener<ReqT> {

    private static final Logger LOG = LoggerFactory.getLogger(GrcpServerCallListener.class);

    private final GrpcError grpcError;

    private final ServerCall<ReqT, RespT> serverCall;

    private final Metadata metadata;

    GrcpServerCallListener(GrpcError grpcError, ServerCall.Listener<ReqT> listener, ServerCall<ReqT, RespT> serverCall, Metadata metadata) {
        super(listener);
        this.grpcError = grpcError;
        this.serverCall = serverCall;
        this.metadata = metadata;
    }

    @Override
    public void onMessage(ReqT message) {
        safelyExecute(() -> super.onMessage(message), "onMessage");
    }

    @Override
    public void onHalfClose() {
        safelyExecute(super::onHalfClose, "onHalfClose");
    }

    @Override
    public void onCancel() {
        safelyExecute(super::onCancel, "onCancel");
        CorrelationId.remove();
    }

    @Override
    public void onComplete() {
        safelyExecute(super::onComplete, "onComplete");
        CorrelationId.remove();
    }

    @Override
    public void onReady() {
        safelyExecute(super::onReady, "onReady");
    }

    private void safelyExecute(Runnable task, String methodName) {
        try {
            LOG.info("{}()", methodName);
            task.run();
        } catch (Exception e) {
            LOG.error("{}()", methodName, e);
            this.grpcError.handleException(e, serverCall, metadata);
        }
    }
}
