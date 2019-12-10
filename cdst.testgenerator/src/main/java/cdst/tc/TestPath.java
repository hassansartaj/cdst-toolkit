package cdst.tc;

import java.io.PrintWriter;
import java.util.ArrayList;

import cdst.sm.SMTransition;

public class TestPath {

	ArrayList<SMTransition> transitions=new ArrayList<SMTransition>();
	
	public TestPath(){
	}
	
	public void addNextStateMachineTransition(SMTransition stateMachineTransition) {
		transitions.add(stateMachineTransition);
	}
	
	public ArrayList<SMTransition> getStateMachineTransitions() {
		return this.transitions;
	}
	
	public ArrayList<SMTransition> copyTransitions() {
		ArrayList<SMTransition> transitions1=new ArrayList<SMTransition>();
		for (SMTransition stateMachineTransition : transitions) {
			transitions1.add(stateMachineTransition);
		}
		return transitions1;
	}
	
	public void print(PrintWriter writer) {
		writer.print("{ "+transitions.get(0).getSourceState() + " }, ");
		for (SMTransition stateMachineTransition : transitions) {
			writer.print(stateMachineTransition.getTransitionName() + ", { " + stateMachineTransition.getTargetState() + " }, ");
		}writer.println();
	}	
}
