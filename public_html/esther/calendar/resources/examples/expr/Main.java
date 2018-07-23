// Driver for parser

package expr;

import java.lang.System;
import expr.parser;
import java_cup.runtime.Symbol;

class Main {

  static public void main(String[] args) {

      /* create a parsing object */
      parser theParser = new parser(System.in);

      try {
          theParser.parse();
      } catch (Exception e) {
        /* do cleanup here -- possibly rethrow e */
      } finally {
	/* do close out here */
      }
  }
}

