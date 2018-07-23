%option noyywrap
%{
#include <stdio.h>
%}
 
%%
[ \t\n]+        printf("white space, length %i\n",yyleng);
 
"*"             printf("times\n");
"/"             printf("div\n");
"+"             printf("plus\n");
"-"             printf("minus\n");
"("             printf("left parenthesis\n");
")"             printf("right parenthesis\n");
 
0|([1-9][0-9]*) printf("integer constant: %s\n",yytext);
[a-zA-Z_][a-zA-Z0-9_]* printf("identifier: %s\n",yytext);

%%
main() {
  yylex();
}
