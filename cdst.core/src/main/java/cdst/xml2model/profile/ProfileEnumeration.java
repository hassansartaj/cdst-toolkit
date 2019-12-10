/**
 * 
 */
package cdst.xml2model.profile;

import java.util.ArrayList;

/**
 * A class that represents the enumeration of the profile
 * 
 * @author Hassan Sartaj
 * @version 1.0
 */
public class ProfileEnumeration extends ProfileConcept{
	private ArrayList<String> literals;
	
	public ProfileEnumeration() {
		this.literals = new ArrayList<>(1);
	}

	/**
	 * @param name
	 */
	public ProfileEnumeration(String name) {
		super();
		this.name = name;
		this.literals = new ArrayList<>(1);
	}

	/**
	 * @return the literals
	 */
	public ArrayList<String> getLiterals() {
		return literals;
	}

	/**
	 * @param literals the literals to set
	 */
	public void setLiterals(ArrayList<String> literals) {
		this.literals = literals;
	}
	
	/**
	 * @param literal
	 */
	public void addLiteral(String literal) {
		this.literals.add(literal);
	}
}
