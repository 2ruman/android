package truman.android.example.ssl_socket.server;

import android.content.Context;
import android.os.Build;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.KeyStore;
import java.security.Security;
import java.util.Iterator;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class TlsServer {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private String ksPassword = "password";
    private ServerThread serverThread;

    private final boolean clientAuth = false;
    private final int port;
    private final Context context;
    private final Ui.Out out;

    public TlsServer(Context context, int port, Ui.Out out) {
        this.context = context;
        this.port = port;
        this.out = out;
    }

    public void setKsPassword(String ksPassword) {
        this.ksPassword = ksPassword;
    }

    private interface ServerThreadCallback {
        void onSuccess(String message);
        void onFailed(String message);
    }

    public void open() {
        serverThread = new ServerThread(new ServerThreadCallback() {
            @Override
            public void onSuccess(String message) {
                out.println("Success: " + message);
            }

            @Override
            public void onFailed(String message) {
                out.println("Failed: " + message);
            }
        });
        serverThread.start();
    }

    public void close() {
        if (serverThread != null) {
            serverThread.close();
        }
    }

    private class ServerThread extends Thread {

        final ServerThreadCallback callback;
        SSLServerSocket serverSocket;
        Socket clientSocket;

        ServerThread(ServerThreadCallback callback) {
            this.callback = callback;
        }

        SSLServerSocket prepare() throws Exception {
            out.println("Prepare() - port = " + port + ", clientAuth = " + clientAuth);

            KeyStore keystore = KeyStore.getInstance("BKS");
//            keystore.load(context.getResources().openRawResource(R.raw.my_ks), ksPassword.toCharArray());
            keystore.load(context.getResources().openRawResource(R.raw.test), "thanksmiles".toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            tmf.init(keystore);

            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            TrustManager[] trustManagers = tmf.getTrustManagers();
            sslContext.init(null, trustManagers, null);


            SSLServerSocketFactory sssf = sslContext.getServerSocketFactory();
            SSLServerSocket sss = (SSLServerSocket) sssf.createServerSocket(port);
            sss.setNeedClientAuth(false);

//            sss.setWantClientAuth(false);
            sss.setEnabledCipherSuites(sss.getSupportedCipherSuites());
            sss.setEnabledProtocols(sss.getSupportedProtocols());
            String[] ret = null;
            ret = sss.getEnabledCipherSuites();
            if (ret != null) {
                out.println("Enabled Cipher: " );
                for (String s : ret) {
                    out.println(s);
                }
            }
            ret = sss.getEnabledProtocols();
            if (ret != null) {
                out.println("Enabled Prot: " );
                for (String s : ret) {
                    out.println(s);
                }
            }
            return sss;
        }

        void listen() throws Exception {
            serverSocket = prepare();

            callback.onSuccess("Waiting for clients");
            clientSocket = serverSocket.accept();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    clientSocket.getInputStream()))) {
                String line;
                while (true) {
                    line = br.readLine();
                    if (line == null || line.isEmpty()) {
                        break;
                    }
                    out.println("Received: " + line);
                }
            }
        }

        void close() {
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                    clientSocket = null;
                } catch (IOException e) {
                }
            }
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                    serverSocket = null;
                } catch (IOException e) {
                }
            }
        }

        public void run() {
            try {
                listen();
            } catch (Exception e) {
                callback.onFailed(e.toString());
                close();
            }
        }
    }
}