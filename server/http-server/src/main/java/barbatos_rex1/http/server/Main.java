package barbatos_rex1.http.server;

import barbatos_rex1.http.server.handle.Router;
import com.sun.net.httpserver.HttpServer;
import org.tinylog.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        Router router = new Router(List.of(), httpServer);
        httpServer = router.registerMethods();
        Logger.info("Starting Server....");
        httpServer.start();
    }
}
