/**
 * 
 */
package cdst.xml2model.profile;

/**
 * A class that represents abstract concept of the profile
 * 
 * @author Hassan Sartaj
 * @version 1.0
 */
public abstract class ProfileConcept {
	protected String name;
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
}
