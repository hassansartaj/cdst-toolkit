/**
 * 
 */
package cdst.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import snt.oclsolver.util.Logger;

/**
 * A class to write evaluation results to the results file
 * 
 * @author Hassan Sartaj
 * @version 1.0
 */
public class ResultsGenerator {
	
	/**
	 * A method to write the input data to the specified file
	 * 
	 * @param fileName
	 * @param data
	 */
	public static void writeToFile(String fileName, ArrayList<String> data, boolean appendData) {
		try {
			BufferedWriter file=new BufferedWriter(new FileWriter(fileName, appendData));
			file.write("------------------------------------New Results----------------------------------\n");
			for(String d: data) {
				file.write(d);
				file.newLine();
				file.flush();
			}
			file.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * A method to write the input data to the specified file
	 * 
	 * @param fileName
	 * @param dataHeader
	 * @param data
	 * @param appendData
	 */
	public static void writeToFile(String fileName,String dataHeader, ArrayList<String> data, boolean appendData) {
		try {
			BufferedWriter file=new BufferedWriter(new FileWriter(fileName, appendData));
			file.write("------------------------------------Results for ["+dataHeader+"]----------------------------------\n");
			for(String d: data) {
				file.write(d);
				file.newLine();
				file.flush();
			}
			file.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * A method to write results (faults) table to the specified file
	 * 
	 * @param pfTab
	 * @param stateFaults
	 */
	public static void createResultsTable(String pfTab, HashMap<String, Integer> stateFaults, boolean appendData) {
		try {
			BufferedWriter file=new BufferedWriter(new FileWriter(pfTab, appendData));
			file.write("----------------------------------New Results------------------------------------\n");
			file.write("State :: Faults\n");
			Set<String> keys = stateFaults.keySet();
			Iterator<String> ki = keys.iterator();
			while(ki.hasNext()) {
				String s = (String) ki.next();
				int faults = stateFaults.get(s);
				file.write(s+" :: "+faults);
				file.newLine();
				file.flush();
			}
			file.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * A method to write results (faults) table to the specified file
	 * 
	 * @param pfTab
	 * @param faultsTable
	 * @param aircraftProperties
	 * @param appendData
	 */
	public static void createFaultsTable(String pfTab, ArrayList<FaultData> faultsTable,ArrayList<String> aircraftProperties, boolean appendData) {
		BufferedWriter file = null;
		try {
			file=new BufferedWriter(new FileWriter(pfTab, appendData));
			file.write("----------------------------------Faults Table------------------------------------\n");
			String propStr = "";
			for(String aircraftProperty: aircraftProperties)
				propStr+="\t"+aircraftProperty;
			file.write("State\tTotalFaults"+propStr);
			file.newLine();
			file.flush();
			for(FaultData fd:faultsTable) {
				String dataStr="";
				dataStr+=fd.getState()+"\t";
				dataStr+=fd.getTotalFaults();
				for(String aircraftProperty: aircraftProperties)
					dataStr+="\t"+fd.getPropertyFaults(aircraftProperty);
				file.write(dataStr);
				file.newLine();
				file.flush();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		finally {
			try {
				file.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void createFaultsTable(String pfTab, String tabHeader, ArrayList<FaultData> faultsTable,ArrayList<String> aircraftProperties, boolean appendData) {
		BufferedWriter file = null;
		try {
			file=new BufferedWriter(new FileWriter(pfTab, appendData));
			file.write("---------------------------------Faults Table for ["+tabHeader+"]---------------------------------\n");
			String propStr = "";
			for(String aircraftProperty: aircraftProperties)
				propStr+="\t"+aircraftProperty;
			file.write("State\tTotalFaults"+propStr);
			file.newLine();
			file.flush();
			for(FaultData fd:faultsTable) {
				String dataStr="";
				dataStr+=fd.getState()+"\t";
				dataStr+=fd.getTotalFaults();
				for(String aircraftProperty: aircraftProperties)
					dataStr+="\t"+fd.getPropertyFaults(aircraftProperty);
				file.write(dataStr);
				file.newLine();
				file.flush();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		finally {
			try {
				file.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * A utility method to print the results
	 * 
	 * @param title
	 * @param data
	 */
	public static void printResults(String title, ArrayList<String> data) {
		Logger.getLogger().println(title);
		for(String d:data)
		{
			Logger.getLogger().println(d);
		}
	}
}
