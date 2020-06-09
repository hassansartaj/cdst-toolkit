/**
 * 
 */
package cdst.sm;

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.Transition;

/**
 * A class that represents a state machine vertex (state). 
 * 
 * @author Hassan Sartaj
 * @version 1.0
 */
public class FVertex {
	private String name;
	private EList<Transition> incomings;
	private EList<Transition> outgoings;
	/**
	 * @param name
	 * @param incomings
	 * @param outgoings
	 */
	public FVertex(String name, EList<Transition> incomings, EList<Transition> outgoings) {
		super();
		this.name = name;
		this.incomings = incomings;
		this.outgoings = outgoings;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the incomings
	 */
	public EList<Transition> getIncomings() {
		return incomings;
	}
	/**
	 * @param incomings the incomings to set
	 */
	public void setIncomings(EList<Transition> incomings) {
		this.incomings = incomings;
	}
	/**
	 * @return the outgoings
	 */
	public EList<Transition> getOutgoings() {
		return outgoings;
	}
	/**
	 * @param outgoings the outgoings to set
	 */
	public void setOutgoings(EList<Transition> outgoings) {
		this.outgoings = outgoings;
	}
}
