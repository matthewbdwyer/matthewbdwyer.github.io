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
 
%left '+' '-'
%left '*' '/'
 
%% 
exp : tIDENTIFIER { printf("load %s\n",$1); }
    | tINTCONST   { printf("push %i\n",$1); }
    | exp '*' exp { printf("mult\n"); }
    | exp '/' exp { printf("div\n"); }
    | exp '+' exp { printf("plus\n"); }
    | exp '-' exp { printf("minus\n"); }
    | '(' exp ')' {}
;
 
%%

main() {
  (void)yyparse();
}
