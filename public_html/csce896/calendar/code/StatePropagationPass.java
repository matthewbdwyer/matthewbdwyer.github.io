package stateprop;

import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Unit;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.FlowSet;

public class StatePropagationPass extends BodyTransformer {
	private static StatePropagationPass instance = new StatePropagationPass();

	private StatePropagationPass() {
	}

	public static StatePropagationPass v() {
		return instance;
	}

	protected void internalTransform(Body b, String phaseName, Map options) {
		ExceptionalUnitGraph graph = new ExceptionalUnitGraph(b);
		
		// Construct a simple: def precedes use automaton
		//    
		//PropertyAutomaton dbupa = new DefBeforeUseAutomaton();
		PropertyAutomaton ocpa = new OpenCloseAutomaton();

		LocalStatePropagationAnalysis analysis = new LocalStatePropagationAnalysis(graph,
				ocpa);
		
		System.out.println("State propogation analysis results for "+ocpa.toString()+
				" on method "+b.getMethod().getName());

		Iterator unitIt = graph.iterator();

		while (unitIt.hasNext()) {
			Unit s = (Unit) unitIt.next();

			// Here we just print out the propagated state sets
			System.out.println("Values for statement : " + s);

			FlowSet set = (FlowSet) analysis.getFlowBefore(s);
			System.out.println("   on input " + set);

			set = (FlowSet) analysis.getFlowAfter(s);
			System.out.println("   on output " + set);

			System.out.println();

		}
	}

}
