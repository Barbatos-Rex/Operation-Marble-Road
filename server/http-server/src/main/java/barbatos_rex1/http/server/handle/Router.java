package barbatos_rex1.http.server.handle;

import barbatos_rex1.http.server.handle.request.Request;
import barbatos_rex1.http.server.handle.request.RequestParameter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.AllArgsConstructor;
import org.tinylog.Logger;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class Router {

    private List<Controller> controllers;


    private HttpServer httpServer;


    public HttpServer registerMethods() {
        Logger.info("Registering Routes...");
        Set<HttpEntry> registeredEntries = new HashSet<>();
        for (Controller controller : controllers) {
            Logger.info(String.format("Starting Controller \"%s\"", controller.rootMapping()) );
            Map<HttpEntry, HttpAction<?>> actionMap = controller.actionMap();
            for (HttpEntry httpEntry : actionMap.keySet()) {
                HttpEntry routedEntry = httpEntry.route(controller.rootMapping());
                Logger.info(String.format("Registering Route %s on %s", httpEntry.getMethod(),routedEntry.getPath()));
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
            log(exchange);
            Logger.info("Defaulting Route Response is 403 Forbidden!");
            ResponseEntity<String> responseEntity = new ResponseEntity<>() {
                @Override
                public Class<String> entityClass() {
                    return String.class;
                }

                @Override
                public String entity() {
                    return "Forbidden!";
                }

                @Override
                public int code() {
                    return 403;
                }
            };
            byte[] content = responseEntity.serialize();
            exchange.sendResponseHeaders(403, content.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(content);
            }
        };
    }

    private HttpHandler buildHandler( HttpMethod method, HttpAction<?> httpAction) {
        return (exchange -> {
            log(exchange);
            handle(exchange, method, httpAction);
        });
    }

    private static void log(HttpExchange exchange) {
        Logger.info(String.format("Request %s to %s by %s", exchange.getRequestMethod(), exchange.getRequestURI(), exchange.getRemoteAddress()));
    }

    private <E extends Serializable> void handle(HttpExchange exchange, HttpMethod method, HttpAction<E> httpAction) {
        try {
            Request<E> request = listen(exchange, method);
            ResponseEntity<?> response = httpAction.action(request);
            respond(exchange, response);
        } catch (Exception e) {
            try {
                respond(exchange,ResponseEntity.builder(String.class).status(500).body(e.getMessage()));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    private <E extends Serializable> Request<E> listen(HttpExchange exchange, HttpMethod method) throws IOException, ClassNotFoundException {
        Set<RequestParameter> parameters = fetchRequestParameter(exchange);
        ResponseEntity<E> object = fetchResponseEntity(exchange);
        if (object == null) {
            Request<E> request = new Request(method, Serializable.class, null, parameters);
            return request;
        }
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
        byte[] content = is.readAllBytes();
        if (content.length == 0) return null;
        return (ResponseEntity<E>) new ObjectInputStream(new ByteArrayInputStream(content)).readObject();
    }

    private Set<RequestParameter> fetchRequestParameter(HttpExchange exchange) {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) {
            return Collections.emptySet();
        }
        return Arrays.stream(query.split("&")).map(s -> {
            String[] tuple = s.split("=");
            return new RequestParameter(tuple[0], tuple[1]);
        }).collect(Collectors.toSet());
    }

}
