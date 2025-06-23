package truman.android.example.httpparser.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import truman.android.example.httpparser.ui.Ui;

public class HttpServer {

    private static final String TAG = HttpServer.class.getSimpleName() + ".2ruman";

    private ServerSocket serverSocket;
    private HttpServerCallback httpServerCallback;

    private final int port;

    public HttpServer(int port) {
        this.port = port;
    }

    public void create() {
        try {
            serverSocket = new ServerSocket(port);
            Ui.println("Server created");

            while (true) {
                Ui.println("Start waiting...");
                Socket clientSocket = serverSocket.accept();

                Ui.println("Client connected..." + clientSocket.getInetAddress());
                HttpConnection.create(clientSocket).setServerCallback(httpServerCallback).listen();
            }
        } catch (IOException e) {
            if ("Socket closed".equals(e.getMessage())) {
                Ui.println("Server closed");
            } else {
                Ui.println("Server error");
            }
        }
    }

    public void setHttpServerCallback(HttpServerCallback httpServerCallback) {
        this.httpServerCallback = httpServerCallback;
    }

    public void terminate() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ignored) {}
        }
    }
}
