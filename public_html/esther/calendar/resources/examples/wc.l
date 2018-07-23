%option noyywrap
%{
int lines = 0, chars = 0;
%}
 
%%
\n      lines++; chars++;
.       chars++;
 
%%
main() {
  yylex();
  printf("#lines = %i, #chars = %i\n", lines, chars);
}
