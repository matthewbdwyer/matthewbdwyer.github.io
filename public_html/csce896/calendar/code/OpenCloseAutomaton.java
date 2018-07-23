package stateprop;

import soot.Unit;
import soot.jimple.internal.JInvokeStmt;

public class OpenCloseAutomaton extends PropertyAutomaton {
	public OpenCloseAutomaton() {
		super();
		name = "OpenClose";
		
		// Trap state is implicitly defined
		State closed = addState("closed", true, true);
		State opened = addState("opened", false, false);

		// Only two symbols
		String open = addSymbol("open");
		String close = addSymbol("close");
		
		// Define explicit transitions, the rest go to the trap state
		addTransition(closed, open, opened);
		addTransition(opened, close, closed);
	}

	public String[] unitLabel(Unit unit) {
		// we look for calls to method call statements with the appropriate
		// names

		if (unit instanceof JInvokeStmt) {
			String methodName = ((JInvokeStmt) unit).getInvokeExpr()
					.getMethod().getName();
			if (methodName.equals("open")) {
				String[] openSigma = { "open" };
				return openSigma;
			} else if (methodName.equals("close")) {
				String[] closeSigma = { "close" };
				return closeSigma;
			}
		}

		return null;
	}
}
