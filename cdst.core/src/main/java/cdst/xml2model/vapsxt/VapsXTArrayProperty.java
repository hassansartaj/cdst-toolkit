/**
 * 
 */
package cdst.xml2model.vapsxt;

import java.util.ArrayList;

/**
 * A class that represents the array property of the model in VapsXT XML
 * 
 * @author Hassan Sartaj
 * @version 1.0
 */
public class VapsXTArrayProperty extends VapsXTProperty {
	private int capacity, size;
	private ArrayList<XYEntry> xyEntries;
	public VapsXTArrayProperty() {
	}
	/**
	 * @param capacity
	 * @param size
	 */
	public VapsXTArrayProperty(int capacity, int size) {
		super();
		this.capacity = capacity;
		this.size = size;
		this.xyEntries = new ArrayList<>(capacity);
	}

	/**
	 * @return the capacity
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * @param capacity the capacity to set
	 */
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * @return the xyEntries
	 */
	public ArrayList<XYEntry> getXyEntries() {
		return xyEntries;
	}
	
	public void createXYEntry(double x, double y) {
		XYEntry xyentry = new XYEntry(x, y);
		xyEntries.add(xyentry);
	}


	/**
	 * A class to create XY entry of the array property
	 * 
	 * @author Hassan Sartaj
	 * @version 1.0
	 */
	class XYEntry{
		private double x, y;

		/**
		 * @param x
		 * @param y
		 */
		public XYEntry(double x, double y) {
			super();
			this.x = x;
			this.y = y;
		}

		/**
		 * @return the x
		 */
		public double getX() {
			return x;
		}

		/**
		 * @param x the x to set
		 */
		public void setX(double x) {
			this.x = x;
		}

		/**
		 * @return the y
		 */
		public double getY() {
			return y;
		}

		/**
		 * @param y the y to set
		 */
		public void setY(double y) {
			this.y = y;
		}
	}
}
