/**
 * 
 */
package cdst.model;

import java.util.ArrayList;

/**
 * A class that represents CDS model elements such as name, aircraft property, and visual properties
 *  
 * @author Hassan Sartaj
 * @version 1.0
 */
public class CDSModelElement {
	private String cdsElementName;
	private String aircraftProperty;
	private ArrayList<ElementVisualProperty> cdsVisualProperties;
	
	/**
	 * @param aircraftProperties
	 */
	public CDSModelElement(String cdsElementName) {
		super();
		this.cdsElementName = cdsElementName;
		this.cdsVisualProperties = new ArrayList<ElementVisualProperty>(1);
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

	/**
	 * @return the cdsVisualProperties
	 */
	public ArrayList<ElementVisualProperty> getCdsVisualProperties() {
		return cdsVisualProperties;
	}

	/**
	 * @param cdsVisualProperties the cdsVisualProperties to set
	 */
	public void setCdsVisualProperties(ArrayList<ElementVisualProperty> cdsVisualProperties) {
		this.cdsVisualProperties = cdsVisualProperties;
	}
	
	/**
	 * @param cdsVisualProperty
	 */
	public void addCdsVisualProperty(ElementVisualProperty cdsVisualProperty) {
		this.cdsVisualProperties.add(cdsVisualProperty);
	}

	/**
	 * @return the aircraftProperty
	 */
	public String getAircraftProperty() {
		return aircraftProperty;
	}

	/**
	 * @param aircraftProperty the aircraftProperty to set
	 */
	public void setAircraftProperty(String aircraftProperty) {
		this.aircraftProperty = aircraftProperty;
	}

//	/**
//	 * @return the aircraftProperties
//	 */
//	public ArrayList<String> getAircraftProperties() {
//		return aircraftProperties;
//	}
//
//	/**
//	 * @param aircraftProperties the aircraftProperties to set
//	 */
//	public void setAircraftProperties(ArrayList<String> aircraftProperties) {
//		this.aircraftProperties = aircraftProperties;
//	}
//	
//	/**
//	 * @param aircraftProperty
//	 */
//	public void addAircraftProperty(String aircraftProperty) {
//		this.aircraftProperties.add(aircraftProperty);
//	}

}
