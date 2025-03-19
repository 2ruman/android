package truman.android.example.tls_echo.common;

import java.io.Serializable;

public class Packet implements Serializable {

    private final int command;

    public Packet(int command) {
        this.command = command;
    }

    public int cmd() {
        return command;
    }
}
