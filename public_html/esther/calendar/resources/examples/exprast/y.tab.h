typedef union {
   int intconst;
   char *stringconst;
   struct EXP *exp;
} YYSTYPE;
#define	tINTCONST	258
#define	tIDENTIFIER	259


extern YYSTYPE yylval;
