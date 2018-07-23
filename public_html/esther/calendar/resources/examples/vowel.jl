import java.lang.System;

%%

%class VowelLex

%{
  private int lines = 0, chars = 0;

  public static void main(String argv[]) {
      String filename = argv[0];
      java.io.InputStream inp;
      VowelLex lexer;

      try {
        inp = new java.io.FileInputStream(filename);
        lexer = new VowelLex(inp);

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

%integer

%%
[aeiouy]      { /* ignore */ }
[0-9]+        { System.out.print( Integer.parseInt(yytext())+1 ); }
.|\n             { System.out.print(yytext()); }

