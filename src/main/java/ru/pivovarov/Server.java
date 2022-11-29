package ru.pivovarov;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

public class Server {
    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;
    private static final LinkedList<Server> serverList = new LinkedList<>();
    private static final String chatHistorySaving = "ChatHistory.txt";
    private static final String settings = "settings.txt";

    public Server(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        FileWriter fileWriter = new FileWriter(chatHistorySaving, true);

        new Thread(() -> {
            try {
                final String word = in.readLine();
                out.write(word + "\r\n");
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                while (true) {
                    final String word = in.readLine();
                    if (word.equals("/stop")) {
                        this.downService();
                        break;
                    }
                    System.out.println(word);
                    fileWriter.write(word + "\n");
                    fileWriter.flush();
                    for (Server server : Server.serverList) {
                        server.send(word);
                    }
                }
            } catch (NullPointerException | IOException ignored) {
            }
        }).start();
    }

    private void send(String msg) throws IOException {
        out.write(msg + "\n");
        out.flush();
    }

    private void downService() throws IOException {
        if (!socket.isClosed()) {
            socket.close();
            in.close();
            out.close();
            for (Server server : Server.serverList) {
                if (server.equals(this)) {
                    Server.serverList.remove(this);
                }
            }
        }
    }

    public static int returnPort() throws FileNotFoundException {
        Scanner file = new Scanner(new File(settings));
        String[] split = file.nextLine().split(" ");
        return Integer.parseInt(split[1]);
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Server stared");
        int PORT = returnPort();
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                serverList.add(new Server(clientSocket));
            }
        }
    }

}
