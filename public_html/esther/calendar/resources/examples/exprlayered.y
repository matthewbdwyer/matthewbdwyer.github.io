%{
#include <stdio.h>

extern char *yytext;

void yyerror() {
   printf("syntax error before %s\n",yytext); 
}
%}
 
%union {
   int intconst;
   char *stringconst;
}
 
%token <intconst> tINTCONST
%token <stringconst> tIDENTIFIER 
 
%start exp
 
%% 
 
exp : exp '+' term { printf("plus\n"); }
    | exp '-' term { printf("minus\n"); }
    | term {}
;

term : term '*' factor { printf("mult\n"); }
     | term '/' factor { printf("div\n"); }
     | factor {}
;

factor : tIDENTIFIER { printf("load %s\n",$1); }
       | tINTCONST   { printf("push %i\n",$1); }
       | '(' exp ')' {}
;
 
%%

main() {
  (void)yyparse();
}
