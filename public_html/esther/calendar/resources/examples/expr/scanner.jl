package expr;

import java.lang.System;
import java_cup.runtime.Symbol;
import expr.sym;

%%

// Value returned on EOF should be special value defined by CUP
%eofval{
  return new Symbol(sym.EOF);
%eofval}

// Scanner is created with the following class name, the actual function
// called to advance to next token is "nextToken" which returns a value
// of the CUP generated "Symbol".
%class scanner
%function nextToken
%type java_cup.runtime.Symbol

ALPHA=[a-zA-Z_]
DIGIT=[0-9]

%%

[\ \t\n]+       { }
"*"             { return new Symbol(sym.tTIMES); }
"/"             { return new Symbol(sym.tDIVIDE); }
"+"             { return new Symbol(sym.tPLUS); }
"-"             { return new Symbol(sym.tMINUS); }
"("             { return new Symbol(sym.tLPAREN); }
")"             { return new Symbol(sym.tRPAREN); }
0|([1-9][0-9]*) { return new Symbol(sym.tINTCONST, new Integer(yytext())); }

