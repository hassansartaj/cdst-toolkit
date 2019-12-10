/**
 * 
 */
package cdst.xml2model.vapsxt;

import java.util.ArrayList;

/**
 * A class that represents the model of the VapsXT XML
 * @author Hassan Sartaj
 * @version 1.0
 */
public class VapsXTModel {
	private VapsXTObject nestedObject;
	private ArrayList<VapsXTProperty> properties;
	
	public VapsXTModel() {
		this.nestedObject = null;
		this.properties = new ArrayList<>(1);
	}
	
	/**
	 * @param nestedObject
	 * @param properties
	 */
	public VapsXTModel(VapsXTObject nestedObject, ArrayList<VapsXTProperty> properties) {
		super();
		this.nestedObject = nestedObject;
		this.properties = properties;
	}

	/**
	 * @return the nestedObject
	 */
	public VapsXTObject getNestedObject() {
		return nestedObject;
	}
	/**
	 * @param nestedObject the nestedObject to set
	 */
	public void setNestedObject(VapsXTObject nestedObject) {
		this.nestedObject = nestedObject;
	}

	/**
	 * @return the properties
	 */
	public ArrayList<VapsXTProperty> getProperties() {
		return properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void addProperty(VapsXTProperty property) {
		this.properties.add(property);
	}
	
	/**
	 * @param properties the properties to set
	 */
	public void setProperties(ArrayList<VapsXTProperty> properties) {
		this.properties = properties;
	}
}
