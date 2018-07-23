package stateprop;

import java.util.*;

public class State {
	String id;
	boolean start;
	boolean accept;
	
	Map localTrans;
	
	public State(String id, boolean start, boolean accept)	{
		this.id = id;
		this.start = start;
		this.accept = accept;
		localTrans = new HashMap();
	}
	
	public boolean isStart() {
		return start;
	}
	
	public boolean isAccept() {
		return accept;
	}
	
	public String toString() {
		String tmp;
		tmp = (accept) ? "(" : "";
		tmp = (start) ? tmp + ">" : tmp;
		tmp = tmp + ((id == null) ? this.toString() : id);
		return tmp + ((accept) ? ")" : "");
	}
	
	public void addTrans(String s, State target) {
		localTrans.put(s, target);
	}
	
	public State delta(String s) {
		return (State)localTrans.get(s);
	}

}
