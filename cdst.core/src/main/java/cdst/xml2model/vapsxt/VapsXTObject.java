/**
 * 
 */
package cdst.xml2model.vapsxt;

/**
 * A class that represents the object of the VapsXT XML
 * 
 * @author Hassan Sartaj
 * @version 1.0
 */
public class VapsXTObject {
	private String name, type;
	private VapsXTModel model;
	/**
	 * @param name
	 * @param type
	 */
	public VapsXTObject(String name, String type) {
		super();
		this.name = name;
		this.type = type;
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
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the model
	 */
	public VapsXTModel getModel() {
		return model;
	}
	/**
	 * @param model the model to set
	 */
	public void setModel(VapsXTModel model) {
		this.model = model;
	}
	
}
