/**
 * 
 */
package cdst.xml2model.vapsxt;

/**
 * A class that represents XY property of the model in VapsXT XML
 * 
 * @author Hassan Sartaj
 * @version 1.1
 */
public class VapsXTXYProperty extends VapsXTProperty{
	private double position;

	public VapsXTXYProperty() {
	}
	/**
	 * @param pos
	 */
	public VapsXTXYProperty(double pos) {
		super();
		this.position = pos;
	}
	
	/**
	 * @return the position
	 */
	public double getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(double pos) {
		this.position = pos;
	}

}
