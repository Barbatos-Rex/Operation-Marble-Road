package barbatos_rex1.http.server.handle.request;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class RequestParameter {
    @EqualsAndHashCode.Include
    private final String key;
    private final String value;
}
