package cdst.image.utils;
import java.util.ArrayList;

import cdst.utils.SimulatorData;

/**
 * A singleton class to store the extracted image and simulator data
 */
public class Register {
	public static Register register=null;
	private ArrayList<ImageData> imageData = new ArrayList<ImageData>();
	private ArrayList<SimulatorData> simulatorData = new ArrayList<SimulatorData>();
	/**
	 * @return one instance of Register
	 */
	public static Register getRegister()
	{
		if(register==null)
		{
			register= new Register();
		}
		return register;
	}
	
	/**
	 * A method to store the extracted image data corresponding to the state
	 * 
	 * @param image
	 * @param state
	 * @param data
	 */
	public void addImageData(String image, String state, AircraftData data) {
		ImageData imgData = new ImageData(state, image);
		imgData.addAircaftData(data);;
		imageData.add(imgData);
	}
	
	/**
	 * A method that returns the image data from register corresponding to the given state
	 * 
	 * @param state
	 * @return ArrayList<ImageData>
	 */
	public ArrayList<ImageData> getAllImagesData(String state) {
		ArrayList<ImageData> data=new ArrayList<ImageData>();
		for(ImageData d : imageData)
		{
			if(d.getState().equals(state))
			{
				data.add(d);
			}
		}
		return data;
	}
	
	/**
	 * A method to clear all images data
	 */
	public void clearAllImagesData() {
		imageData.clear();
	}
	
	/**
	 * A method to store the extracted simulator data corresponding to the state and date time
	 * 
	 * @param dateTime
	 * @param state
	 * @param data
	 */
	public void addSimulatorData(String dateTime, String state, AircraftData data) {
		SimulatorData simData = new SimulatorData(state, dateTime);
		simData.addAircaftData(data);
		simulatorData.add(simData);
	}
	
	/**
	 * A method that returns the image data from register corresponding to the given state
	 * 
	 * @param state
	 * @return ArrayList<ImageData>
	 */
	public ArrayList<SimulatorData> getAllSimulatorData(String state) {
		ArrayList<SimulatorData> data=new ArrayList<SimulatorData>();
		for(SimulatorData d : simulatorData)
		{
			if(d.getState().equals(state))
			{
				data.add(d);
			}
		}
		return data;
	}
}
