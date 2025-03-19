package truman.android.example.tls_echo.server;

import static java.lang.System.out;

import java.net.Socket;

import truman.android.example.tls_echo.common.Callback;
import truman.android.example.tls_echo.common.Message;

public class Main {
    private void openServer(int port, String password) {
        Server server = new Server(port, password, new Callback() {
            @Override
            public void onConnected(Socket socket) {
                out.println("Client connected: " + socket.getRemoteSocketAddress());
            }

            @Override
            public void onError(Exception e) {
                out.println("Error detected: " + e);
            }

            @Override
            public void onMessage(Message m) {
                out.println("Message received: " + m.get());
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread(server::close));

        server.open();

        out.println("Server terminated... bye!");
    }

    private Main usage() {
        out.println("Usage: Press Ctrl + C to terminate the server");
        return this;
    }

    public static void main(String[] args) {
        new Main().usage().openServer(Settings.getPort(), Settings.getKsPassword());
    }
}