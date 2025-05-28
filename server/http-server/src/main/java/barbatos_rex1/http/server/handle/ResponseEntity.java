package barbatos_rex1.http.server.handle;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public interface ResponseEntity<E extends Serializable> extends Serializable {

    class ResponseEntityBuilder<E extends Serializable> {

        private record InternalResponseEntity<E extends Serializable>(E entity, int code,
                                                                      Class<E> entityClass) implements ResponseEntity<E> {
        }

        private int code;
        private final Class<E> tClass;

        public ResponseEntityBuilder(Class<E> tClass) {
            this.tClass = tClass;
            this.code = 200;
        }

        public ResponseEntityBuilder<E> status(int status) {
            this.code = status;
            return this;
        }

        public ResponseEntity<E> body(E entity) {
            return new InternalResponseEntity<>(entity, code, tClass);
        }

        public ResponseEntityBuilder<E> ok() {
            this.code = 200;
            return this;
        }

        public ResponseEntityBuilder<E> created() {
            this.code = 201;
            return this;
        }


        public ResponseEntity<E> noContent() {
            return new InternalResponseEntity<>(null, 204,tClass);
        }

        public ResponseEntity<E> notFound() {
            return new InternalResponseEntity<>(null, 404,tClass);
        }
    }

    Class<E> entityClass();

    E entity();

    int code();

    default byte[] serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try(ObjectOutputStream oos = new ObjectOutputStream(baos)){
            oos.writeObject(this);
            return baos.toByteArray();
        }
    }

}
