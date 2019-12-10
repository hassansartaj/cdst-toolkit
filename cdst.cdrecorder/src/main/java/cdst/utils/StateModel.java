/**
 * 
 */
package cdst.utils;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * A class that represents states table containing the name of state, simulation time duration, and the unit of time.
 * 
 * @author Hassan Sartaj
 * @version 1.0
 */
public class StateModel {
	private SimpleStringProperty name;
	private SimpleIntegerProperty duration;
	private SimpleStringProperty unit;
	
	public StateModel() {
	}

	/**
	 * @param name
	 * @param duration
	 * @param unit
	 */
	public StateModel(String name, Integer duration, String unit) {
		super();
		this.name = new SimpleStringProperty(name);
		this.duration = new SimpleIntegerProperty(duration);
		this.unit = new SimpleStringProperty(unit);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name.get();
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = new SimpleStringProperty(name);
	}

	/**
	 * @return the duration
	 */
	public int getDuration() {
		return duration.get();
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(int duration) {
		this.duration = new SimpleIntegerProperty(duration);
	}

	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit.get();
	}

	/**
	 * @param unit the unit to set
	 */
	public void setUnit(String unit) {
		this.unit = new SimpleStringProperty(unit);
	}
	
}
