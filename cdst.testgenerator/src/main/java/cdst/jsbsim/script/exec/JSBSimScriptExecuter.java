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
				String scriptFile=scriptsFolder+"/"+listOfFiles[i].getName();
				try {
					Runtime.getRuntime().exec(scriptFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}		
	}
}
