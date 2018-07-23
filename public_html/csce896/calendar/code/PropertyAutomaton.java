package stateprop;

import java.util.*;

import soot.Unit;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;

public abstract class PropertyAutomaton {
	String name;
	FlowSet symbols;
	FlowSet states;
	FlowSet accepts;
	
	State start;
	State trap;
	
	// Transitions are factored across the states - each has a local transition
	// system.
	
	public PropertyAutomaton() {
		symbols = new ArraySparseSet();
		states = new ArraySparseSet();
		accepts = new ArraySparseSet();
		trap = addState("trap", false, false);
	}
	
	public String addSymbol(String id) {
		symbols.add(id);
		return id;
	}
	
	public State addState(String id, boolean start, boolean accept) {
		State s = new State(id, start, accept);
		states.add(s);
		if (start) {
			// assert(this.start == null);
			this.start = s;
		}
		if (accept) {
			accepts.add(s);
		}
		return s;
	}
	
	public void addTransition(State s, String a, State t) {
		s.addTrans(a,t);
	}
	
	public State delta(State source, String a) {
		State target = source.delta(a);	
		return (target == null) ? trap : target;	
	}
	
	public FlowSet deltaLifted(FlowSet sources, String a)	{
		FlowSet targets = new ArraySparseSet();
		Iterator sourcesIt = sources.iterator();
		
		while (sourcesIt.hasNext()) {
			targets.add(delta((State)sourcesIt.next(),a));
		}
		return targets;
	}
	
	public State Delta(State source, String[] sigma) {
		State tmp = source;
		for (int i = 0; i < sigma.length; i++) {
			tmp = delta(tmp, sigma[i]);
		}
		return tmp;
	}
	
	public FlowSet DeltaLifted(FlowSet sources, String[] sigma)	{
		FlowSet targets = new ArraySparseSet();
		Iterator sourcesIt = sources.iterator();
		
		while (sourcesIt.hasNext()) {
			targets.add(Delta((State)sourcesIt.next(),sigma));
		}
		return targets;
	}
	
	abstract public String[] unitLabel(Unit unit);
	
	public State getStart() {
		return start;
	}
	
	public State getTrap() {
		return trap;
	}
	
	public FlowSet getStates() {
		return states;
	}

}
