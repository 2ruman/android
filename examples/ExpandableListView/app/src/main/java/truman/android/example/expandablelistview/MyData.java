package truman.android.example.expandablelistview;

public final class MyData {
    private String data;
    private boolean state;
    private boolean stateful;
    private Object tag;

    public MyData(String data) {
        this(data, false);
    }

    public MyData(String data, boolean state) {
        this(data, state, true);
    }

    public MyData(String data, boolean state, boolean stateful) {
        this.data = data;
        this.state = state;
        this.stateful = stateful;
    }

    public String get() {
        return data;
    }

    public boolean getState() {
        return state;
    }

    public MyData setState(boolean state) {
        this.state = state;
        return this;
    }

    public Object getTag() {
        return tag;
    }

    public MyData setTag(Object tag) {
        this.tag = tag;
        return this;
    }

    public boolean isStateful() {
        return stateful;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof MyData) && equals((MyData) obj);
    }

    private boolean equals(MyData that) {
        return this.data.equals(that.data);
    }
}
