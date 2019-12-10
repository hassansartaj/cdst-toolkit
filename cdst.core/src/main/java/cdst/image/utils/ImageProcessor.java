package cdst.image.utils;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;

import cdst.text.ocr.TextOCR;

/**
 * A class that performs image processing to extract data from image
 * 
 * @author Hassan Sartaj
 * @version 1.0
 */
public class ImageProcessor {
	private boolean isPredictedOk;
	private String imgFile;
	/**
	 * @return the isPredictedOk
	 */
	public boolean isPredictedOk() {
		return isPredictedOk;
	}

	public ImageProcessor(String imgFile) {
		this.imgFile = imgFile;
	}
	
	/**
	 * A method that takes coordinates of target CDS element and extract data from the given image part
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return value
	 */
	public int getImageData(int x, int y, int w, int h) {
		Mat img = Imgcodecs.imread(imgFile, Imgcodecs.IMREAD_GRAYSCALE);
		String cImage = "temp/cimg.jpg";
//		System.out.println("x="+x+" y="+y+" w="+w+" h="+h);
		cropImage(x, y, h, w, img, cImage);
		String result = TextOCR.extractTextFromImage(cImage);
		if(result.isEmpty() || result.equals("A")) {
			cropImage(x, y-25, h, w, img, cImage);
			result = TextOCR.extractTextFromImage(cImage);
		}
		result = filterText(result);
		Double dval = 0.0;
		isPredictedOk=true;
		try {
			dval = Double.parseDouble(result);
		} catch (NumberFormatException e) {
			isPredictedOk=false;
//			e.printStackTrace();
		}
		dval = (double) Math.round(dval);
    	return dval.intValue();
	}
	
	
	private void cropImage(int x, int y, int h, int w, Mat img, String cImage) {
		Rect rect_Crop = new Rect(new Point(x, y), new Point(x+w, y+h));
		Mat cImg = new Mat(img,rect_Crop);
		Imgcodecs.imwrite(cImage,cImg);
	}
	
	private String filterText(String text) {
		if(text.isEmpty()) 
			text = "0";
		if(text.trim().equals("o")) 
			text = text.replaceAll("o", "0");
		if(text.trim().equals("w")) 
			text = text.replaceAll("w", "0");
		if(text.contains("o")) 
			text = text.replaceAll("o", "0");
		if(text.contains("="))
			text = text.replaceAll("=", "e");
		if(text.contains("s"))
			text = text.replaceAll("s", "5");
		if(text.contains("S"))
			text = text.replaceAll("S", "5");
		if(text.contains("m"))
			text = text.replaceAll("m", "");
		if(text.contains("M"))
			text = text.replaceAll("M", "");
		if(text.contains(":"))
			text = text.replaceAll(":", "");
		if(text.contains("i"))
			text = text.replaceAll("i", "1");
		if(text.contains("l"))
			text = text.replaceAll("l", "1");
		return text;
	}
}