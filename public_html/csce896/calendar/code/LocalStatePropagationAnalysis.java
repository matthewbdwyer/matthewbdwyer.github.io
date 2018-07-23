package stateprop;

import java.util.Iterator;

import soot.Unit;
import soot.ValueBox;
import soot.jimple.internal.JimpleLocal;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardFlowAnalysis;

public class LocalStatePropagationAnalysis extends ForwardFlowAnalysis {
	FlowSet emptySet = new ArraySparseSet();
	PropertyAutomaton pa;
	
	public LocalStatePropagationAnalysis(UnitGraph graph, PropertyAutomaton pa) {
		super(graph);
		this.pa = pa;
		doAnalysis();
	}
	    
	    /**
	 * All INs are initialized to the empty set.
	 */
	protected Object newInitialFlow() {
		return emptySet.clone();
	}

	/**
	 * IN(Start) is the empty set
	 */
	protected Object entryInitialFlow() {
		FlowSet init = (FlowSet) emptySet.clone();
		init.add(pa.getStart());
		return init;
	}

	/**
	 * If label(unit) in pa.Symbols then OUT is delta lifted to sets and
	 * applied to IN else use identity transfer function
	 */
	protected void flowThrough(Object inValue, Object unit, Object outValue) {
		FlowSet in = (FlowSet)inValue;
		FlowSet out = (FlowSet)outValue;
		
		// update result with new generated value
		String[] sigma = pa.unitLabel((Unit)unit);
		
		if (sigma == null) {
			in.copy(out);
		} else {
			pa.DeltaLifted(in, sigma).copy(out);
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


