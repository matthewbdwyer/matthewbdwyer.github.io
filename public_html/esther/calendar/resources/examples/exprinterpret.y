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

%type <intconst> exp
 
%start s
 
%left '+' '-'
%left '*' '/'
 
%% 
s : exp { printf("answer is %i\n", $1); }

exp : tIDENTIFIER { $$ = 1; }
    | tINTCONST   { $$ = $1; }
    | exp '*' exp { $$ = $1 * $3; }
    | exp '/' exp { $$ = $1 / $3; }
    | exp '+' exp { $$ = $1 + $3; }
    | exp '-' exp { $$ = $1 - $3; }
    | '(' exp ')' { $$ = $2; }
;
 
%%

main() {
  (void)yyparse();
}
