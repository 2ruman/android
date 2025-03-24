package truman.android.example.tls_echo.client;

import java.util.concurrent.CountDownLatch;

public class Waiter {

    private final CountDownLatch countDownLatch;

    private Waiter(boolean bypass) {
        countDownLatch = new CountDownLatch(bypass ? 0 : 1);
    }

    public static Waiter create() {
        return create(false);
    }

    public static Waiter create(boolean bypass) {
        return new Waiter(bypass);
    }

    public void release() {
        countDownLatch.countDown();
    }

    public void await() {
        try {
            countDownLatch.await();
        } catch (InterruptedException ignored) {}
    }
}