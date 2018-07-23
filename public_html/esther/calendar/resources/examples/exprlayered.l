%{
#include "y.tab.h"
#include <string.h>

%}

%option noyywrap
 
%%
[ \t\n]+  /* ignore */;
 
"*"       return '*';
"/"       return '/';
"+"       return '+';
"-"       return '-';
"("       return '(';
")"       return ')';
 
0|([1-9][0-9]*)  { yylval.intconst = atoi(yytext);
                   return tINTCONST; 
                 }
[a-zA-Z_][a-zA-Z0-9_]* { yylval.stringconst = (char *)malloc(strlen(yytext)+1);
                         sprintf(yylval.stringconst,"%s",yytext); 
                         return tIDENTIFIER; 
                       }
%%
