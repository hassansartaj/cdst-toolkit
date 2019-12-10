/**
 * 
 */
package cdst.camrecorder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
	
	static{
		OpenCVLoader.loadOpenCV();
	}

    public CameraRecorder(String imageDir, int capTime){
        cap = new VideoCapture();
        cap.open(0);
    }
    
    private BufferedImage getImageFrame() {
    	cap.read(mat2Img.mat);
        return mat2Img.getImage(mat2Img.mat);
	}
    
    @Override
	public void run() {
		while(true) {
			try {
				Calendar now = Calendar.getInstance();
				ImageIO.write(getImageFrame(), "JPG", new File(imgDir+"\\Img-"+dateTimeFormat.format(now.getTime())+"_"+(imgCounter++)+".jpg"));
				Thread.sleep(captureTime*1000);
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
