package barbatos_rex1.http.server.handle;

import barbatos_rex1.http.server.handle.request.Request;
import barbatos_rex1.http.server.handle.request.RequestParameter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.AllArgsConstructor;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class Router {

    private List<Controller> controllers;


    private HttpServer httpServer;


    public HttpServer registerMethods() {
        Set<HttpEntry> registeredEntries = new HashSet<>();
        for (Controller controller : controllers) {
            Map<HttpEntry, HttpAction<?>> actionMap = controller.actionMap();
            for (HttpEntry httpEntry : actionMap.keySet()) {
                HttpEntry routedEntry = httpEntry.route(controller.rootMapping());
                if (registeredEntries.contains(routedEntry)) {
                    throw new RuntimeException("Something!");
                }
                registeredEntries.add(routedEntry);
                httpServer.createContext(routedEntry.getPath(), buildHandler(routedEntry.getMethod(), actionMap.get(httpEntry)));
            }
        }
        httpServer.createContext("/", defaultRoute());
        return httpServer;
    }

    private static HttpHandler defaultRoute() {
        return exchange -> {
            ResponseEntity<String> responseEntity = new ResponseEntity<>() {
                @Override
                public Class<String> entityClass() {
                    return String.class;
                }

                @Override
                public String entity() {
                    return "Hello World!";
                }

                @Override
                public int code() {
                    return 200;
                }
            };
            byte[] content = responseEntity.serialize();
            exchange.sendResponseHeaders(200, content.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(content);
            }
        };
    }

    private HttpHandler buildHandler(HttpMethod method, HttpAction<?> httpAction) {
        return (exchange -> {
            handle(exchange, method, httpAction);
        });
    }

    private <E extends Serializable> void handle(HttpExchange exchange, HttpMethod method, HttpAction<E> httpAction) {
        try (exchange) {
            Request<E> request = lisiten(exchange, method);
            ResponseEntity<E> response = httpAction.action(request);
            respond(exchange, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <E extends Serializable> Request<E> lisiten(HttpExchange exchange, HttpMethod method) throws IOException, ClassNotFoundException {
        Set<RequestParameter> parameters = fetchRequestParameter(exchange);
        ResponseEntity<E> object = fetchResponseEntity(exchange);
        Request<E> request = new Request(method, object.entityClass(), object.entity(), parameters);
        return request;
    }

    private static <E extends Serializable> void respond(HttpExchange exchange, ResponseEntity<E> response) throws IOException {
        byte[] content = response.serialize();
        exchange.sendResponseHeaders(response.code(), content.length);
        OutputStream os = exchange.getResponseBody();
        os.write(content);
    }

    private <E extends Serializable> ResponseEntity<E> fetchResponseEntity(HttpExchange exchange) throws IOException, ClassNotFoundException {
        InputStream is = exchange.getRequestBody();
        try (ObjectInputStream ois = new ObjectInputStream(is)) {
            return (ResponseEntity<E>) ois.readObject();
        }
    }

    private Set<RequestParameter> fetchRequestParameter(HttpExchange exchange) {
        String query = exchange.getRequestURI().getQuery();
        return Arrays.stream(query.split("&")).map(s -> {
            String[] tuple = s.split("=");
            return new RequestParameter(tuple[0], tuple[1]);
        }).collect(Collectors.toSet());
    }

}
