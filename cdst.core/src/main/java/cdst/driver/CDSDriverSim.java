package cdst.driver;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import cdst.image.utils.DataExtractor;
import cdst.image.utils.Register;
import cdst.model.CDSModelElement;
import cdst.model.CDSModelLoader;
import cdst.oclevaluator.OCLEvaluator;
import cdst.utils.FaultData;
import cdst.utils.OCLLoader;
import cdst.utils.ResultsGenerator;
import cdst.utils.SimulatorData;
import cdst.xml2model.VapsXML2CDSModel;
import snt.oclsolver.tuples.ClassifierTuple;
import snt.oclsolver.util.Logger;

/**
 * A driver class to initiate the execution for the evaluation on the basis of simulator data.
 * 
 * @author Hassan Sartaj
 * @version 1.0
 */
public class CDSDriverSim {

	private static HashMap<String, String> constraints = null;
	
	public static void main(String []args) throws Exception
	{
		VapsXML2CDSModel xml2cds = new VapsXML2CDSModel();
		//load configurations
		String configFile = "src/main/resources/config/config.properties";
		InputStream input = new FileInputStream(configFile);
		Properties prop = new Properties();
		prop.load(input);
		
		String genModelDir = "model/";
		String modelPath = "model/CDSModel.uml";
		String aircraftName = prop.getProperty("aircraftName");
		String profilePath = prop.getProperty("profilePath");
		String xmlPath = prop.getProperty("cdsXmlPath");
		String mappingFile = prop.getProperty("mappingFile");
//		String imagesRootDir = prop.getProperty("imagesRootDir");
		String simDataRootDir = prop.getProperty("simDataRootDir");
		String oclFile = prop.getProperty("oclFile");
		String failedOCL = prop.getProperty("resultsDir")+"/FailedOCL.txt";
		String failedImgs = prop.getProperty("resultsDir")+"/FailedImgs.txt";
		String failReport = prop.getProperty("resultsDir")+"/FailReport.txt";
		
		//load ocl constraints from file
		constraints = OCLLoader.loadConstraints(oclFile);

		//load opencv java lib
//		OpenCVLoader.loadOpenCV();
		
		Set<String> states = constraints.keySet();
		
		//cds2xml
		xml2cds.transformXML2CDSModel(aircraftName, xmlPath, profilePath, mappingFile, genModelDir);
		
		//load cds model
		ArrayList<CDSModelElement> cdsElements = CDSModelLoader.loadCDSModel(modelPath);
		
		//read from image and store in register
		ArrayList<String> statesList = new ArrayList<>();
		statesList.addAll(states);
		boolean doTrial = false;
//		DataExtractor.extactSimulatorData(cdsElements, statesList, simDataRootDir, doTrial);
		DataExtractor.extactSimulatorData(cdsElements, simDataRootDir, doTrial);
		//setup ocl evaluator (load model as well)
		OCLEvaluator.setupOCLEvalutor(modelPath);
//		
		ArrayList<String> imgFaults = new ArrayList<String>();
		ArrayList<String>passed=new ArrayList<String>();
		ArrayList<String>failed=new ArrayList<String>();
		ArrayList<String> aircraftProperties = CDSModelLoader.getAircraftProperties(cdsElements);
		ArrayList<FaultData> faultsTable = new ArrayList<>(1);
		//begin constraints evaluation
		for(String state : states) {
			System.out.println(state);
			ArrayList<SimulatorData> values = Register.getRegister().getAllSimulatorData(state);
			FaultData faultData = new FaultData(state);
			for(SimulatorData v : values)
			{
				String stateConstraints = constraints.get(state);
				String constraintParts[]=stateConstraints.split(";");
				for(String constraint:constraintParts)
				{
					Logger.getLogger().println("["+state+"] Evaluating:: "+constraint+" ...");
					
					ArrayList<ClassifierTuple> tuples = OCLEvaluator.getInstanceModel(modelPath, constraint);
					OCLEvaluator.updateInstanceModel(tuples, v.getAircaftData());
					String res = OCLEvaluator.evaluateConstraint(tuples, constraint);
					if(!res.equals("true") && !res.equals("false"))
						res = "false";

					if(!res.equals("true"))
					{
						failed.add("["+state+"] "+constraint);
						imgFaults.add("["+state+"] "+"["+constraint+"] ["+v.getDateTime()+"]");
						for(String aProp: aircraftProperties) {
							if(constraint.contains(aProp)) {
								faultData.addPropertyFault(aProp);
							}
						}
					}
					else
					{	
						passed.add(constraint);
					}
				}
			}
			faultsTable.add(faultData);
		}
		boolean appendData = true;
		//print and write results 
		ResultsGenerator.printResults("Passed constraints", passed);
		ResultsGenerator.printResults("Failed constraints", failed);
		ResultsGenerator.printResults("Failed images", imgFaults);
		Logger.getLogger().println("Writing results to file ["+failedOCL+"]...");
		ResultsGenerator.writeToFile(failedOCL, failed, appendData);
		Logger.getLogger().println("Writing results to file ["+failedImgs+"]...");
		ResultsGenerator.writeToFile(failedImgs, imgFaults, appendData);
		Logger.getLogger().println("Writing results to file ["+failReport+"]...");
		ResultsGenerator.createFaultsTable(failReport, faultsTable,aircraftProperties, appendData);
	}
}
