package server;

import java.io.*;
import java.net.Socket;

/**
 * @author Markus Reichl
 * @version 2017-03-06
 */

class ClientHandler extends Thread {
    private Socket socket;
    private String statement;
    private File tmpFile;

    ClientHandler(Socket socket) {
        super();
        this.socket = socket;

        try {
            this.tmpFile = File.createTempFile(socket.getLocalAddress() + "-", ".csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            while (true) {
                String input, output;

                System.out.println("Waiting for requests..");
                input = in.readLine();

                if (input == null) {
                    System.out.println("Empty request, Client disconnected!");
                    return;
                }

                if (input.equals("COMMIT")) {
                    commit();
                    continue;
                }

                if (input.equals("ABORT")) {
                    abort();
                    continue;
                }

                if (prepare(input)) {
                    System.out.println("Succeeded on: " + input);
                    output = "READY";
                } else {
                    System.out.println("Failed on: " + input);
                    output = "FAILED";
                }

                out.println(output);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void abort() {
        statement = "";
        try {
            System.out.println(tmpFile);
            // tmpFile.deleteOnExit();
            FileWriter fileWriter = new FileWriter(tmpFile);
            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write("Commit failed on: " + statement);

            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // rollback();
    }

    private void rollback() {
        System.out.println("Rollback succeeded on: " + statement);
    }

    private void commit() {
        try {
            System.out.println(tmpFile);
            // tmpFile.deleteOnExit();
            FileWriter fileWriter = new FileWriter(tmpFile);
            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write("Commit succeeded on: " + statement);

            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean prepare(String input) {
        if (input.contains(";")) return false;
        statement = input;
        return true;
    }
}
