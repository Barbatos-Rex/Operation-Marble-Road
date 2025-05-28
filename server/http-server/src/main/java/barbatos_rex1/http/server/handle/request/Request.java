package barbatos_rex1.http.server.handle.request;

import barbatos_rex1.http.server.handle.HttpMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.Set;

@AllArgsConstructor
@Getter
public class Request<E extends Serializable> {
    private final HttpMethod method;
    private final Class<E> tClass;
    private final E entity;
    private final Set<RequestParameter> parameters;
}
