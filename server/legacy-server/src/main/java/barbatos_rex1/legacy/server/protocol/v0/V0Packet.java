package barbatos_rex1.legacy.server.protocol.v0;

import barbatos_rex1.legacy.server.exception.PacketException;
import barbatos_rex1.legacy.server.protocol.Packet;
import barbatos_rex1.legacy.server.protocol.RcompProtocol.RcompProtocolCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.*;

public class V0Packet implements Packet, Serializable {


    private final RcompProtocolCode code;
    private final byte[] byteArray;

    public V0Packet(RcompProtocolCode code, Serializable object) throws PacketException {
        this.code = code;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(object);
            byteArray = baos.toByteArray();
        } catch (IOException e) {
            throw new PacketException("Cannot serialize packet!");
        }
    }

    V0Packet(RcompProtocolCode code) {
        this.code = code;
        this.byteArray = new byte[0];
    }

    @Override
    public byte version() {
        return 0;
    }

    @Override
    public byte code() {
        return code.byteCode();
    }

    @Override
    public <T> T protocolObject(Class<T> tClass) throws PacketException {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(cloneArray()))) {
            return tClass.cast(ois.readObject());
        } catch (IOException e) {
            throw new PacketException("Something went wrong reading object!", e);
        } catch (ClassNotFoundException e) {
            throw new PacketException("System does not recognize object internally! Maybe missing dependencies?", e);
        } catch (ClassCastException e) {
            throw new PacketException("Object in packet is not of correct type! Maybe mismatch protocol codes?", e);
        }
    }

    @Override
    public byte[] getRawObject() {
        return cloneArray();
    }

    private byte[] cloneArray() {
        byte[] result = new byte[byteArray.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = byteArray[i];
        }
        return result;
    }

    public static Builder builder() {
        return new Builder();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Builder {

        private Object object;
        private RcompProtocolCode code;

        public Builder with(RcompProtocolCode code) {
            this.code = code;
            return this;
        }

        public <T> Builder with(T object) {
            this.object = object;
            return this;
        }

        public V0Packet build() throws PacketException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(this.object);
                return new V0Packet(code, baos.toByteArray());
            } catch (IOException e) {
                throw new PacketException("Cannot build packet!", e);
            }
        }

    }
}
