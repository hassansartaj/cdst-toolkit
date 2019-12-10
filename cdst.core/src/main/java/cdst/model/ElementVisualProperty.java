/**
 * 
 */
package cdst.model;

/**
 * A class that represents visual property of CDS element
 * 
 * @author Hassan Sartaj
 * @version 1.0
 */
public class ElementVisualProperty {
	private String name;
	private int value;
	/**
	 * @param name
	 * @param value
	 */
	public ElementVisualProperty(String name, int value) {
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
	public int getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}
}
