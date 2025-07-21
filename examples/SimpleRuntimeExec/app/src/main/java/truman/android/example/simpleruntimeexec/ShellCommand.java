package truman.android.example.simpleruntimeexec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShellCommand {

    public static void execute(String command, IShellCommandCallback callback) {
        if (command == null) {
            return;
        }
        try {
            Process proc = Runtime.getRuntime().exec(command);
            BufferedReader reader;
            String line;

            reader = new BufferedReader(
                    new InputStreamReader(proc.getInputStream()));
            while ((line = reader.readLine()) != null) {
                callback.onReadLine(line);
            }
            reader.close();

            reader = new BufferedReader(
                    new InputStreamReader(proc.getErrorStream()));
            while ((line = reader.readLine()) != null) {
                callback.onReadLine(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
