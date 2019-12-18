/**
 * 
 */
package cdst.camrecorder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.opencv.videoio.VideoCapture;

/**
 * A class to record images from camera after a specified time period.
 * 
 * @author Hassan Sartaj
 * @version 1.0
 */
public class CameraRecorder extends Thread{

	private VideoCapture cap;
	private Mat2Image mat2Img = new Mat2Image();

	private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");	
	private int imgCounter = 1;
	private String imgDir;
	private int captureTime;
	private static HashMap<String, Integer> stateDurationMap;

	static{
		OpenCVLoader.loadOpenCV();
	}

	public CameraRecorder(String imageDir, int capTime, HashMap<String, Integer> stDurationMap){
		cap = new VideoCapture();
		cap.open(0);
		stateDurationMap = stDurationMap;
		makeStateDirectories();
	}

	private BufferedImage getImageFrame() {
		cap.read(mat2Img.mat);
		return mat2Img.getImage(mat2Img.mat);
	}

	@Override
	public void run() {
		while(true) {
			try {
				for(String state:stateDurationMap.keySet()) {
					for(int counter=1;counter<=stateDurationMap.get(state);counter++) {
						Calendar now = Calendar.getInstance();
						ImageIO.write(getImageFrame(), "JPG", new File(imgDir+File.separator+state+File.separator+"Img-"+dateTimeFormat.format(now.getTime())+"_"+(imgCounter++)+".jpg"));
						Thread.sleep(captureTime*1000);
					}
				}
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void makeStateDirectories() {
		for(String state:stateDurationMap.keySet()) {
			File file = new File(imgDir+File.separator+state);
			if (!file.exists()) {
				boolean isCreated = file.mkdir();
				if (!isCreated)
					System.out.println("Cannot create directory for " + state);
			}

		}
	}
}
