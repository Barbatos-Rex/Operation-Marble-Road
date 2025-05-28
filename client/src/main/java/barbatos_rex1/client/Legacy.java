package barbatos_rex1.client;

import barbatos_rex1.legacy.server.exception.ProtocolException;
import barbatos_rex1.legacy.server.protocol.v0.V0Packet;
import barbatos_rex1.legacy.server.protocol.RcompProtocol;
import barbatos_rex1.legacy.server.protocol.v0.V0Protocol;

import java.io.IOException;
import java.net.Socket;

public class Legacy {

    public static void main(String[] args) throws IOException, ProtocolException {
        Socket socket = new Socket("localhost", 8080);
        V0Protocol.safeHandling(socket,Legacy::sendComTest);
    }

    private static void sendComTest( RcompProtocol protocol) throws ProtocolException{
        protocol.send(new V0Packet(RcompProtocol.RcompProtocolCode.COMTEST, "Hello World!"));
    }
}
