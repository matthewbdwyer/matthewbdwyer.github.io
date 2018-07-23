import java.lang.System;

%%

%class ExprLex

%{
  public static void main(String argv[]) { 
      String filename = argv[0];
      java.io.InputStream inp;
      ExprLex lexer;

      try { 
        inp = new java.io.FileInputStream(filename); 
        lexer = new ExprLex(inp);

        try { lexer.yylex(); } 
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
ALPHA=[a-zA-Z_]
DIGIT=[0-9]
 
%%

[\ \t\n]+   { System.out.println("white space, length " + yytext().length()); }


"*"                  { System.out.println("times"); }
"/"                  { System.out.println("div"); }
"+"                  { System.out.println("plus"); }
"-"                  { System.out.println("minus"); }
"("                  { System.out.println("left parenthesis"); }
")"                  { System.out.println("right parenthesis"); }
 
0|([1-9][0-9]*)        { System.out.println("integer constant: " + yytext()); }
{ALPHA}({ALPHA}|{DIGIT})* { System.out.println("identifier: " + yytext()); }

