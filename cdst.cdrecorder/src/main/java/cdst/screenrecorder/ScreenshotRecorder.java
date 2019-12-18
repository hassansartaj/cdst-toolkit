package cdst.screenrecorder;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import javax.imageio.ImageIO;

/**
 * A class to capture a screenshot after a specified time period and according to different flight states.
 * 
 * @author Hassan Sartaj
 * @version 1.0
 */
public class ScreenshotRecorder extends Thread
{
	private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");	
	private static int imgCounter = 1;
	private static String imgDir;
	private static int captureTime;
	private static HashMap<String, Integer> stateDurationMap;

	public ScreenshotRecorder(String imageDir, int capTime, HashMap<String, Integer> stDurationMap) {
		imgDir = imageDir;
		captureTime = capTime;
		stateDurationMap = stDurationMap;
		makeStateDirectories();
	}

	@Override
	public void run() {
		while(true) {
			try {
				for(String state:stateDurationMap.keySet()) {
					for(int counter=1;counter<=stateDurationMap.get(state);counter++) {
						Calendar now = Calendar.getInstance();
						Robot robot = new Robot();
						BufferedImage screenShot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
						ImageIO.write(screenShot, "JPG", new File(imgDir+File.separator+state+File.separator+"Img-"+dateTimeFormat.format(now.getTime())+"_"+(imgCounter++)+".jpg"));
						Thread.sleep(captureTime*1000);
					}
				}
			} catch (AWTException | IOException | InterruptedException e) {
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
