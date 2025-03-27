package hr.serverinterceptor;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static hr.grpc.GreeterGrpc.GreeterImplBase;
import static hr.grpc.Proto.HelloRequest;
import static hr.grpc.Proto.HelloResponse;

@Service
public class GreeterService extends GreeterImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(GreeterService.class);

    @Override
    public void sayHelloUnary(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        LOG.info("RECEIVED: " + request);
        HelloResponse response = HelloResponse.newBuilder().setMessage("response").build();
        LOG.info("RESPONSE: " + response);
        if ("err".equals(request.getMessage())) {
            throw new RuntimeException("Test ex unary");
        }
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void sayHelloServerStream(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        LOG.info("RECEIVED: " + request);
        for (int i = 0; i < 3; i++) {
            HelloResponse response = HelloResponse.newBuilder().setMessage("response " + i).build();
            LOG.info("RESPONSE: " + response);
            responseObserver.onNext(response);
        }
        if ("err".equals(request.getMessage())) {
            throw new RuntimeException("Test ex server stream");
        }
        responseObserver.onCompleted();
    }
}
