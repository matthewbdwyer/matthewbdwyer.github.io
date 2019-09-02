#include <stdlib.h>
#include <stdio.h>
#include <string.h>

int main() {
char *p,*q;
p = NULL;
printf ("%s",p);
q = (char *) malloc (100);
p = q;
free(q);
*p = 'x';
free(p);
p = (char *) malloc (100);
p = (char *) malloc (100);
q = p;
strcat (p,q);
}
