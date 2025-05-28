package barbatos_rex1.http.server.handle;

import java.util.Map;

public interface Controller {

    String rootMapping();

    Map<HttpEntry, HttpAction<?>> actionMap();


}
