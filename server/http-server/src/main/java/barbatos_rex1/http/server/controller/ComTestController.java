package barbatos_rex1.http.server.controller;

import barbatos_rex1.http.server.handle.*;
import org.tinylog.Logger;

import java.util.Map;

public class ComTestController implements Controller {
    @Override
    public String rootMapping() {
        return "/api/com-test/";
    }

    @Override
    public Map<HttpEntry, HttpAction<?>> actionMap() {
        return Map.of(new HttpEntry("", HttpMethod.POST), request -> {
            String object = request.getEntity().toString();
            Logger.info("Communication Test: " + object);
            return ResponseEntity.builder(String.class).ok().body("ACK");
        });
    }
}
