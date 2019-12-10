package cdst.tc;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import cdst.sm.SMTransition;

public class TestCase {

	//ArrayList<String> testPaths=new ArrayList<String>();
	ArrayList<TestPath> paths=new ArrayList<TestPath>();
	ArrayList<TestPath> prefferedTestPaths=new ArrayList<TestPath>();


	/**
	 * 
	 * @param transitions
	 * @param targetState
	 * @param transitionName
	 * @param testPath
	 * @return
	 */
	public Boolean generateTestPaths(ArrayList<SMTransition> transitions, String targetState, String transitionName, TestPath testPath, String startPathFromState) {
		Boolean foundTerminal=false;
		if(testPath.getStateMachineTransitions().size()>0)
		{
			int visitedIndex=0;

			for (SMTransition stateMachineTransition : transitions) {
				if(stateMachineTransition.getSourceState().equalsIgnoreCase(targetState) && !isTargetDuplicateInPath(testPath, stateMachineTransition.getTargetState(), stateMachineTransition.getSourceState()))
				{
					foundTerminal=true;

					ArrayList<SMTransition> nonVisitedTransitions = getNonVisitedTransitions(transitions, visitedIndex);
					//----------------------------------------
					TestPath ttPath = copyPreviousTestPath(testPath);
					ttPath.addNextStateMachineTransition(stateMachineTransition);
					//----------------------------------------
					foundTerminal=generateTestPaths(nonVisitedTransitions, stateMachineTransition.getTargetState(), stateMachineTransition.getTransitionName(), ttPath, startPathFromState);
				}
				visitedIndex++;
			}
			if(!foundTerminal)
			{
				paths.add(testPath);
				foundTerminal=true;
			}
		}

		else
		{
			//			String startPathFromState=transitions.get(0).getSourceState();
			for (int i = 0; i < transitions.size(); i++) {
				if (transitions.get(i).getSourceState().equalsIgnoreCase(startPathFromState)) {
					testPath=new TestPath();
					testPath.addNextStateMachineTransition(transitions.get(i));
					foundTerminal=true;
					foundTerminal=generateTestPaths(transitions, transitions.get(i).getTargetState(), transitions.get(i).getTransitionName(), testPath, startPathFromState);
				}
			}		
		}
		return foundTerminal;
	}

	//old method
	public Boolean isTargetDuplicateInPath(TestPath path, String target) {
		Boolean isduplicate=false;
		ArrayList<SMTransition> pathTransitions=new ArrayList<SMTransition>();
		pathTransitions=path.getStateMachineTransitions();
		if(target!=null)
		{
			for (SMTransition stateMachineTransition : pathTransitions) {

				if((stateMachineTransition.getTargetState()!=null && target.equalsIgnoreCase(stateMachineTransition.getTargetState()))
						|| (stateMachineTransition.getSourceState()!=null && target.equalsIgnoreCase(stateMachineTransition.getSourceState())))
					isduplicate=true;
			}
		}
		return isduplicate;
	}

	public Boolean isTargetDuplicateInPath(TestPath path, String target, String source) {
		Boolean isduplicate=false;
		ArrayList<SMTransition> pathTransitions=new ArrayList<SMTransition>();
		pathTransitions=path.getStateMachineTransitions();
		if(target!=null)
		{
			for (SMTransition stateMachineTransition : pathTransitions) {
				//TODO: need to further analyze
				if((stateMachineTransition.getTargetState()!=null && target.equalsIgnoreCase(stateMachineTransition.getTargetState()))
						&& (stateMachineTransition.getSourceState()!=null && source.equalsIgnoreCase(stateMachineTransition.getSourceState())))
					isduplicate=true;
			}
		}
		return isduplicate;
	}

	/**
	 * 
	 * @param tPath
	 * @return
	 */
	public TestPath copyPreviousTestPath(TestPath tPath) {
		TestPath ttPath=new TestPath();
		for (SMTransition stateMachineTransition2 : tPath.getStateMachineTransitions()) {
			ttPath.addNextStateMachineTransition(stateMachineTransition2);
		}
		return ttPath;
	}


	/**
	 * 
	 * @param transitions
	 * @param visitedIndex
	 * @return
	 */
	public ArrayList<SMTransition> getNonVisitedTransitions(
			ArrayList<SMTransition> transitions, int visitedIndex) {
		ArrayList<SMTransition> nonVisitedTransitions=new ArrayList<SMTransition>();
		for (SMTransition sMTransition : transitions) {
			nonVisitedTransitions.add(sMTransition);
		}
		nonVisitedTransitions.remove(visitedIndex);
		return nonVisitedTransitions;
	}

	/**
	 * 
	 */
	public void printTestPaths() {
		PrintWriter writer=null;
		Integer i=0;

		try {
			writer = new PrintWriter("TransitionTree.txt");
			for (TestPath tPath : paths) {
				i++;
				writer.print(i +":: ");
				tPath.print(writer);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			writer.close();
		}


	}

	/**
	 * 
	 * @param transitions
	 * @return
	 */
	public ArrayList<SMTransition> getPathReadyTransitions(
			ArrayList<SMTransition> transitions) {
		ArrayList<SMTransition> modifiedTransitions = new ArrayList<SMTransition>();
		copyTransitions(modifiedTransitions, transitions);

		for (SMTransition parentStateTransition : transitions) {
			if(parentStateTransition.getTargetState()!=null && isTarget_AParentOfAnyState(parentStateTransition.getTargetState(),transitions))
			{
				ArrayList<SMTransition> nestedTransitions=getStateNestedTransitions(parentStateTransition.getTargetState(), transitions);
				//==========================
				ArrayList<SMTransition> parentExitTransitions=getParentStateExitTransitions(transitions,parentStateTransition.getTargetState());
				addParentExitTransitions(modifiedTransitions, nestedTransitions, parentExitTransitions);
				//==========================
				if(parentStateTransition.getTargetState()!=parentStateTransition.getSourceState())
					addParentEntryTransitions(nestedTransitions, modifiedTransitions, parentStateTransition);				
			}
		}
		modifiedTransitions=deleteDuplicateTransition(modifiedTransitions);
		return modifiedTransitions;
	}

	/**
	 * 
	 * @param modifiedTransitions
	 * @param nestedTransitions
	 * @param parentExitTransitions
	 */
	public void addParentExitTransitions(ArrayList<SMTransition> modifiedTransitions, ArrayList<SMTransition> nestedTransitions, ArrayList<SMTransition> parentExitTransitions) {
		//select Nested States
		for (SMTransition stateMachineTransition : nestedTransitions) {
			String nestedState=stateMachineTransition.getSourceState();
			for (SMTransition exitTransition : parentExitTransitions) {
				SMTransition newTransition= new SMTransition(exitTransition.getTransitionName(), nestedState, exitTransition.getTargetState(), null);
				if(!containsTransition(newTransition, modifiedTransitions))
					modifiedTransitions.add(newTransition);
			}
		}
	}
	/**
	 * 
	 * @param transitions
	 * @param parentStateName
	 * @return
	 */
	public ArrayList<SMTransition> getParentStateExitTransitions(ArrayList<SMTransition> transitions, String parentStateName) {
		ArrayList<SMTransition> exitTransitions=new ArrayList<SMTransition>();
		for (SMTransition stateMachineTransition : transitions) {
			if(stateMachineTransition.getSourceState()!=null && stateMachineTransition.getSourceState().equalsIgnoreCase(parentStateName))
			{
				if(stateMachineTransition.getSourceState()!=stateMachineTransition.getTargetState())	//if not parentState self transitions
					exitTransitions.add(stateMachineTransition);
			}
		}
		return exitTransitions;
	}

	/**
	 * 
	 * @param stateName
	 * @param tranitions
	 * @return
	 */
	public Boolean isTarget_AParentOfAnyState(String stateName, ArrayList<SMTransition> tranitions) {
		for (SMTransition stateMachineTransition : tranitions) {
			if(stateMachineTransition.getParentStateName()!=null && stateMachineTransition.getParentStateName().equalsIgnoreCase(stateName))
				return true;
		}
		return false;
	}

	/**
	 * 
	 * @param stateName
	 * @param tranitions
	 * @return
	 */
	public ArrayList<SMTransition> getStateNestedTransitions(String stateName, ArrayList<SMTransition> tranitions) {
		ArrayList<SMTransition> nestedTransitions=new ArrayList<SMTransition>();

		for (SMTransition stateMachineTransition : tranitions) {
			if(stateMachineTransition.getParentStateName()!=null && stateMachineTransition.getParentStateName().equalsIgnoreCase(stateName))
			{
				nestedTransitions.add(stateMachineTransition);
			}
		}
		return nestedTransitions;
	}

	public ArrayList<SMTransition> deleteDuplicateTransition(ArrayList<SMTransition> modifedTransitions) {
		ArrayList<SMTransition> uniqueTransitions=new ArrayList<SMTransition>();
		for (int i = 0; i < modifedTransitions.size(); i++) {
			Boolean containsDuplicate=false;
			for (int j = i+1; j < modifedTransitions.size()-(i+1); j++) {
				if(modifedTransitions.get(i).getTransitionName()!=null && modifedTransitions.get(j).getTransitionName().equalsIgnoreCase(modifedTransitions.get(i).getTransitionName()) &&
						(modifedTransitions.get(i).getSourceState()!=null && modifedTransitions.get(j).getSourceState().equalsIgnoreCase(modifedTransitions.get(i).getSourceState())) &&
						(modifedTransitions.get(i).getTargetState()!=null && modifedTransitions.get(j).getTargetState().equalsIgnoreCase(modifedTransitions.get(i).getTargetState())))
				{
					containsDuplicate=true;
				}
			}
			if (!containsDuplicate) {
				uniqueTransitions.add(modifedTransitions.get(i));
			}
		}
		return uniqueTransitions;	
	}

	public void addParentEntryTransitions(ArrayList<SMTransition> nestedTransitions, ArrayList<SMTransition> modifiedTransitions, SMTransition transitionToDuplicate) {

		String state=null;
		for (SMTransition stateMachineTransition : nestedTransitions) {
			Boolean isNewTarget=true; // to find the start state in nested region ==> the state that is not target of any transition except self transition
			state=stateMachineTransition.getSourceState();
			for (SMTransition stateMachineTransition2 : nestedTransitions) {
				if(stateMachineTransition2.getTargetState()!=null && stateMachineTransition2.getTargetState().equalsIgnoreCase(state))	//check for self transition
				{
					if(stateMachineTransition2.getTargetState()!=stateMachineTransition2.getSourceState())
						isNewTarget=false;
				}
			}
			if(isNewTarget)
			{
				SMTransition newTransition= new SMTransition(transitionToDuplicate.getTransitionName(), transitionToDuplicate.getTargetState(), state, null);
				if (!containsTransition(newTransition, modifiedTransitions)) {
					modifiedTransitions.add(newTransition);
					//deleteTransition(stateMachineTransition, modifiedTransitions);
				}
			}

		}
	}

	public Boolean containsTransition(SMTransition transition, ArrayList<SMTransition> modifiedTransitions) {
		for (SMTransition stateMachineTransition : modifiedTransitions) {
			if(transition.getTransitionName()!=null && stateMachineTransition.getTransitionName().equalsIgnoreCase(transition.getTransitionName()) &&
					(transition.getSourceState()!=null && stateMachineTransition.getSourceState().equalsIgnoreCase(transition.getSourceState())) &&
					(transition.getTargetState()!=null && stateMachineTransition.getTargetState().equalsIgnoreCase(transition.getTargetState())))
			{
				return true;
			}
		}
		return false;
	}
	public void copyTransitions(ArrayList<SMTransition> modifiedTransitions, ArrayList<SMTransition> transitions) {
		for (SMTransition stateMachineTransition : transitions) {
			//if(stateMachineTransition.getParentStateName()==null)	
			modifiedTransitions.add(stateMachineTransition);
		}
	}
}
