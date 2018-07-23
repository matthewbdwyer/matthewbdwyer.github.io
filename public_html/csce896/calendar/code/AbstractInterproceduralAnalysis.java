/* Soot - a J*va Optimization Framework
 * Copyright (C) 2005 Antoine Mine, Matthew Dwyer
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

// change this eventually to replace version in soot
//     soot.jimple.toolkits.annotation.purity
package summaryBasedStateProp; 

import java.util.*;
import java.io.*;
import soot.*;
import soot.util.dot.*;
import soot.jimple.*;
import soot.jimple.toolkits.annotation.purity.DirectedCallGraph;
import soot.jimple.toolkits.annotation.purity.SootMethodFilter;
import soot.jimple.toolkits.callgraph.*;
import soot.toolkits.graph.*;

/**
 * Inter-procedural summary-based analysis framework
 * <p>
 * A "summary" is an abstract element associated to each method that fully
 * models the effect of calling the method. In a summary-based analysis, the
 * summary of a method can be computed using solely the summary of all methods
 * it calls: the summary does not depend upon the context in which a method is
 * called. The inter-procedural analysis interacts with an underlying intra-procedural
 * analysis to compute the summary of a method.  The underlying analysis,
 * in turn, makes use of summaries calculated for called methods to implement
 * the tranfer function across method invocation.
 * <p>
 * The inter-procedural analysis calls the intra-procedural analysis in
 * reverse topological order of method dependencies to resolve unknown summaries. 
 * It iterates over recursively dependant methods until a fixpoint is reached.
 * <p>
 * Generally, the intra-procedural works by maintaining an abstract value that
 * represent the effect of the method from its entry point up to the current
 * point. At the entry point, this value is empty. The summary of the method is
 * then the merge of the abstract values at all its return points.  Note that
 * it is possible to construct summaries that are indexed by values flowing
 * into the method to gain some measure of context-sensitivity.
 * <p>
 * You can provide off-the-shelf summaries for methods you do not wish to
 * analyse. Any method using these "filtered-out" methods will use the
 * off-the-shelf summary instead of performing an intra-procedural analysis.
 * This is useful for native methods, incremental analysis, or when you have
 * hand-made summary. Methods that are called solely by filtered-out methods will
 * never be analysed, effectively trimming the call-graph dependencies.  Note
 * that the filtering of methods in this framework will not affect call-graph
 * construction, which may in general, yield huge call-graphs.  You need to
 * find another mechanism to perform call-graph filtering.
 * <p>
 * This class tries to use the same abstract methods and data managnment policy
 * as regular {@link soot.toolkits.scalar.FlowAnalysis} classes.  In the future, it would be good to update
 * this class to be parameterized, along with the underlying flow analysis, based
 * on the underlying lattice type.
 * <p>
 * The original version of this implementation was customized somewhat for
 * an analysis in {@link soot.jimple.toolkits.annotation.purity}. This implementation
 * generalizes that implementation by allowing the underlying intra-procedural 
 * analysis to have a flow value type that is different from the summary type.
 * The javadoc comments have been enriched to express the expected types of 
 * parameters to help distinguish when a summary is expected versus a value of
 * the underlying flow analysis lattice.
 * <p>
 * @author Antoine Mine (original design and implementation)
 * @author Matthew Dwyer (modifications to generalize framework)
 */
public abstract class AbstractInterproceduralAnalysis {

	public static final boolean doCheck = false;
	
	protected int recurseBound; // helper to prevent common instantiation error
	
	protected CallGraph cg; // analysed call-graph

	protected DirectedGraph dg; // filtered trimed call-graph

	protected Map data; // SootMethod -> summary

	protected Map order; // SootMethod -> topo order

	protected Map unanalysed; // SootMethod -> summary

	/** Initial summary value for analysed funtions. */
	protected abstract Object newInitialSummary();

	/** Initial value for underlying intra-procedural flow analysis */
	protected abstract Object newInitialFlow();

	/**
	 * Whenever the analyse requires the summary of a method you filtered-out,
	 * this function is called instead of analyseMethod.
	 * 
	 * <p>
	 * Note: This function is called at most once per filtered-out method. It is
	 * the equivalent of entryInitialFlow!
	 * 
	 */
	protected abstract Object summaryOfUnanalysedMethod(SootMethod method);

	/**
	 * Compute the summary for a method by analysing its body.
	 * 
	 * Will be called only on methods not filtered-out.
	 * 
	 * @param method
	 *            is the method to be analysed
	 * @param dst
	 *            is summary where to put the computed method summary
	 */
	protected abstract void analyseMethod(SootMethod method, Object dst);

	/**
	 * Interprocedural analysis will call applySummary repeatidly as a
	 * consequence to analyseCall. Once for each possible target method of the
	 * callStmt statement, provided with its summary.
	 * 
	 * @param src
	 *            flow analysis value  before the call statement
	 * @param callStmt
	 *            a statement containing a InvokeStmt or InvokeExpr
	 * @param summary
	 *            summary of the possible target of callStmt considered here
	 * @param dst
	 *            flow analysis value after the call statement
	 * @see analyseCall
	 */
	protected abstract void applySummary(Object src, Stmt callStmt,
			Object summary, Object dst);

	/**
	 * Merge flow analysis values.
	 */
	protected abstract void merge(Object in1, Object in2, Object out);

	/** 
	 * Copy flow analysis values.
	 */
	protected abstract void copy(Object sr, Object dst);

	/**
	 * Called by drawAsOneDot to fill dot subgraph out with the contents of
	 * summary o.
	 * 
	 * @param prefix
	 *            gives you a unique string to prefix your node names and avoid
	 *            name-clash
	 */
	protected void fillDotGraph(String prefix, Object o, DotGraph out) {
		throw new Error(
				"abstract function AbstractInterproceduralAnalysis.fillDotGraph called but not implemented.");
	}

	/**
	 * Analyse the call callStmt in the context src, and put the resul into dst.
	 * This will repeatedly calling summaryOfUnanalysedMethod and applySummary,
	 * and then merging the results using merge.
	 * 
	 * @param src
	 *            the incoming flow value, in the underlying analysis, to the
	 *            call stmt
	 * @param callStmt
	 *            the call stmt itself
	 * @param dst
	 *            the resultant flow value after applying the summary
	 */
	protected void analyseCall(Object src, Stmt callStmt, Object dst) {
		Object accum = newInitialFlow();
		Iterator it = cg.edgesOutOf(callStmt);
		copy(accum, dst);
		while (it.hasNext()) {
			Edge edge = (Edge) it.next();
			SootMethod m = edge.tgt();
			Object elem;
			if (data.containsKey(m)) {
				// analysed method
				elem = data.get(m);
			} else {
				// unanalysed method
				if (!unanalysed.containsKey(m)) {
					unanalysed.put(m, summaryOfUnanalysedMethod(m));
				}
				elem = unanalysed.get(m);
			}
			applySummary(src, callStmt, elem, accum);
			merge(dst, accum, dst);
		}
	}

	/**
	 * The constructor performs some preprocessing, but you have to call
	 * doAnalysis to preform the real stuff.
	 */
	public AbstractInterproceduralAnalysis(CallGraph cg,
			SootMethodFilter filter, Iterator heads, boolean verbose) {
		this.cg = cg;
		this.dg = new DirectedCallGraph(cg, filter, heads, verbose);
		this.data = new HashMap();
		this.unanalysed = new HashMap();
		this.recurseBound = 10;

		// construct reverse pseudo topological order on filtered methods
		this.order = new HashMap();
		PseudoTopologicalOrderer o = new ReversePseudoTopologicalOrderer();
		Iterator it = (o.newList(dg)).iterator();
		int i = 0;
		while (it.hasNext()) {
			this.order.put(it.next(), new Integer(i));
			i++;
		}
	}

	/**
	 * Dump the interprocedural analysis result as a graph. One node / subgraph
	 * for each analysed method that contains the method summary, and call-to
	 * edges.
	 * 
	 * <p>
	 * Note: this graph does not show filtered-out methods for which a
	 * conservative summary was asked via summaryOfUnanalysedMethod.
	 * 
	 * @param name
	 *            output filename
	 * @see fillDotGraph
	 */
	public void drawAsOneDot(String name) {
		DotGraph dot = new DotGraph(name);
		dot.setGraphLabel(name);
		dot.setGraphAttribute("compound", "true");
		// dot.setGraphAttribute("rankdir","LR");
		int id = 0;
		Map idmap = new HashMap();

		// draw sub-graph cluster
		Iterator it = dg.iterator();
		while (it.hasNext()) {
			SootMethod m = (SootMethod) it.next();
			DotGraph sub = dot.createSubGraph("cluster" + id);
			DotGraphNode label = sub.drawNode("head" + id);
			idmap.put(m, new Integer(id));
			sub.setGraphLabel("");
			label.setLabel("(" + order.get(m) + ") " + m.toString());
			label.setAttribute("fontsize", "18");
			label.setShape("box");
			if (data.containsKey(m))
				fillDotGraph("X" + id, data.get(m), sub);
			id++;
		}

		// connect edges
		it = dg.iterator();
		while (it.hasNext()) {
			SootMethod m = (SootMethod) it.next();
			Iterator itt = dg.getSuccsOf(m).iterator();
			while (itt.hasNext()) {
				SootMethod mm = (SootMethod) itt.next();
				DotGraphEdge edge = dot.drawEdge("head" + idmap.get(m), "head"
						+ idmap.get(mm));
				edge.setAttribute("ltail", "cluster" + idmap.get(m));
				edge.setAttribute("lhead", "cluster" + idmap.get(mm));
			}

		}

		File f = new File(SourceLocator.v().getOutputDir(), name
				+ DotGraph.DOT_EXTENSION);
		dot.plot(f.getPath());
	}

	/**
	 * Dump the each summary computed by the interprocedural analysis as a
	 * seperate graph.
	 * 
	 * @param prefix
	 *            is prepended before method name in output filename
	 * @param drawUnanalysed
	 *            do you also want info for the unanalysed methods required by
	 *            the analysis via summaryOfUnanalysedMethod ?
	 * 
	 * @see fillDotGraph
	 */
	public void drawAsManyDot(String prefix, boolean drawUnanalysed) {
		Iterator it = data.keySet().iterator();
		while (it.hasNext()) {
			SootMethod m = (SootMethod) it.next();
			DotGraph dot = new DotGraph(m.toString());
			dot.setGraphLabel(m.toString());
			fillDotGraph("X", data.get(m), dot);
			File f = new File(SourceLocator.v().getOutputDir(), prefix
					+ m.toString() + DotGraph.DOT_EXTENSION);
			dot.plot(f.getPath());
		}

		if (drawUnanalysed) {
			it = unanalysed.keySet().iterator();
			while (it.hasNext()) {
				SootMethod m = (SootMethod) it.next();
				DotGraph dot = new DotGraph(m.toString());
				dot.setGraphLabel(m.toString() + " (unanalysed)");
				fillDotGraph("X", unanalysed.get(m), dot);
				File f = new File(SourceLocator.v().getOutputDir(), prefix
						+ m.toString() + "_u" + DotGraph.DOT_EXTENSION);
				dot.plot(f.getPath());
			}
		}
	}

	/**
	 * Query the analysis result.
	 */
	public Object getSummaryFor(SootMethod m) {
		if (data.containsKey(m))
			return data.get(m);
		if (unanalysed.containsKey(m))
			return unanalysed.get(m);
		return newInitialSummary();
	}

	/**
	 * Get an iterator over the list of SootMethod with an associated summary.
	 * (Does not contain filtered-out or native methods.)
	 */
	public Iterator getAnalysedMethods() {
		return data.keySet().iterator();
	}

	/**
	 * Carry out the analysis.
	 * 
	 * Call this from your InterproceduralAnalysis constructor, just after
	 * super(cg). Then , you will be able to call drawAsDot, for instance.
	 */
protected void doAnalysis(boolean verbose)
    {
	// queue class
	class IntComparator implements Comparator {
	    public int compare(Object o1, Object o2) 
	    {
		Integer v1 = (Integer)order.get(o1);
		Integer v2 = (Integer)order.get(o2);
		return v1.intValue()-v2.intValue();
	    }
	    public boolean equals(Object o1, Object o2) 
	    {
		Integer v1 = (Integer)order.get(o1);
		Integer v2 = (Integer)order.get(o2);
		return v1.intValue()==v2.intValue();
	    }
	};
	SortedSet queue = new TreeSet(new IntComparator());
	
	// init
	Iterator it = order.keySet().iterator();
	while (it.hasNext()) {
	    Object o = it.next();
	    data.put(o, newInitialSummary());
	    queue.add(o);
	}

	Map nb = new HashMap(); // only for debug pretty-printing

	// fixpoint iterations
	while (!queue.isEmpty()) {
	    SootMethod m = (SootMethod)queue.first();
	    queue.remove(m);
	    Object newSummary = newInitialSummary();
	    Object oldSummary = data.get(m);

	    if (nb.containsKey(m)) nb.put(m,new Integer(((Integer)nb.get(m)).intValue()+1));
	    else nb.put(m,new Integer(1));
	    
	    if (verbose) {
	    		G.v().out.println(" |- processing "+m.toString()+" ("+nb.get(m)+"-st time)");
	    }

	    if (((Integer)nb.get(m)).intValue() < recurseBound) {	    
	    		analyseMethod(m,newSummary);
	    		if (!oldSummary.equals(newSummary)) {
	    			// summary for m changed!
	    			data.put(m,newSummary);
	    			queue.addAll(dg.getPredsOf(m));
	    		}
	    } else {
	    		G.v().out.println(" |- probable recursion error in analyzing "+m.toString());
	    		G.v().out.println(" |- did you implement deep-equality on method summaries?");
	    }
	}

	// fixpoint verification
	if (doCheck) {
	    it = order.keySet().iterator();
	    while (it.hasNext()) {
		SootMethod m = (SootMethod)it.next();
		Object newSummary = newInitialSummary();
		Object oldSummary = data.get(m);
		analyseMethod(m,newSummary);
		if (!oldSummary.equals(newSummary)) {
		    G.v().out.println("inter-procedural fixpoint not reached for method "+m.toString());
		    DotGraph gm  = new DotGraph("false_fixpoint");
		    DotGraph gmm = new	DotGraph("next_iterate");
		    gm.setGraphLabel("false fixpoint: "+m.toString());
		    gmm.setGraphLabel("fixpoint next iterate: "+m.toString());
		    fillDotGraph("", oldSummary, gm);
		    fillDotGraph("", newSummary, gmm);
		    gm.plot(m.toString()+"_false_fixpoint.dot");
		    gmm.plot(m.toString()+"_false_fixpoint_next.dot");
		    throw new Error("AbstractInterproceduralAnalysis sanity check failed!!!");
		}
	    }
	}

    }}
