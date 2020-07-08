package cdst.driver;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import cdst.image.utils.DataExtractor;
import cdst.image.utils.ImageData;
import cdst.image.utils.ImageProcessor;
import cdst.image.utils.Register;
import cdst.model.CDSModelElement;
import cdst.model.CDSModelLoader;
import cdst.oclevaluator.OCLEvaluator;
import cdst.text.ocr.TextOCR;
import cdst.utils.FaultData;
import cdst.utils.OCLLoader;
import cdst.utils.OpenCVLoader;
import cdst.utils.ResultsGenerator;
import cdst.xml2model.VapsXML2CDSModel;
import snt.oclsolver.tuples.ClassifierTuple;
import snt.oclsolver.util.Logger;

/**
 * A driver class to initiate the execution for the evaluation on the basis of images data.
 * 
 * @author Hassan Sartaj
 * @version 1.1
 */
public class CDSDriverExp {

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
//		String modelPath = "model/PFDModel.uml";
		String aircraftName = prop.getProperty("aircraftName");
		String profilePath = prop.getProperty("profilePath");
		String xmlPath = prop.getProperty("cdsXmlPath");
		String mappingFile = prop.getProperty("mappingFile");
		String imagesRootDir = prop.getProperty("imagesRootDir");
		//		String simDataRootDir = prop.getProperty("simDataRootDir");
		String oclFile = prop.getProperty("oclFile");
		String failedOCL = prop.getProperty("resultsDir")+"/FailedOCL.txt";
		String failedImgs = prop.getProperty("resultsDir")+"/FailedImgs.txt";
		String failReport = prop.getProperty("resultsDir")+"/FailReport.txt";
		String timeReport = prop.getProperty("resultsDir")+"/EvalTime.txt";
		ImageProcessor.MAX_THRESHOLD = Integer.parseInt(prop.getProperty("maxThreshold"));

		//load ocl constraints from file
		constraints = OCLLoader.loadConstraints(oclFile);

		//load opencv java lib
		OpenCVLoader.loadOpenCV();

		Set<String> states = constraints.keySet();

		//cds2xml
		xml2cds.transformXML2CDSModel(aircraftName, xmlPath, profilePath, mappingFile, genModelDir);

		//load cds model
		ArrayList<CDSModelElement> cdsElements = CDSModelLoader.loadCDSModel(modelPath);

		//read from image and store in register
		ArrayList<String> statesList = new ArrayList<>();
		statesList.addAll(states);
		
		//setup ocl evaluator (load model as well)
		OCLEvaluator.setupOCLEvalutor(modelPath);
		
		boolean doTrial = false;
		String[] all_tessdata = {"tessdata/tessdata_standard" /*"tessdata/tessdata_best","tessdata/tessdata_fast", "tessdata/tessdata_standard","tessdata/tessdata_old"*/};
		for(String td : all_tessdata) {
			TextOCR.tessdataDir=td;
			Logger.getLogger().println("tessdata :: "+td);
			ArrayList<String>imgFaults = new ArrayList<String>();
			ArrayList<String>passed=new ArrayList<String>();
			ArrayList<String>failed=new ArrayList<String>();
			ArrayList<String>execTime=new ArrayList<String>();
			ArrayList<String> aircraftProperties = CDSModelLoader.getAircraftProperties(cdsElements);
			ArrayList<FaultData> faultsTable = new ArrayList<>(1);
			//begin constraints evaluation
			for(String state : states) {
				DataExtractor.extactImageData(cdsElements, state, imagesRootDir, doTrial);
				ArrayList<ImageData> values = Register.getRegister().getAllImagesData(state);
				FaultData faultData = new FaultData(state);
				long startTime = System.currentTimeMillis();
				String stateConstraints = constraints.get(state);
				String constraintParts[]=stateConstraints.split(";");
				Logger.getLogger().println("[TD - "+td+"]["+state+"] Evaluating:: "+stateConstraints+" ...");
				for(ImageData v : values)
				{
					for(String constraint:constraintParts)
					{
						if(!v.getAircaftData().isEmpty()) {
							if(constraint.contains(v.getAircaftData().get(0).getPropertyName())) {
								//						Logger.getLogger().println("["+state+"] Evaluating:: "+constraint+" ...");

								ArrayList<ClassifierTuple> tuples = OCLEvaluator.getInstanceModel(modelPath, constraint);
								OCLEvaluator.updateInstanceModel(tuples, v.getAircaftData());
								String res = OCLEvaluator.evaluateConstraint(tuples, constraint);
								if(!res.equals("true") && !res.equals("false"))
									res = "false";

								if(!res.equals("true"))
								{
									failed.add("["+state+"] "+constraint);
									imgFaults.add("["+state+"] "+"["+constraint+"] ["+v.getImage()+"]");
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
					}
				}
				long endTime = System.currentTimeMillis();
				long timeDiffMillis = endTime-startTime;
				float elapsedTimeSec = timeDiffMillis/1000F;
				execTime.add(state+" \t "+elapsedTimeSec);
				faultsTable.add(faultData);
				boolean appendData = true;
				//print and write results 
				ResultsGenerator.printResults("Passed constraints", passed);
				ResultsGenerator.printResults("Failed constraints", failed);
				ResultsGenerator.printResults("Failed images", imgFaults);
				Logger.getLogger().println("Writing results to file ["+timeReport+"]...");
				ResultsGenerator.writeToFile(timeReport, (td+"["+state+"]") , execTime, appendData);
				Logger.getLogger().println("Writing results to file ["+failedOCL+"]...");
				ResultsGenerator.writeToFile(failedOCL, (td+"["+state+"]") , failed, appendData);
				Logger.getLogger().println("Writing results to file ["+failedImgs+"]...");
				ResultsGenerator.writeToFile(failedImgs, (td+"["+state+"]") , imgFaults, appendData);
				Logger.getLogger().println("Writing results to file ["+failReport+"]...");
				ResultsGenerator.createFaultsTable(failReport, (td+"["+state+"] - FPC["+DataExtractor.fpCount+"]"), faultsTable,aircraftProperties, appendData);
				
				passed.clear();failed.clear();imgFaults.clear();execTime.clear();faultsTable.clear();	
				Register.getRegister().clearAllImagesData();
			}
		}
	}
}
