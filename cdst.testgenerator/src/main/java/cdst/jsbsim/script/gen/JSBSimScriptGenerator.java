/**
 * 
 */
package cdst.jsbsim.script.gen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * @author Hassan Sartaj
 * @version 1.0
 */
public class JSBSimScriptGenerator {
	public static void main(String[] args) {
		String ttPath = "TransitionTree-Ex.txt";
		String scriptsGenPath = "jsbsim-scripts";
		String stateScriptsPath = "jsbsim-state-scripts";
		String scriptTemp = "script-templates";
		
		ArrayList<String> testPaths = loadTestPaths(ttPath);
		
		HashMap<String, String> scriptTempMap = loadScriptTemplates(scriptTemp);
		
		HashMap<String, String> stateScripts = loadStateScripts(stateScriptsPath);
		
		generateTestScripts(testPaths, stateScripts, scriptsGenPath, scriptTempMap);
	}

	private static HashMap<String, String> loadScriptTemplates(String scriptTemp) {
		HashMap<String, String> scriptTempMap = new HashMap<>(2);
		File folder = new File(scriptTemp);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				String dataFile=scriptTemp+File.separator+listOfFiles[i].getName();
				BufferedReader file=null;
				try {
					file=new BufferedReader(new FileReader(dataFile));
					String line=null;
					String script = "";
					while((line = file.readLine()) != null) {
						if(!line.isEmpty()) {
							script += line;
							script += "\n";
						}
					}
					scriptTempMap.put(listOfFiles[i].getName().replace(".xml", "").trim(), script);
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
			}
		}
		return scriptTempMap;
	}

	private static void generateTestScripts(ArrayList<String> testPaths, HashMap<String, String> stateScripts,
			String scriptsGenPath, HashMap<String, String> scriptTempMap) {
		String scriptName = "test-script-";
		int sCount = 1;
		for(String tp:testPaths) {
			String []splitTP = tp.split(",");
			String fileName=scriptsGenPath+File.separator+scriptName+sCount+".xml";
			String initalScript = scriptTempMap.get("ScriptInitialPart");
			writeToFile(fileName, initalScript, true);
			for(String sTp : splitTP) {
				if(sTp.contains("{")) {
					String state = sTp.substring(sTp.indexOf("{")+1, sTp.indexOf("}")-1).trim();
					if(state.contains(":")) {
						String []parts = state.split(":");
						state = parts[0];
					}
					String stateScript = stateScripts.get(state);
					if(stateScript != null)
						writeToFile(fileName, stateScript, true);
					else 
						System.out.println(state);
				}
			}
			String finalScript = scriptTempMap.get("ScriptFinalPart");
			writeToFile(fileName, finalScript, true);
			sCount++;
		}
	}

	private static HashMap<String, String> loadStateScripts(String stateScriptsPath) {
		HashMap<String, String> stateScripts = new HashMap<>();
		File folder = new File(stateScriptsPath);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				String dataFile=stateScriptsPath+File.separator+listOfFiles[i].getName();
				BufferedReader file=null;
				try {
					file=new BufferedReader(new FileReader(dataFile));
					String line=null;
					String script = "";
					while((line = file.readLine()) != null) {
						if(!line.isEmpty()) {
							script += line;
							script += "\n";
						}
					}
					stateScripts.put(listOfFiles[i].getName().replace(".xml", "").trim(), script);
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
			}
		}
		return stateScripts;
	}

	private static ArrayList<String> loadTestPaths(String ttPath) {
		ArrayList<String> testPaths = new ArrayList<>(1);
		BufferedReader file=null;
		try {
			file=new BufferedReader(new FileReader(ttPath));
			String line=null;
			while((line = file.readLine()) != null) {
				if(!line.isEmpty()) {
					testPaths.add(line);
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
		return testPaths;
	}
	
	private static void writeToFile(String fileName, String data, boolean appendData) {
		try {
			BufferedWriter file=new BufferedWriter(new FileWriter(fileName, appendData));
			file.write(data);
			file.newLine();
			file.flush();
			file.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
