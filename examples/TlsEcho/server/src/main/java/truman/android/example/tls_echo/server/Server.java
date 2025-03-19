package truman.android.example.tls_echo.server;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import truman.android.example.tls_echo.common.Callback;
import truman.android.example.tls_echo.common.Packet;
import truman.android.example.tls_echo.common.Message;

import javax.net.ssl.*;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private final int port;
    private final Callback callback;
    private final ExecutorService executorService;
    private final AtomicBoolean isClosed;
    private final char[] ksPassword;
    private final Object cLock = new Object();
    private final Map<Socket, ClientHandler> clients;

    private SSLServerSocket serverSocket;

    public Server(int port, String ksPassword, Callback callback) {
        this.port = port;
        this.callback = callback;
        this.executorService = Executors.newSingleThreadExecutor();
        this.isClosed = new AtomicBoolean(false);
        this.ksPassword = ksPassword.toCharArray();
        this.clients = new HashMap<>();
    }

    private SSLServerSocket createSocket(int port, char[] ksPassword) throws KeyStoreException, IOException,
            CertificateException, NoSuchAlgorithmException, KeyManagementException, UnrecoverableKeyException {
        KeyStore keystore = KeyStore.getInstance("BKS");
        keystore.load(new FileInputStream("certs/server_ks.bks"), ksPassword);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keystore, ksPassword);

        SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
        KeyManager[] keyManagers = kmf.getKeyManagers();
        sslContext.init(keyManagers, null, null);

        SSLServerSocketFactory sssf = sslContext.getServerSocketFactory();
        SSLServerSocket sss = (SSLServerSocket) sssf.createServerSocket(port);

        return sss;
    }

    public void open() {
        try {
            serverSocket = createSocket(port, ksPassword);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                onConnected(clientSocket);
            }
        } catch (IOException | KeyStoreException | CertificateException | NoSuchAlgorithmException |
                 KeyManagementException | UnrecoverableKeyException e) {
            onServerError(e);
        }
    }

    private void onConnected(Socket clientSocket) {
        callback.onConnected(clientSocket);
        registerClient(clientSocket);
    }

    private void onServerError(Exception e) {
        callback.onError(e);
        close();
    }

    private void registerClient(Socket clientSocket) {
        ClientHandler clientHandler = new ClientHandler(clientSocket);
        synchronized (cLock) {
            clients.put(clientSocket, clientHandler);
        }
        executorService.execute(clientHandler);
    }

    private void unregisterClient(ClientHandler clientHandler) {
        synchronized (cLock) {
            clients.remove(clientHandler.get());
        }
        clientHandler.close();
    }

    public void close() {
        if (isClosed.getAndSet(true)) {
            return;
        }
        closeClients();
        closeSelf();

        executorService.shutdown();
    }

    private void closeClients() {
        synchronized (cLock) {
            clients.values().forEach(ClientHandler::close);
            clients.clear();
        }
    }

    private void closeSelf() {
        closeSafe(serverSocket);
    }

    private static void closeSafe(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ignored) {}
    }

    public void send(Message message) {
        synchronized (cLock) {
            clients.forEach((socket, handler) -> {
                handler.send(message);
            });
        }
    }

    private class ClientHandler implements Runnable {
        Socket socket;
        ObjectInputStream ois;
        ObjectOutputStream oos;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        Socket get() {
            return socket;
        }

        void send(Packet packet) {
            try {
                if (oos != null) {
                    oos.writeObject(packet);
                }
            } catch (IOException e) {
                onClientError(this, e);
            }
        }

        @Override
        public void run() {
            try {
                ois = new ObjectInputStream(socket.getInputStream());
                oos = new ObjectOutputStream(socket.getOutputStream());

                while (true) {
                    onReceive((Packet) ois.readObject());
                }
            } catch (IOException | ClassNotFoundException e) {
                onClientError(this, e);
            }
        }

        void close() {
            closeSafe(ois);
            closeSafe(oos);
            closeSafe(socket);
        }
    }

    private void onReceive(Packet packet) {
        if (!(packet instanceof Message)) {
            return;
        }
        callback.onMessage((Message) packet);
        send((Message) packet); // Send back the packet for echo
    }

    private void onClientError(ClientHandler clientHandler, Exception e) {
        callback.onError(e);
        unregisterClient(clientHandler);
    }
}