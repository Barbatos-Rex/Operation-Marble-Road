package barbatos_rex1.legacy.server.protocol;

import barbatos_rex1.legacy.server.exception.PacketException;

public interface Packet {
    byte version();
    byte code();
    <T> T protocolObject(Class<T> tClass) throws PacketException;
    byte[] getRawObject();
}
