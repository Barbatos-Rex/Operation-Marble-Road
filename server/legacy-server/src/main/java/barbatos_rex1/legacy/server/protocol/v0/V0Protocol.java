package barbatos_rex1.legacy.server.protocol.v0;

import barbatos_rex1.legacy.server.exception.ProtocolException;
import barbatos_rex1.legacy.server.exception.ReceiveException;
import barbatos_rex1.legacy.server.exception.SendException;
import barbatos_rex1.legacy.server.protocol.Packet;
import barbatos_rex1.legacy.server.protocol.RcompProtocol;
import barbatos_rex1.legacy.server.utils.ExceptionalConsumer;
import org.tinylog.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class V0Protocol implements RcompProtocol {

    private final Socket socket;
    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;
    private boolean virtualDisconnect;

    public V0Protocol(Socket socket) throws ProtocolException {
        try {
            this.socket = socket;
            this.oos = new ObjectOutputStream(socket.getOutputStream());
            this.ois = new ObjectInputStream(socket.getInputStream());
            this.virtualDisconnect = false;
        } catch (IOException e) {
            throw new ProtocolException("Could not establish protocol connection!", e);
        }
    }

    private static final V0Packet ACK_PACKET = new V0Packet(RcompProtocolCode.ACK);
    private static final V0Packet HAND_SHAKE_PACKET = new V0Packet(RcompProtocolCode.HNDSHK);
    private static final V0Packet DISCONN_SHAKE_PACKET = new V0Packet(RcompProtocolCode.DISCONN);

    private void virtualDisconnect() {
        this.virtualDisconnect = true;
    }

    @Override
    public void disconnect() throws ProtocolException {
        try {
            sendPacket(DISCONN_SHAKE_PACKET);
            Packet packet = recievePacket();
            if (packet.code() == DISCONN_SHAKE_PACKET.code()) {
                return;
            }
            if (packet.code() == ACK_PACKET.code()) {
                Logger.warn("Possible miscommunication? Well it's the client's problem anyway ¯\\_(ツ)_/¯. Maybe different version?");
                return;
            }
            Logger.warn("Possible miscommunication? Well it's the client's problem anyway ¯\\_(ツ)_/¯.");
            virtualDisconnect();
        } catch (SendException | ReceiveException e) {
            throw new ProtocolException("Could not perform the virtual disconnect!");
        }
    }

    @Override
    public void send(Packet packet) throws SendException {
        try {
            sendPacket(packet);
            Packet received = recievePacket();
            if (received.code() == ACK_PACKET.code()) {
                return;
            }

            if (received.code() == RcompProtocolCode.ERROR.byteCode()) {
                Exception e = received.protocolObject(Exception.class);
                throw new ProtocolException("Error from client", e);
            }

            throw new SendException("Received erroneous code! Maybe different protocol versions?");

        } catch (ProtocolException e) {
            throw new SendException("Could not send established packet!", e);
        }
    }

    private void sendACK() throws SendException {
        sendPacket(ACK_PACKET);
    }

    @Override
    public Packet listen() throws ReceiveException {
        try {
            Packet packet = recievePacket();
            sendACK();
            return packet;
        } catch (SendException e) {
            throw new ReceiveException("Did not receive ACK!", e);
        }
    }

    @Override
    public void handshake() throws ProtocolException {
        sendPacket(HAND_SHAKE_PACKET);
        Packet p = recievePacket();
        if (p.code() == RcompProtocolCode.HNDSHK.byteCode()) {
            return;
        }
        throw new ProtocolException("Did no perform handshake!");
    }


    private Packet recievePacket() throws ReceiveException {
        if (virtualDisconnect) throw new ReceiveException("Currently virtually disconnected!");
        try {
            V0Packet packet = ((V0Packet) ois.readObject());
            return packet;
        } catch (IOException e) {
            throw new ReceiveException("Could not receive packet!", e);
        } catch (ClassNotFoundException | ClassCastException e) {
            throw new ReceiveException("Invalid protocol object received! Probably different versions?", e);
        }
    }

    private void sendPacket(Packet packet) throws SendException {
        if (virtualDisconnect) throw new SendException("Currently virtually disconnected!");
        try {
            oos.writeObject(packet);
        } catch (IOException e) {
            throw new SendException("Could not send packet!", e);
        }
    }


    @Override
    public void close() throws ProtocolException {
        try {
            this.socket.close();
        } catch (IOException e) {
            throw new ProtocolException("Could not close established socket!");
        }
    }

    public static void safeHandling(Socket connection, ExceptionalConsumer<RcompProtocol, ProtocolException> handlingConsumer)
            throws ProtocolException {
        try (RcompProtocol protocol = new V0Protocol(connection)) {
            protocol.handshake();
            handlingConsumer.consume(protocol);
            protocol.disconnect();
        } catch (Exception e) {
            throw new ProtocolException("Cannot close protocol!", e);
        }
    }
}
