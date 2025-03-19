package truman.android.example.tls_echo.common;

import java.io.Serial;

public class Message extends Packet {
    @Serial
    private static final long serialVersionUID = 855731776900423712L;
    private final String message;

    public Message(String message) {
        this(message, 0);
    }

    public Message(String message, int command) {
        super(command);
        this.message = message;
    }

    public String get() {
        return message;
    }

    @Override
    public String toString() {
        return get();
    }
}
