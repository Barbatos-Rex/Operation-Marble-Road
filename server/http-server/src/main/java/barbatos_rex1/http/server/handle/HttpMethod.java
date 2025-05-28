package barbatos_rex1.http.server.handle;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    PATCH,
    DELETE,
    HEAD,
    OPTIONS,
    CONNECT,
    TRACE;


    static HttpMethod castCode(String code) {
        return switch (code) {
            case "GET" -> GET;
            case "POST" -> POST;
            case "PUT" -> PUT;
            case "PATCH" -> PATCH;
            case "DELETE" -> DELETE;
            case "HEAD" -> HEAD;
            case "OPTIONS" -> OPTIONS;
            case "CONNECT" -> CONNECT;
            case "TRACE" -> TRACE;
            default -> throw new RuntimeException("Invalid HTTP Method!");
        };
    }
}
