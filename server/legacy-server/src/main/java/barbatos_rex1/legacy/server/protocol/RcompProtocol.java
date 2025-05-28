package barbatos_rex1.legacy.server.protocol;

import barbatos_rex1.legacy.server.exception.ProtocolException;
import barbatos_rex1.legacy.server.exception.ReceiveException;
import barbatos_rex1.legacy.server.exception.SendException;

public interface RcompProtocol extends AutoCloseable {

    void disconnect() throws ProtocolException;

    enum RcompProtocolCode {
        HNDSHK,
        ACK,
        DISCONN,
        COMTEST,
        ERROR;

        public byte byteCode() {
            if (ordinal() > 127) {
                return ((byte) -ordinal());
            }
            return ((byte) ordinal());
        }
    }

    void send(Packet packet) throws SendException;

    Packet listen() throws ReceiveException;

    void handshake() throws ProtocolException;


}
