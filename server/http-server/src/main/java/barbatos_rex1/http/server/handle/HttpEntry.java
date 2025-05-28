package barbatos_rex1.http.server.handle;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class HttpEntry {

    @EqualsAndHashCode.Include
    private final String routeFromRoot;
    @EqualsAndHashCode.Include
    @Getter
    private final HttpMethod method;

    public HttpEntry route(String root) {
        if (root.endsWith("/")) {
            return new HttpEntry(root + this.routeFromRoot, method);
        }
        return new HttpEntry(root + "/" + this.routeFromRoot, method);
    }

    public String getPath() {
        return routeFromRoot;
    }
}
