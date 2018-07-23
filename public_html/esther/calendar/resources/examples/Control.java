import joos.lib.*;

public class Control {

  public Control() { super(); }

  public static void main(String argv[])
  {  int x,y,z;
     boolean a,b;
     String s;

     a = x < y;
     b = z != y; 
     a = !a;
     b = b || a;

     s = "Final is " + b;
  }
} 
