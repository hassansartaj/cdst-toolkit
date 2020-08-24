/**
 * 
 */
package cdst.image.utils;

/**
 * A class that represents aircraft data including CDS element name, property, and the value
 * 
 * @author Hassan Sartaj
 * @version 1.0
 */
public class AircraftData {
	private String cdsElementName;
	private String propertyName;
	private int value;
	/**
	 * @param propertyName
	 * @param value
	 */
	public AircraftData(String cdsElementName, String propertyName, int value) {
		super();
		this.cdsElementName = cdsElementName;
		this.propertyName = propertyName;
		this.value = value;
	}
	/**
	 * @return the propertyName
	 */
	public String getPropertyName() {
		return propertyName;
	}
	/**
	 * @param propertyName the propertyName to set
	 */
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
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
	/**
	 * @return the cdsElementName
	 */
	public String getCdsElementName() {
		return cdsElementName;
	}
	/**
	 * @param cdsElementName the cdsElementName to set
	 */
	public void setCdsElementName(String cdsElementName) {
		this.cdsElementName = cdsElementName;
	}
	
	@Override
	public boolean equals(Object arg) {
		if(arg instanceof AircraftData) {
			AircraftData aData = (AircraftData) arg;
			if(this.getPropertyName().equals(aData.getPropertyName()) && this.getValue() == aData.getValue()) {
				return true;
			}
		}
		return false;
	}
}
