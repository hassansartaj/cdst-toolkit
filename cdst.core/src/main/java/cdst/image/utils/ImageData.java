/**
 * 
 */
package cdst.image.utils;

import java.util.ArrayList;

/**
 * A class to represent the data extracted from image according to the state
 * 
 * @author Hassan Sartaj
 * @version 1.1
 */
public class ImageData {
	private String state;
	private String image;
	private ArrayList<AircraftData> aircaftData = new ArrayList<AircraftData>();
	/**
	 * @param state
	 * @param image
	 */
	public ImageData(String state, String image) {
		super();
		this.state = state;
		this.image = image;
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
	 * @return the image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(String image) {
		this.image = image;
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
	
	public boolean isDuplicateAircraftData(ArrayList<AircraftData> prevAircaftData) {
		int size = prevAircaftData.size();
		int count=0;
		for(AircraftData aData:this.aircaftData) {
			for(AircraftData preData:prevAircaftData) {
				if(aData.equals(preData)) {
					count++;
				}
			}
		}
		if(count == size)
			return true;
		return false;
	}
}
