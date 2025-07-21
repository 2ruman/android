package truman.android.example.simpleruntimeexec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.function.Predicate;

public class ShellCommand {

    public static String EMPTY = "";

    public static void execute(String command, ShellCommandCallback callback) {
        if (command == null) {
            return;
        }
        executeInternal(command, callback);
    }

    public static String execute(String command) {
        if (command == null) {
            return EMPTY;
        }
        StringWriter sw = new StringWriter();
        executeInternal(command, (line) -> {
            sw.append(line);
            sw.write(System.lineSeparator());
        });
        return sw.toString();
    }

    public static String executeFilteredFirst(String command, Predicate<String> filter) {
        if (command == null) {
            return EMPTY;
        }
        String filtered = EMPTY;
        try {
            executeInternal(command, (line) -> {
                if (filter.test(line)) {
                    throw new ExecutionInterruptedException(line);
                }
            });
        } catch (ExecutionInterruptedException e) {
            filtered = e.reason;
        }
        return filtered;
    }

    private static void executeInternal(String command, ShellCommandCallback callback) {
        try {
            Process proc = Runtime.getRuntime().exec(command);
            String line;

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(proc.getInputStream()))) {
                while ((line = reader.readLine()) != null) {
                    callback.onReadLine(line);
                }
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(proc.getErrorStream()))) {
                while ((line = reader.readLine()) != null) {
                    callback.onReadLine(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ExecutionInterruptedException extends RuntimeException {
        String reason;
        ExecutionInterruptedException(String reason) { this.reason = reason; }
    }
}
