/**
 * 
 */
package cdst.image.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import cdst.model.CDSModelElement;
import cdst.model.ElementVisualProperty;
import snt.oclsolver.util.Logger;

import net.sourceforge.tess4j.TesseractException;

/**
 * A class that process the images to extract the data
 * 
 * @author Hassan Sartaj
 * @version 1.0
 */
public class DataExtractor {
	public static int fpCount=0;
	/**
	 * A method that process the images from root directory, extract the data, and store in register
	 * 
	 * @param statesList
	 * @param imagesRootDir
	 * @param doTrial
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws TesseractException
	 */
	public static void extactImageData(ArrayList<CDSModelElement> cdsElements, ArrayList<String> statesList, String imagesRootDir, boolean doTrial) throws IOException, InterruptedException, TesseractException {
		fpCount=0;
		for(String state : statesList)
		{
			String imagesDir=imagesRootDir+"/"+state;
			File folder = new File(imagesDir);
			File[] listOfFiles = folder.listFiles();
			Logger.getLogger().println("Processing:: "+imagesDir+" ...");
			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					String imgFile=imagesDir+"/"+listOfFiles[i].getName();
					Logger.getLogger().println("Processing:: "+listOfFiles[i].getName()+" ...");
					ImageProcessor imgpro = new ImageProcessor(imgFile);
					for(CDSModelElement cdsElem:cdsElements) {
						String cdsElementName = cdsElem.getCdsElementName();
						String propertyName = cdsElem.getAircraftProperty();
						int x = 0, y = 0, w = 0, h = 0;
						for(ElementVisualProperty vp : cdsElem.getCdsVisualProperties()) {
							if(vp.getName().equals("PosX"))
								x = vp.getValue();
							else if(vp.getName().equals("PosY"))
								y = vp.getValue();
							else if(vp.getName().equals("SizeX"))
								w = vp.getValue();
							else if(vp.getName().equals("SizeY"))
								h = vp.getValue();
						}
						if(!(x==0 || y==0 || w==0 || h==0)) {
							int value = imgpro.getImageData(x, y, w, h);
							if(imgpro.isPredictedOk()) {
								AircraftData aData = new AircraftData(cdsElementName, propertyName, value);
								Register.getRegister().addImageData(imgFile, state, aData);
							}
							else fpCount++;
						}
					}

				} else if (listOfFiles[i].isDirectory()) {
					Logger.getLogger().println("Directory " + listOfFiles[i].getName());
				}
				if(doTrial)	break;
			}
			if(doTrial)	break;
		}
	}
	
	public static void extactImageData(ArrayList<CDSModelElement> cdsElements, String state, String imagesRootDir, boolean doTrial) throws IOException, InterruptedException, TesseractException {
		fpCount=0;		
		String imagesDir=imagesRootDir+"/"+state;
		File folder = new File(imagesDir);
		File[] listOfFiles = folder.listFiles();
		Logger.getLogger().println("Processing:: "+imagesDir+" ...");
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				String imgFile=imagesDir+"/"+listOfFiles[i].getName();
				Logger.getLogger().println("Processing:: "+listOfFiles[i].getName()+" ...");
				ImageProcessor imgpro = new ImageProcessor(imgFile);
				for(CDSModelElement cdsElem:cdsElements) {
					String cdsElementName = cdsElem.getCdsElementName();
					String propertyName = cdsElem.getAircraftProperty();
					int x = 0, y = 0, w = 0, h = 0;
					for(ElementVisualProperty vp : cdsElem.getCdsVisualProperties()) {
						if(vp.getName().equals("PosX"))
							x = vp.getValue();
						else if(vp.getName().equals("PosY"))
							y = vp.getValue();
						else if(vp.getName().equals("SizeX"))
							w = vp.getValue();
						else if(vp.getName().equals("SizeY"))
							h = vp.getValue();
					}
					if(!(x==0 || y==0 || w==0 || h==0)) {
						int value = imgpro.getImageData(x, y, w, h);
						if(imgpro.isPredictedOk()) {
							AircraftData aData = new AircraftData(cdsElementName, propertyName, value);
							Register.getRegister().addImageData(imgFile, state, aData);
						}
						else fpCount++;
					}
				}

			} else if (listOfFiles[i].isDirectory()) {
				Logger.getLogger().println("Directory " + listOfFiles[i].getName());
			}
			if(doTrial)	break;
		}

	}

	/**
	 * A method to extract data when the simulator data files are organized according to the states.
	 * 
	 * @param cdsElements
	 * @param statesList
	 * @param simDataRootDir
	 * @param doTrial
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void extactSimulatorData(ArrayList<CDSModelElement> cdsElements, ArrayList<String> statesList, String simDataRootDir, boolean doTrial) throws IOException, InterruptedException {
		for(String state : statesList)
		{
			String simDataDir=simDataRootDir+"/"+state;
			File folder = new File(simDataDir);
			File[] listOfFiles = folder.listFiles();
			Logger.getLogger().println("Processing:: "+simDataDir+" ...");
			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					String dataFile=simDataDir+"/"+listOfFiles[i].getName();
					System.out.println("File: "+dataFile);
					BufferedReader file=null;
					try {
						file=new BufferedReader(new FileReader(dataFile));
						HashMap<String, Integer> prop2IndexMap = new HashMap<>(); 
						//read file header
						String line=file.readLine();
						int dtIndex = 0;
						if(line.contains("DateTime")) {
							String []header = line.split("\t");
							for(int h=0;h<header.length;h++) {
								if(header[h].equals("DateTime"))
									dtIndex = 0;
								else
									prop2IndexMap.put(header[h], h);
							}
						}
						while((line = file.readLine()) != null) {
							String []row = line.split("\t");
							for(CDSModelElement cdsElem:cdsElements) {
								String cdsElementName = cdsElem.getCdsElementName();
								String propertyName = cdsElem.getAircraftProperty();
								if (propertyName != null) {
									String key = cdsElementName+":"+propertyName;
									if (prop2IndexMap.get(key)>=0) {
										String result = row[prop2IndexMap.get(key)];
										Double dval = 0.0;
										try {
											dval = Double.parseDouble(result);
										} catch (NumberFormatException e) {
											//									e.printStackTrace();
										}
										dval = (double) Math.round(dval);
										AircraftData aData = new AircraftData(cdsElementName, propertyName,
												dval.intValue());
										Register.getRegister().addSimulatorData(row[dtIndex], state, aData);
									}
								}
							}
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}finally {
						try {
							file.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} else if (listOfFiles[i].isDirectory()) {
					Logger.getLogger().println("Directory " + listOfFiles[i].getName());
				}
				if(doTrial)	break;
			}
			if(doTrial)	break;
		}
	}
	
	/**
	 * A method to extract data when the simulator data files are available in root directory.
	 * 
	 * @param cdsElements
	 * @param simDataRootDir
	 * @param doTrial
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void extactSimulatorData(ArrayList<CDSModelElement> cdsElements, String simDataRootDir, boolean doTrial) throws IOException, InterruptedException {
		File folder = new File(simDataRootDir);
		File[] listOfFiles = folder.listFiles();
		Logger.getLogger().println("Processing:: "+simDataRootDir+" ...");
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				String dataFile=simDataRootDir+"/"+listOfFiles[i].getName();
				System.out.println("File: "+dataFile);
				BufferedReader file=null;
				try {
					file=new BufferedReader(new FileReader(dataFile));
					HashMap<String, Integer> prop2IndexMap = new HashMap<>(); 
					//read file header
					String line=file.readLine();
					int dtIndex = 0;
					if(line.contains("DateTime")) {
						String []header = line.split("\t");
						for(int h=0;h<header.length;h++) {
							if(header[h].equals("DateTime"))
								dtIndex = 0;
							else
								prop2IndexMap.put(header[h], h);
						}
					}
					String state = null;
					while((line = file.readLine()) != null) {
						if(line.contains("State")) {
							String []srow = line.split("\t");
							state = srow[1].trim();
						}else {
							String []row = line.split("\t");
							for(CDSModelElement cdsElem:cdsElements) {
								String cdsElementName = cdsElem.getCdsElementName();
								String propertyName = cdsElem.getAircraftProperty();
								if (propertyName != null) {
									String key = cdsElementName+":"+propertyName;
									if (prop2IndexMap.get(key)>=0) {
										String result = row[prop2IndexMap.get(key)];
										Double dval = 0.0;
										try {
											dval = Double.parseDouble(result);
										} catch (NumberFormatException e) {
											//									e.printStackTrace();
										}
										dval = (double) Math.round(dval);
										AircraftData aData = new AircraftData(cdsElementName, propertyName,
												dval.intValue());
										Register.getRegister().addSimulatorData(row[dtIndex], state, aData);
									}
								}
							}
						}
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}finally {
					try {
						file.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} /*else if (listOfFiles[i].isDirectory()) {
				Logger.getLogger().println("Directory " + listOfFiles[i].getName());
			}*/
			if(doTrial)	break;
		}
		//		if(doTrial)	break;
	}
}
