package ru.pivovarov;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Client {
    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final BufferedReader inputUser;
    private final String nickname;
    private static final String settings = "settings.txt";

    public Client(String ip, int port) throws IOException {

        this.socket = new Socket(ip, port);

        inputUser = new BufferedReader(new InputStreamReader(System.in));
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        this.nickname = chooseNickname();

        new Thread(() -> {
            try {
                while (true) {
                    final String str = in.readLine();
                    if (str.equals("/exit")) {
                        closeSocket();
                    }
                    System.out.println(str);
                }
            } catch (IOException ignored) {
            }
        }).start();

        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    final String userWord = inputUser.readLine();
                    if (userWord.equals("/exit")) {
                        out.write("/exit" + "\n");
                        Thread.currentThread().interrupt();
                        closeSocket();
                    } else {
                        String localDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
                        out.write("(" + localDateTime + ")" + " " + nickname + ": " + userWord + "\n");
                    }
                    out.flush();
                } catch (IOException ignored) {
                }
            }
        }).start();
    }

    private String chooseNickname() throws IOException {
        System.out.print("Choose your nickname: ");
        String nickname = inputUser.readLine();
        out.write("Hello " + nickname + "\r\n");
        out.flush();

        return nickname;
    }

    private void closeSocket() throws IOException {
        if (!socket.isClosed()) {
            socket.close();
            in.close();
            out.close();
        }
    }

    public static int returnPort() throws FileNotFoundException {
        Scanner file = new Scanner(new File(settings));
        String[] split = file.nextLine().split(" ");
        return Integer.parseInt(split[1]);
    }

    public static String returnIp() throws FileNotFoundException {
        Scanner file = new Scanner(new File(settings));
        file.nextLine();
        String[] split = file.nextLine().split(" ");
        return split[1];
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client(returnIp(), returnPort());
    }
}
