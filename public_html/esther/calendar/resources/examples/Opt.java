import joos.lib.*;

public class Opt {

  public Opt() { super(); }

  public void foo() {
    String s;

    if ((s = "test") == null) return;

    s = "dead";
  }

} 
