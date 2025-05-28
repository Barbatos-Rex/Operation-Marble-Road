package barbatos_rex1.legacy.server;

import barbatos_rex1.legacy.server.exception.ServerException;
import barbatos_rex1.legacy.server.handle.ComTestHandler;
import org.tinylog.Logger;

import java.util.Scanner;

public class Main {
    static boolean toStop = false;


    public static void main(String[] args) throws ServerException, InterruptedException {
        Server mainServer = Server.factory().create(8080);
        mainServer.chainHandler(new ComTestHandler());

        Scanner sc = new Scanner(System.in);
        System.out.println("RCOMP Server example.");
        System.out.println("At any time you can execute commands tot the server via terminal input!");
        System.out.println("Write the command and then press ENTER");
        System.out.println();
        System.out.println("Press ENTER to continue...");
        sc.nextLine();
        mainServer.start();

        while (!toStop) {
            String command = sc.nextLine();
            handleServerCommand(command, mainServer);
        }


    }

    private static void handleServerCommand(String command, Server server) throws InterruptedException {
        switch (command) {
            case "stop":
                server.stop();
                toStop = true;
                break;
            default:
                Logger.info(String.format("Unrecognized Command \"%s\"", command));
        }
    }

}
