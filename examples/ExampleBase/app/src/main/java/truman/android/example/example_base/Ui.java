package truman.android.example.example_base;

import java.lang.ref.WeakReference;

public final class Ui {

    private static WeakReference<Out> out = new WeakReference<>(null);

    public interface Out {
        void print(String s);
        void println(String s);
        void clear();

        default void print(String s, int color, int style) {}
        default void println() {
            println("");
        }
    }

    public static void setOut(Out o) {
        synchronized (Ui.class) {
            out = new WeakReference<>(o);
        }
    }

    public static void print(String s, int color, int style) {
        synchronized (Ui.class) {
            Out o = out.get();
            if (o != null) o.print(s, color, style);
        }
    }

    public static void print(String s) {
        synchronized (Ui.class) {
            Out o = out.get();
            if (o != null) o.print(s);
        }
    }

    public static void println(String s) {
        synchronized (Ui.class) {
            Out o = out.get();
            if (o != null) o.println(s);
        }
    }

    public static void println() {
        synchronized (Ui.class) {
            Out o = out.get();
            if (o != null) o.println();
        }
    }

    public static void clear() {
        synchronized (Ui.class) {
            Out o = out.get();
            if (o != null) o.clear();
        }
    }
}
