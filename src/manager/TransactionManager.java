package manager;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * @author Markus Reichl
 * @version 2017-03-06
 */


public class TransactionManager {
    private LinkedList<ServerHandler> servers;

    public TransactionManager() {
        Scanner s = new Scanner(System.in);
        servers = new LinkedList<>();

        while (true) {
            System.out.print(">> ");
            String input = s.nextLine();

            if (input.equals("/quit")) break;
            if (input.contains("/connect")) {
                connect(
                        input.substring(input.indexOf(" ") + 1, input.lastIndexOf(" ")),
                        Integer.parseInt(input.substring(input.lastIndexOf(" ") + 1))
                );
            } else if (prepare(input)) {
                commit();
            } else {
                abort();
            }
        }
    }

    private void connect(String host, int port) {
        try {
            servers.add(new ServerHandler(host, port));
            System.out.println("Connected!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean prepare(String statement) {
        int fails = 0;

        for (ServerHandler server : servers) {
            if (!server.prepare(statement)) {
                fails++;
            }
        }

        System.out.println(fails + " fails");

        return fails < 1;
    }

    private void abort() {
        for (ServerHandler server : servers) {
            server.abort();
        }
    }

    private void commit() {
        for (ServerHandler server : servers) {
            server.commit();
        }
    }

}
