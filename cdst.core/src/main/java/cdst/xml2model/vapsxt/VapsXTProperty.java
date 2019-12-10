/**
 * 
 */
package cdst.xml2model.vapsxt;

/**
 * A class that represents the simple property of the model in VapsXT XML
 * 
 * @author Hassan Sartaj
 * @version 1.0
 */
public class VapsXTProperty {
	protected String name;
	private String value;
	
	public VapsXTProperty() {
		this.name = null;
		this.value = null;
	}
	/**
	 * @param name
	 * @param value
	 */
	public VapsXTProperty(String name, String value) {
		super();
		this.name = name;
		this.value = value;
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
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
}
