/**
 * 
 */
package cdst.xml2model.profile;

/**
 * A class that represents the attribute of the profile's stereotype 
 * 
 * @author Hassan Sartaj
 * @version 1.0
 */
public class StereotypeAttribute {
	public enum Type{Primitive, Enum}
	private String name;
	private String type;
	private Type abstractType;
	/**
	 * @param name
	 * @param type
	 */
	public StereotypeAttribute(String name, String type, Type abstractType) {
		super();
		this.name = name;
		this.type = type;
		this.abstractType = abstractType;
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
	 * @return the abstractType
	 */
	public Type getAbstractType() {
		return abstractType;
	}
	/**
	 * @param abstractType the abstractType to set
	 */
	public void setAbstractType(Type abstractType) {
		this.abstractType = abstractType;
	}
	
}
