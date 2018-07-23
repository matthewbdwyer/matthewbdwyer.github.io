
package summaryBasedStateProp;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;
import stateprop.OpenCloseAutomaton;
import stateprop.PropertyAutomaton;


public class WholeProgramStatePropagationPass extends SceneTransformer
{
	private static WholeProgramStatePropagationPass instance = new WholeProgramStatePropagationPass();

	private WholeProgramStatePropagationPass() {
	}

	public static WholeProgramStatePropagationPass v() {
		return instance;
	}

    protected void internalTransform(String phaseName, Map options) {

	CallGraph cg = Scene.v().getCallGraph();

	// Find main methods
	List heads = new LinkedList();
	Iterator getClassesIt = Scene.v().getApplicationClasses().iterator();
	while (getClassesIt.hasNext()) {
	    SootClass appClass = (SootClass)getClassesIt.next();
	    Iterator getMethodsIt = appClass.getMethods().iterator();
	    while (getMethodsIt.hasNext()) {
		SootMethod method = (SootMethod) getMethodsIt.next();
		if (method.getName().equals("main")) {
		    heads.add(method);
		}
	    }
	}

	PropertyAutomaton ocpa = new OpenCloseAutomaton();
	
	WholeProgramStatePropagationAnalysis p =
	    new WholeProgramStatePropagationAnalysis(cg, heads.iterator(), ocpa, true); 
	
	// we already dump out the analysis results in the constructor
    }
}
