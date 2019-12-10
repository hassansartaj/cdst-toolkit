package cdst.sm;

import java.util.ArrayList;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.xmi.UnresolvedReferenceException;
import org.eclipse.uml2.uml.Behavior;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Region;
import org.eclipse.uml2.uml.State;
import org.eclipse.uml2.uml.StateMachine;
import org.eclipse.uml2.uml.Transition;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.Vertex;

public class SMReader {
	ArrayList<SMState> states = new ArrayList<SMState>();
	ArrayList<SMTransition> transitions = new ArrayList<SMTransition>();
	State parentState = null;

	public ArrayList<SMState> getStates() {
		return this.states;
	}

	public ArrayList<SMTransition> getTransitions() {
		return this.transitions;
	}

	/***
	 * @param Model
	 * @return proper java code structure object containing all classes and hierarchy
	 * @throws UnresolvedReferenceException
	 */
	public void processTheModel(EList<PackageableElement> members)
			throws UnresolvedReferenceException {

		for (PackageableElement element : members) {
			if (element.eClass() == UMLPackage.Literals.CLASS) {
				Class c = (Class) element;

				EList<Behavior> ownedBehaviors = c.getOwnedBehaviors();
				for (Behavior beh : ownedBehaviors) {
					if (beh.eClass() == UMLPackage.Literals.STATE_MACHINE) {
						// print(beh.getName());
						readBehaviours(beh);
					}
				}
			}
			if (element.eClass() == UMLPackage.Literals.STATE_MACHINE) {
				readBehaviours((Behavior) element);
			}
		}
		// System.out.println("Print this.javaCodeStructure.printClasses()");
		// print(this.javaCodeStructure.printClasses());
	}

	/**
	 * @param Behavior
	 *            behavior
	 * @functionality reads behavior of state machine region wise
	 */
	private void readBehaviours(Behavior behavior) {

		StateMachine stateMachine = (StateMachine) behavior;
		EList<Region> regions = stateMachine.getRegions();
		for (Region reg : regions) {
			readRegoins(reg);
		}
	}

	/**
	 * 
	 * @param Region
	 *            region
	 * @functionality Explore region vertex by vertex
	 */

	private void readRegoins(Region region) {

		EList<Vertex> vertices = region.getSubvertices();

		for (Vertex vertex : vertices) {
			if (vertex.eClass() == UMLPackage.Literals.STATE) {
				readVertices(vertex);
			}
		}
	}

	private void readSubRegoins(Region region) {

		EList<Vertex> vertices = region.getSubvertices();

		for (Vertex vertex : vertices) {
			if (vertex.eClass() == UMLPackage.Literals.STATE) {
				readSubVertices(vertex);
			}
		}
	}

	private void readSubVertices(Vertex vertex) {

		/** States (Derive Classes) */
		State state = (State) vertex;
		String derivedClassName = state.getLabel(); // Derived class name
		SMState stateLabel = null;
		if (this.parentState != null)
			stateLabel = new SMState(derivedClassName,
					this.parentState.getLabel());
		else
			stateLabel = new SMState(derivedClassName, null);

		// to check for duplicate
		boolean contains = containsState(derivedClassName,
				stateLabel.getParentStateName());
		if (contains == false)
			states.add(stateLabel);

		// -----------------------------------------------------------------------------

		EList<Transition> outgoingTransitions = state.getOutgoings();

		for (Transition trans : outgoingTransitions) {
			//if (state.eClass() != UMLPackage.Literals.PSEUDOSTATE)
			getTransitions(trans, derivedClassName);

		}
	}

	/**
	 * 
	 * @param vertex
	 * @functionality Explore each vertex state by state and extract
	 */
	private void readVertices(Vertex vertex) {

		/** States (Derive Classes) */
		State state = (State) vertex;
		String derivedClassName = state.getLabel(); // Derived class name
		SMState stateLabel = null;
		if (this.parentState != null)
			stateLabel = new SMState(derivedClassName,
					this.parentState.getLabel());
		else
			stateLabel = new SMState(derivedClassName, null);

		// to check for duplicate
		boolean contains = containsState(derivedClassName,
				stateLabel.getParentStateName());
		if (contains == false)
			states.add(stateLabel);

		// Nested Regions
		if (state.getRegions() != null) {
			this.parentState = state;
			EList<Region> regions = state.getRegions();
			for (Region reg : regions) { // parallel regions
				readSubRegoins(reg);
			}
		}
		// when return from <region/>
		// System.out.println("Returned from Nested...");
		this.parentState = null;
		// -----------------------------------------------------------------------------

		EList<Transition> outgoingTransitions = state.getOutgoings();

		for (Transition trans : outgoingTransitions) {
			//if (state.eClass() != UMLPackage.Literals.PSEUDOSTATE)
			getTransitions(trans, derivedClassName);
		}
	}

	public boolean containsState(String derivedClassName, String parentStateName) {
		boolean contains = false;
		for (int i = 0; i < states.size(); i++) {
			SMState element = states.get(i);
			if (derivedClassName.equalsIgnoreCase(element.getStateName()) && ((parentStateName==null && element.getParentStateName()==null) || parentStateName.equalsIgnoreCase(element.getParentStateName())))
			{
				contains = true;
			}
		}
		return contains;
	}

	/**
	 * 
	 * @param transition
	 * @param paramList
	 * @param methodStruct
	 * @param derivedClassName
	 * 
	 * @functionality handles guards condition ( conditional expressions ) and
	 *                bodies
	 */
	private void getTransitions(Transition transition, String derivedClassName) {
		SMTransition trans = null;
		//-------------------
		String targetState;
		if( transition.getTarget() == UMLPackage.Literals.PSEUDOSTATE)
			targetState="choicePoint";
		else
			targetState=transition.getTarget().getName();
		//-------------------
		if (transition.getLabel() != null) {
			if (this.parentState != null)
			{
				trans = new SMTransition(transition.getLabel(),
						transition.getSource().getName(), targetState,
						this.parentState.getLabel());
			}
			else
			{
				trans = new SMTransition(transition.getLabel(),
						transition.getSource().getName(), targetState, null);
			}
			transitions.add(trans);
		}
		else if (transition.getEffect() != null) {
			if (this.parentState != null)
			{
				trans = new SMTransition(transition.getEffect()
						.getName(), transition.getSource().getName(),
						targetState, this.parentState.getLabel());
			}
			else
			{
				trans = new SMTransition(transition.getEffect()
						.getName(), transition.getSource().getName(), targetState, null);
			}
			transitions.add(trans);
		}
	}
}
