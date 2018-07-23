package summaryBasedStateProp;

import soot.G;
import soot.jimple.Stmt;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardFlowAnalysis;
import stateprop.PropertyAutomaton;
import stateprop.State;

/**
 * Summary based inter-procedural state propagation analysis calculates, for each,
 * method a mapping from an input {@link State} of a {@link PropertyAutomaton} to the set of possible
 * {@link State}s produced on exit from the method along some program path.
 * 
 * @author dwyer
 *
 */
public class PerMethodStatePropagationAnalysis extends ForwardFlowAnalysis {
	FlowSet emptySet = new ArraySparseSet();
	PropertyAutomaton pa;
	State start;
	AbstractInterproceduralAnalysis inter;
	boolean verbose;
	
	public PerMethodStatePropagationAnalysis(UnitGraph graph, PropertyAutomaton pa,
			State start, AbstractInterproceduralAnalysis inter, boolean verbose) {
		super(graph);
		this.pa = pa;
		this.start = start;
		this.inter = inter;
		this.verbose = verbose;
		doAnalysis();
	}
	    
	    /**
	 * All INs are initialized to the empty set.
	 */
	protected Object newInitialFlow() {
		return emptySet.clone();
	}

	/**
	 * Initialize start state to the designated start (rather than extracting
	 * it from the {@link PropertyAutomaton} as was done in the local analysis).
	 */
	protected Object entryInitialFlow() {
		FlowSet init = (FlowSet) emptySet.clone();
		init.add(start);
		return init;
	}

	/**
	 * If label(unit) in pa.Symbols then OUT is delta lifted to sets and
	 * applied to IN else use identity transfer function
	 */
	protected void flowThrough(Object inValue, Object unit, Object outValue) {
		FlowSet in = (FlowSet)inValue;
		FlowSet out = (FlowSet)outValue;
		Stmt stmt = (Stmt)unit;
		
		// An interesting issue arises here:
		//    what if the invoked call has a symbol attached to it
		// and
		//    it contains symbols, transitively, within it's body
		//
		// It gets even more interesting if we allow "pre" and "post"
		// symbols to denote call and return from a named method.
		//
		// We take a simple solution here and assume that the methods
		// are disjoint.  Namely that the ones with symbols have nothing
		// in their bodies.
		
		// Check to see if the statement is corresponds to a symbol
		String[] sigma = pa.unitLabel(stmt);		
		
		if (sigma == null) {
			//	If not and it is a call, then use the summary
			if (stmt.containsInvokeExpr()) {
				inter.analyseCall(in, stmt, out);
			} else {
				in.copy(out);
			}
		} else {
			pa.DeltaLifted(in, sigma).copy(out);
		}
		
		if (verbose) {
			G.v().out.println(" |>>- input for "+unit+" is "+in);
			G.v().out.println(" |>>- new output for "+unit+" is "+out);
		}
	}

	/**
	 * lub is union
	 */
	protected void merge(Object in1, Object in2, Object out) {
		FlowSet inSet1 = (FlowSet) in1, inSet2 = (FlowSet) in2, outSet = (FlowSet) out;

		inSet1.union(inSet2, outSet);
	}

	protected void copy(Object source, Object dest) {
		FlowSet sourceSet = (FlowSet) source, destSet = (FlowSet) dest;

		sourceSet.copy(destSet);
	}
}


