%option noyywrap
%{
#include <stdlib.h>
#include <stdio.h>
%}

%%
[aeiouy]      /* ignore */
[0-9]+        printf("%i",atoi(yytext)+1);

%%
main() {
  yylex();
}
