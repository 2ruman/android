package truman.android.example.ssl_socket.client;

import android.content.Context;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.KeyStore;
import java.security.Security;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class TlsClient {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private String ksPassword = "password";
    private ClientThread clientThread;

    private final boolean clientAuth = false;
    private final String ip;
    private final int port;
    private final Context context;
    private final Ui.Out out;

    public TlsClient(Context context, String ip, int port, Ui.Out out) {
        this.context = context;
        this.ip = ip;
        this.port = port;
        this.out = out;
    }

    public void setKsPassword(String ksPassword) {
        this.ksPassword = ksPassword;
    }

    private interface ClientThreadCallback {
        void onSuccess(String message);
        void onFailed(String message);
    }

    public void connect() {
        clientThread = new ClientThread(new ClientThreadCallback() {

            @Override
            public void onSuccess(String message) {
                out.println("Success: " + message);
            }

            @Override
            public void onFailed(String message) {
                out.println("Failed: " + message);
            }
        });
        clientThread.start();
    }

    public void close() {
        if (clientThread != null) {
            clientThread.close();
        }
    }

    private class ClientThread extends Thread {

        final ClientThreadCallback callback;
        SSLSocket clientSocket;

        ClientThread(ClientThreadCallback callback) {
            this.callback = callback;
        }

        SSLSocket prepare() throws Exception {
            KeyStore keystore = KeyStore.getInstance("BKS");
            keystore.load(context.getResources().openRawResource(R.raw.my_ks), ksPassword.toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            tmf.init(keystore);

            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            TrustManager[] trustManagers = tmf.getTrustManagers();
            sslContext.init(null, trustManagers, null);

            SSLSocketFactory sf = sslContext.getSocketFactory();
            String[] ret = sf.getSupportedCipherSuites();

            if (ret != null) {
                out.println("Supported Cipher: " );
                for (String s : ret) {
                    out.println(s);
                }
            }
            SSLSocket socket = (SSLSocket) sf.createSocket(ip, port);
            out.println("conn? " + socket.isConnected());
            socket.setEnabledCipherSuites(socket.getSupportedCipherSuites());
            socket.setEnabledProtocols(socket.getSupportedProtocols());

            return socket;
        }

        void connect() throws Exception {
            clientSocket = prepare();
            clientSocket.startHandshake();

            callback.onSuccess("Connected to server");
            Thread.sleep(5000);
            out.println("woke up!");
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    clientSocket.getOutputStream()))) {
                bw.write("Hello1");
                bw.write("Hello2");
                bw.write("Hello3");
                bw.write("Hello4");
                bw.write("Hello5\n");
                bw.write("Hello6\n");
            }
            out.println("Sent data");
        }

        void close() {
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                    clientSocket = null;
                } catch (IOException e) {
                }
            }
        }

        public void run() {
            try {
                connect();
            } catch (Exception e) {
                callback.onFailed(e.toString());
                close();
            }
        }
    }

//    private void openSocket() {
//        Socket socket = null;
//        SSLContext context = null;
//        char[] passphrase = "123456".toCharArray();
//        try{
//            KeyStore keystore =  KeyStore.getInstance("BKS");
//            keystore.load(getResources().openRawResource(R.raw.my_app), passphrase);
//            TrustManagerFactory tmf = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//            tmf.init(keystore);
//            context = SSLContext.getInstance("TLS");
//            TrustManager[] trustManagers = tmf.getTrustManagers();
//            context.init(null, trustManagers, null);
//
//
//            SSLSocketFactory sf = context.getSocketFactory();
//            socket = sf.createSocket("192.168.1.228", 12345);
//        }catch (Exception e){
//            e.printStackTrace();
//            println("failed: " + e);
//        }
//    }
}