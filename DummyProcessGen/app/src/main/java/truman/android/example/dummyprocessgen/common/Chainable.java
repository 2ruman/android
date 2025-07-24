package truman.android.example.dummyprocessgen.common;

interface Chainable {
    boolean isChaining();
    void onChain();
    void postChain();

    default void chain() {
        if (isChaining()) {
            onChain();
            postChain();
        }
    }
}
