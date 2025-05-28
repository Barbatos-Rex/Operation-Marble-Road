package barbatos_rex1.legacy.server.handle;

import barbatos_rex1.legacy.server.exception.ServerException;
import barbatos_rex1.legacy.server.protocol.Packet;
import lombok.AllArgsConstructor;

import java.util.Optional;

public interface Handler {


    boolean canAccept(Packet packet);

    Optional<Handler> next();

    void accept(Packet packet) throws ServerException;

    default void cascade(Packet packet) throws ServerException {
        if (canAccept(packet)) {
            accept(packet);
        }
        Optional<Handler> next = next();
        if (next.isPresent()) {
            next.get().cascade(packet);
        }
    }

    static Handler chain(Handler h1, Handler h2) {
        return new WrapperHandler(h1, h2);
    }

    @AllArgsConstructor
    class WrapperHandler implements Handler {
        private Handler handler;
        private Handler next;

        @Override
        public boolean canAccept(Packet packet) {
            return handler.canAccept(packet);
        }

        @Override
        public Optional<Handler> next() {
            return Optional.ofNullable(next);
        }

        @Override
        public void accept(Packet packet) throws ServerException {
            handler.accept(packet);
        }
    }

}
