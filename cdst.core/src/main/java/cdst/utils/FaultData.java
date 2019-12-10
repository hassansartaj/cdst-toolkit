/**
 * 
 */
package cdst.utils;

import java.util.ArrayList;

/**
 * A class that represents faults data corresponding to the state and properties.
 * 
 * @author Hassan Sartaj
 * @version 1.0
 */
public class FaultData {
	private String state;
	private ArrayList<AircraftPropertyFault> propertyFaults;
	private int totalFaults;
	/**
	 * @param state
	 * @param cdsElement
	 */
	public FaultData(String state) {
		super();
		this.state = state;
		this.propertyFaults = new ArrayList<>(1);
		this.totalFaults=0;
	}
	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}
	
	/**
	 * A method that returns faults count corresponding to specified aircraft property
	 * 
	 * @param aircraft property
	 * @return faults count
	 */
	public int getPropertyFaults(String aircraftProperty) {
		for(AircraftPropertyFault apf: this.propertyFaults) {
			if(apf.getAircraftProperty().equals(aircraftProperty)) {
				return apf.getFaultCount();
			}
		}
		return 0;
	}
	
	/**
	 * A method that increments faults counter corresponding to specified aircraft property
	 * 
	 * @param aircraftProperty
	 */
	public void addPropertyFault(String aircraftProperty) {
		AircraftPropertyFault propFault = null;
		for(AircraftPropertyFault apf: this.propertyFaults) {
			if(apf.getAircraftProperty().equals(aircraftProperty)) {
				propFault = apf;
				break;
			}
		}
		if(propFault!=null) {
			propFault.increaseFaultCount();
			this.totalFaults++;
		}else {
			AircraftPropertyFault apf = new AircraftPropertyFault(aircraftProperty);
			apf.increaseFaultCount();
			this.totalFaults++;
			this.propertyFaults.add(apf);
		}
	}
	/**
	 * @return the totalFaults
	 */
	public int getTotalFaults() {
		return totalFaults;
	}
	/**
	 * @param totalFaults the totalFaults to set
	 */
	public void setTotalFaults(int totalFaults) {
		this.totalFaults = totalFaults;
	}


	/**
	 * A class that represents aircraft property faults.
	 * 
	 * @author Hassan Sartaj
	 * @version 1.0
	 */
	class AircraftPropertyFault{
		private String aircraftProperty;
		private int faultCount;
		/**
		 * @param aircraftProperty
		 * @param faultCount
		 */
		public AircraftPropertyFault(String aircraftProperty) {
			super();
			this.aircraftProperty = aircraftProperty;
			this.faultCount = 0;
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
		/**
		 * @return the faultCount
		 */
		public int getFaultCount() {
			return faultCount;
		}
		
		/**
		 * A method that increments faults count
		 */
		public void increaseFaultCount() {
			this.faultCount++;
		}
	}
}
