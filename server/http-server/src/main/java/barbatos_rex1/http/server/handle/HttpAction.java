package barbatos_rex1.http.server.handle;

import barbatos_rex1.http.server.handle.request.Request;

import java.io.Serializable;

@FunctionalInterface
public interface HttpAction<E extends Serializable> {

    ResponseEntity<E> action(Request<E> request) throws Exception;
}
