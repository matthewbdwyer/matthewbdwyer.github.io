#ifndef __tree_h
#define __tree_h
#include "tree.h"
#endif
#include "pretty.h"

int lineno;

void yyparse();

EXP *theexpression;

void main()
{ lineno = 1;
  yyparse();
  prettyEXP(theexpression);
}
