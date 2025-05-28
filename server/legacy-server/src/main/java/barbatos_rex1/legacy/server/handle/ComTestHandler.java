package barbatos_rex1.legacy.server.handle;

import barbatos_rex1.legacy.server.exception.PacketException;
import barbatos_rex1.legacy.server.exception.ServerException;
import barbatos_rex1.legacy.server.protocol.Packet;
import barbatos_rex1.legacy.server.protocol.RcompProtocol;
import org.tinylog.Logger;

import java.util.Optional;

public class ComTestHandler implements Handler {


    @Override
    public boolean canAccept(Packet packet) {
        return packet.code() == RcompProtocol.RcompProtocolCode.COMTEST.byteCode();
    }

    @Override
    public Optional<Handler> next() {
        return Optional.empty();
    }

    @Override
    public void accept(Packet packet) throws ServerException {
        try {
            Logger.info("COMTEST from Client: " + packet.protocolObject(String.class));
        } catch (PacketException e) {
            throw new ServerException(e);
        }

    }
}
