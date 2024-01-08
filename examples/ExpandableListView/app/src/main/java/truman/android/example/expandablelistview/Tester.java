package truman.android.example.expandablelistview;

public class Tester {

    public static void fillData(MyDataAdapter adapter) {
        adapter.add("A Class", new MyData("Alice", true));
        adapter.add("A Class", new MyData("Bob"));
        adapter.add("A Class", new MyData("Charlie"));
        adapter.add("B Class", new MyData("Dave"));
        adapter.add("B Class", new MyData("Eve",true));
        adapter.add("B Class", new MyData("Frank", true));
        adapter.add("B Class", new MyData("Grace"));
        adapter.add("B Class", new MyData("Heidi"));
        adapter.add("C Class", new MyData("Ivan"));
        adapter.add("C Class", new MyData("Judy", true));
        adapter.add("C Class", new MyData("Mallory"));
        adapter.add("C Class", new MyData("Oscar"));
    }
}
