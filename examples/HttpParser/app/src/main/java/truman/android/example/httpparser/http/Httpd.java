package truman.android.example.httpparser.http;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Httpd {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final HttpServer httpServer;

    public static Httpd create(int port) {
        return create(port, null);
    }

    public static Httpd create(int port, HttpServerCallback serverCallback) {
        return new Httpd(port, serverCallback);
    }

    private Httpd(int port, HttpServerCallback serverCallback) {
        this.httpServer = new HttpServer(port);
        this.httpServer.setHttpServerCallback(serverCallback);
    }

    public void start() {
        executorService.execute(httpServer::create);
    }

    public void stop() {
        httpServer.terminate();
        executorService.shutdown();
        try {
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {}
    }
}
