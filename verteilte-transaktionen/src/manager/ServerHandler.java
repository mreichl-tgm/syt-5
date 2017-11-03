package manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author Markus Reichl
 * @version 2017-03-06
 */

class ServerHandler {
    private final PrintWriter out;
    private final BufferedReader in;

    ServerHandler(String host, int port) throws IOException {
        Socket socket = new Socket(host, port);

        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    boolean prepare(String statement) {
        try {
            out.println(statement);

            String input = in.readLine();

            if (input == null) return false;
            if (input.equals("READY")) return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    void abort() {
        out.println("ABORT");
    }

    void commit() {
        out.println("COMMIT");
    }
}
