package truman.android.example.expandablelistview;

public final class  MyData {
   private String data;
   private boolean state;

   public MyData(String data) {
      this(data, false);
   }

   public MyData(String data, boolean state) {
      this.data = data;
      this.state = state;
   }

   public String getData() {
      return data;
   }

   public boolean getState() {
      return state;
   }

   public MyData setState(boolean state) {
      this.state = state;
      return this;
   }
}
