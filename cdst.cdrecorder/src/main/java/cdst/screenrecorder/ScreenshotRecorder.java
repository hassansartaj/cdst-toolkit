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

import javax.imageio.ImageIO;

/**
 * A class to capture a screenshot after a specified time period.
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
	
	public ScreenshotRecorder(String imageDir, int capTime) {
		imgDir = imageDir;
		captureTime = capTime;
	}

	@Override
	public void run() {
		while(true) {
			try {
				Calendar now = Calendar.getInstance();
				Robot robot = new Robot();
				BufferedImage screenShot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
				ImageIO.write(screenShot, "JPG", new File(imgDir+"\\Img-"+dateTimeFormat.format(now.getTime())+"_"+(imgCounter++)+".jpg"));
				Thread.sleep(captureTime*1000);
			} catch (AWTException | IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
