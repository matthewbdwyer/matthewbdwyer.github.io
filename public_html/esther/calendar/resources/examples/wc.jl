import java.lang.System;

%%

%class WCLex

%{
  private int lines = 0, chars = 0;

  public static void main(String argv[]) {
      String filename = argv[0];
      java.io.InputStream inp;
      WCLex lexer;

      try {
        inp = new java.io.FileInputStream(filename);
        lexer = new WCLex(inp);

        try { 
          lexer.yylex(); 
        }
        catch (java.io.IOException exception) {
          System.out.println("exception raised");
        }
      }
      catch (java.io.FileNotFoundException exception) {
        System.out.println("exception raised");
      }
  }
%}

%eof{
          System.out.println("#lines = " + lines + 
                             " #chars = " + chars);
%eof}

%integer

 
%%
\n      { lines++; chars++; }
.       { chars++; }
 
