/**
 * 
 */
package cdst.jsbsim.script.exec;

import java.io.File;
import java.io.IOException;

/**
 * @author Hassan Sartaj
 * @version 1.0
 */
public class JSBSimScriptExecuter {
	public static void main(String[] args) {
		String scriptsFolder = "jsbsim-scripts";
		File folder = new File(scriptsFolder);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				String scriptFile=scriptsFolder+File.separator+listOfFiles[i].getName();
				try {
					String jsbSimCmd = "./JSBSim --script="+scriptFile+"--realtime";
					Runtime.getRuntime().exec(args[0]);
					Runtime.getRuntime().exec(jsbSimCmd);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}		
	}
}
