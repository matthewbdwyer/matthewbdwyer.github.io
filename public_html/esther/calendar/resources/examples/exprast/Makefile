CC = gcc

CFLAGS = -Wall -ansi -pedantic

# First setting for when you have bison, second for byacc
YACC = bison -y -d 
#YACC = byacc -d

# First setting for unix-like operating systems, second for Windows
EXESUFFIX = 
#EXESUFFIX = .exe

main:			y.tab.o lex.yy.o main.o tree.h tree.o pretty.h pretty.o memory.h memory.o
			$(CC) lex.yy.o y.tab.o tree.o pretty.o memory.o main.o -o exp -lfl

y.tab.c y.tab.h:	exp.y
			$(YACC) -d  exp.y

lex.yy.c:		exp.l y.tab.h 
			flex exp.l

clean:	
			rm *.o y.tab.c y.tab.h lex.yy.c exp$(EXESUFFIX)

