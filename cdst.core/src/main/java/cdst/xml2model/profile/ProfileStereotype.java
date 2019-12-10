/**
 * 
 */
package cdst.xml2model.profile;

import java.util.ArrayList;
import cdst.xml2model.profile.StereotypeAttribute.Type;
/**
 * A class that represents the stereotype of the profile 
 * 
 * @author Hassan Sartaj
 * @version 1.0
 */
public class ProfileStereotype extends ProfileConcept{
	private ArrayList<StereotypeAttribute> attributes;
	
	public ProfileStereotype() {
		this.attributes = new ArrayList<>(1);
	}

	/**
	 * @param name
	 */
	public ProfileStereotype(String name) {
		super();
		this.name = name;
		this.attributes = new ArrayList<>(1);
	}

	/**
	 * @return the attributes
	 */
	public ArrayList<StereotypeAttribute> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(ArrayList<StereotypeAttribute> attributes) {
		this.attributes = attributes;
	}

	/**
	 * @param name
	 * @param type
	 * @param abstractType
	 */
	public void addAttribute(String name, String type, Type abstractType) {
		StereotypeAttribute attr = new StereotypeAttribute(name, type, abstractType);
		this.attributes.add(attr);
	}
}
