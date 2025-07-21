package truman.android.example.simpleruntimeexec;

public class Utils {

    public static String getKernelVersion() {
        String prop = ShellCommand.executeFilteredFirst("getprop",
                (line) -> line.startsWith("[ro.kernel.version]"));
        String result = "";
        try {
            if (!prop.isEmpty()) {
                result = prop.split(": ")[1].replaceAll("[\\[\\]]", "");
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        return result;
    }
}
