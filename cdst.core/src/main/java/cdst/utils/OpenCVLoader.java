/**
 * 
 */
package cdst.utils;

import java.io.File;

import snt.oclsolver.util.Logger;

/**
 * A class that loads OpenCV library according to the operating system and the architecture
 * 
 * @author Hassan Sartaj
 * @version 1.0
 */
public class OpenCVLoader {

	/**
	 * @param args
	 */
//	public static void main(String[] args) {
//		loadOpenCV();
//	}

	/**
	 * A method that loads OpenCV library w.r.t to OS and the architecture
	 */
	public static void loadOpenCV() {
		String osName = System.getProperty("os.name").toLowerCase();
		String fileName = null;
		if(osName.indexOf("win")>=0) {
			String osArch = System.getProperty("os.arch");
			//for x64 architecture
			if(osArch.indexOf("64")>=0) {
				fileName = "src/main/resources/opencv/win/x64/opencv_java341.dll";
			} //for x86 architecture
			else if(osArch.indexOf("86")>=0) {
				fileName = "src/main/resources/opencv/win/x86/opencv_java341.dll";
			}
		}
		else if(osName.indexOf("mac")>=0) {
			fileName = "/src/main/resources/opencv/mac/libopencv_java341.dylib";
		}
		//TODO: add support for linux/ubuntu
		else if(osName.indexOf("nix") >= 0 || osName.indexOf("nux") >= 0 || osName.indexOf("aix") > 0 || osName.indexOf("sunos") >= 0) {
			
		}
		
		File file = new File(fileName);
		Logger.getLogger().println("Loading opencv library from:: "+file.getAbsolutePath());
		System.load(file.getAbsolutePath());
	}
}
