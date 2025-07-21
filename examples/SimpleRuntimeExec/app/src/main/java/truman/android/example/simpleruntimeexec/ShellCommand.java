package truman.android.example.simpleruntimeexec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This example is not suitable for certain shell command such as "logcat" which produces lines
 * constantly, in the case the executor thread will be stuck on the way to initialize BufferedReader.
 * If it's required, you better refer to another example, "RuntimeExec".
 */
public class ShellCommand {
    public static final String SHELL_COMMAND_EXAMPLE = "cat /proc/meminfo";

    public static void execute(String command, IShellCommandCallback callback) {
        if (command == null) {
            return;
        }
        try {
            Process proc = Runtime.getRuntime().exec(command);
            BufferedReader reader;
            String line;

            /* To get standard output from the process */
            reader = new BufferedReader(
                    new InputStreamReader(proc.getInputStream()));
            while ((line = reader.readLine()) != null) {
                callback.onReadLine(line);
            }
            reader.close();

            /* To get standard error from the process */
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
