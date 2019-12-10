/**
 * 
 */
package cdst.utils;

import java.util.ArrayList;

import cdst.image.utils.AircraftData;

/**
 * A class that represents the data extracted from simulator files according to the state
 * 
 * @author Hassan Sartaj
 * @version 1.0
 */
public class SimulatorData {
	private String state;
	private String dateTime;
	private ArrayList<AircraftData> aircaftData = new ArrayList<AircraftData>();
	/**
	 * @param state
	 * @param dateTime
	 */
	public SimulatorData(String state, String dateTime) {
		super();
		this.state = state;
		this.dateTime = dateTime;
		this.aircaftData = new ArrayList<>();
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
	 * @return the dateTime
	 */
	public String getDateTime() {
		return dateTime;
	}
	/**
	 * @param dateTime the dateTime to set
	 */
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	/**
	 * @return the aircaftData
	 */
	public ArrayList<AircraftData> getAircaftData() {
		return aircaftData;
	}
	/**
	 * @param aircaftData the aircaftData to set
	 */
	public void setAircaftData(ArrayList<AircraftData> aircaftData) {
		this.aircaftData = aircaftData;
	}
	/**
	 * @param aircaftData
	 */
	public void addAircaftData(AircraftData aircaftData) {
		this.aircaftData.add(aircaftData);
	}
}
