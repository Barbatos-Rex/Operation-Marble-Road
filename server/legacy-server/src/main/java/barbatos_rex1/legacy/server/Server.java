package barbatos_rex1.legacy.server;

import barbatos_rex1.legacy.server.exception.ProtocolException;
import barbatos_rex1.legacy.server.exception.ServerException;
import barbatos_rex1.legacy.server.handle.Handler;
import barbatos_rex1.legacy.server.protocol.Packet;
import barbatos_rex1.legacy.server.protocol.RcompProtocol;
import barbatos_rex1.legacy.server.protocol.v0.V0Protocol;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.tinylog.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Server implements Runnable {

    public final static class Factory {
        private final ThreadGroup threadGroup;

        public Factory() {
            this.threadGroup = new ThreadGroup("RCOMP-Server-Group");
        }


        public Server create(int port) throws ServerException {
            return new Server(port, newId(), threadGroup);
        }

        private UUID newId() {
            return UUID.randomUUID();
        }


    }

    public static Factory factory() {
        return new Factory();
    }


    private final ServerSocket socket;
    private boolean running;
    private final ThreadGroup threadGroup;
    private Thread serverThread;
    private Handler handler;


    @Getter
    @EqualsAndHashCode.Include
    private UUID serverId;


    private Server(int port, UUID id, ThreadGroup group) throws ServerException {
        running = false;
        this.serverId = id;
        threadGroup = group;
        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            throw new ServerException("Could not open server!", e);
        }
    }

    public Server chainHandler(Handler handler) {
        if (this.handler == null) {
            this.handler = handler;
            return this;
        }
        this.handler = Handler.chain(this.handler, handler);
        return this;
    }


    public void start() {
        Logger.info("Starting server " + name() + "...");
        running = true;
        open();
    }

    public String name() {
        return "Server-" + serverId;
    }

    public void stop() throws InterruptedException {
        Logger.info("Stopping server {}", name());
        this.running = false;
        this.serverThread.interrupt();
        this.serverThread.join();
    }

    private void open() {
        this.serverThread = new Thread(threadGroup, this, name());
        this.serverThread.setDaemon(true);
        this.serverThread.start();
    }

    private void listen() {
        while (running) {
            try {
                Socket connection = socket.accept();
                Logger.info("Received Connection from " + connection.getInetAddress());
                V0Protocol.safeHandling(connection, this::handle);
            } catch (IOException | ProtocolException e) {
                Logger.error("Error attending connection!");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        listen();
    }

    private void handle(RcompProtocol protocol) throws ProtocolException {
        try {
            Packet p = protocol.listen();
            handler.cascade(p);
        } catch (ServerException e) {
            throw new ProtocolException("Error while executing protocol!", e);
        }
    }
}
