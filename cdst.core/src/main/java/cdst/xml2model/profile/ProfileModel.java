/**
 * 
 */
package cdst.xml2model.profile;

import java.util.ArrayList;

/**
 * A class that represents the profile model including the core elements of profile 
 * such as meta classes, stereotypes, and enumerations.
 * 
 * @author Hassan Sartaj
 * @version 1.0
 */
public class ProfileModel {
	private ArrayList<String> metaClasses;
	private ArrayList<ProfileStereotype> stereotypes;
	private ArrayList<ProfileEnumeration> enumerations;
	
	public ProfileModel() {
		this.metaClasses = new ArrayList<>(1);
		this.stereotypes = new ArrayList<>(1);
		this.enumerations = new ArrayList<>(1);
	}

	/**
	 * @return the metaClasses
	 */
	public ArrayList<String> getMetaClasses() {
		return metaClasses;
	}

	/**
	 * @param metaClasses the metaClasses to set
	 */
	public void setMetaClasses(ArrayList<String> metaClasses) {
		this.metaClasses = metaClasses;
	}
	
	/**
	 * @param metaClass
	 */
	public void addMetaClass(String metaClass) {
		this.metaClasses.add(metaClass);
	}

	/**
	 * @return the stereotypes
	 */
	public ArrayList<ProfileStereotype> getStereotypes() {
		return stereotypes;
	}

	/**
	 * @param stereotypes the stereotypes to set
	 */
	public void setStereotypes(ArrayList<ProfileStereotype> stereotypes) {
		this.stereotypes = stereotypes;
	}
	
	/**
	 * @param stereotype
	 */
	public void addStereotype(ProfileStereotype stereotype) {
		this.stereotypes.add(stereotype);
	}

	/**
	 * @return the enumerations
	 */
	public ArrayList<ProfileEnumeration> getEnumerations() {
		return enumerations;
	}

	/**
	 * @param enumerations the enumerations to set
	 */
	public void setEnumerations(ArrayList<ProfileEnumeration> enumerations) {
		this.enumerations = enumerations;
	}
	
	/**
	 * @param enumeration
	 */
	public void addEnumeration(ProfileEnumeration enumeration) {
		this.enumerations.add(enumeration);
	}
}
