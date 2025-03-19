package truman.android.example.tls_echo.client;

import android.content.Context;

import truman.android.example.tls_echo.common.Callback;
import truman.android.example.tls_echo.common.Packet;
import truman.android.example.tls_echo.common.Message;

import javax.net.ssl.*;
import java.io.*;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {

    private final String ip;
    private final int port;
    private final Callback callback;
    private final ExecutorService executorService;
    private final char[] ksPassword;
    private final Object cLock = new Object();
    private final Context context;

    private Connector connector;

    public Client(Context context, String ip, int port, String ksPassword, Callback callback) {
        this.context = context;
        this.ip = ip;
        this.port = port;
        this.callback = callback;
        this.executorService = Executors.newCachedThreadPool();
        this.ksPassword = ksPassword.toCharArray();
    }

    private class Connector implements Runnable {

        SSLSocket serverSocket;
        ObjectInputStream ois;
        ObjectOutputStream oos;

        void send(Packet packet) {
            try {
                if (oos != null) {
                    oos.writeObject(packet);
                }
            } catch (IOException e) {
                onError(e);
            }
        }

        @Override
        public void run() {
            try {
                serverSocket = createSocket(ip, port, ksPassword);
                serverSocket.startHandshake();

                oos = new ObjectOutputStream(serverSocket.getOutputStream());
                ois = new ObjectInputStream(serverSocket.getInputStream());

                callback.onConnected(serverSocket);

                while (true) {
                    onReceive((Packet) ois.readObject());
                }
            } catch (IOException | KeyStoreException | CertificateException |
                     NoSuchAlgorithmException | KeyManagementException | ClassNotFoundException e) {
                onError(e);
            }
        }

        void close() {
            closeSafe(ois);
            closeSafe(oos);
            closeSafe(serverSocket);
        }
    }

    public void open() {
        synchronized (cLock) {
            if (connector == null) {
                executorService.execute(connector = new Connector());
            }
        }
    }

    private SSLSocket createSocket(String ip, int port, char[] ksPassword) throws KeyStoreException, IOException,
            CertificateException, NoSuchAlgorithmException, KeyManagementException {
        KeyStore keystore = KeyStore.getInstance("BKS");
        keystore.load(context.getResources().openRawResource(R.raw.client_ks), ksPassword);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        tmf.init(keystore);

        SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
        TrustManager[] trustManagers = tmf.getTrustManagers();
        sslContext.init(null, trustManagers, null);

        SSLSocketFactory ssf = sslContext.getSocketFactory();
        SSLSocket ss = (SSLSocket) ssf.createSocket(ip, port);

        return ss;
    }

    public void send(String message) {
        executorService.execute(() -> {
            synchronized (cLock) {
                if (connector != null) {
                    connector.send(new Message(message));
                }
            }
        });
    }

    public void close() {
        executorService.execute(() -> {
            synchronized (cLock) {
                if (connector != null) {
                    connector.close();
                }
                connector = null;
            }
        });
    }

    private static void closeSafe(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ignored) {}
    }

    private void onReceive(Packet packet) {
        if (!(packet instanceof Message)) {
            return;
        }
        callback.onMessage((Message) packet);
    }

    private synchronized void onError(Exception e) {
        callback.onError(e);
        close();
    }
}