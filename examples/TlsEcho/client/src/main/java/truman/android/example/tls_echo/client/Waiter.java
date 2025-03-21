package truman.android.example.tls_echo.client;

import java.util.concurrent.CountDownLatch;

public class Waiter {

    private final CountDownLatch countDownLatch;

    private Waiter(boolean bypass) {
        countDownLatch = (!bypass) ? new CountDownLatch(1) : null;
    }

    public static Waiter create() {
        return create(false);
    }

    public static Waiter create(boolean bypass) {
        return new Waiter(bypass);
    }

    public void release() {
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    public void await() {
        if (countDownLatch != null) {
            try {
                countDownLatch.await();
            } catch (InterruptedException ignored) {}
        }
    }
}