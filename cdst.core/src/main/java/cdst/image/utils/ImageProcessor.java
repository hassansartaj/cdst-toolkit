package cdst.image.utils;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import cdst.text.ocr.TextOCR;

/**
 * A class that performs image processing to extract data from image. 
 * 
 * @author Hassan Sartaj
 * @version 1.0
 */
public class ImageProcessor {
	private boolean isPredictedOk;
	private String imgFile;
	public static int MAX_THRESHOLD = Integer.MAX_VALUE;
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
		cropAndTransformImage(x, y, h, w, img, cImage);
		String result = TextOCR.extractTextFromImage(cImage);
		if(result.isEmpty() || result.equals("A")) {
			cropAndTransformImage(x, y-25, h, w, img, cImage);
			result = TextOCR.extractTextFromImage(cImage);
		}
		result = filterText(result);
		Double dval = 0.0;
		isPredictedOk=true;
		try {
			dval = Double.parseDouble(result);
			if(dval>MAX_THRESHOLD) 
				isPredictedOk=false;
		} catch (NumberFormatException e) {
			isPredictedOk=false;
			//			e.printStackTrace();
		}
		dval = (double) Math.round(dval);
		return dval.intValue();
	}


	private void cropAndTransformImage(int x, int y, int h, int w, Mat img, String cImage) {
		Rect rect_Crop = new Rect(new Point(x, y), new Point(x+w, y+h));
		Mat cImg = new Mat(img,rect_Crop);
		Mat dstImg = new Mat();
		//scale the image to 4x
		Imgproc.resize(cImg, dstImg, new Size(cImg.cols(), cImg.rows()), 4, 4, Imgproc.INTER_CUBIC);
		//convert image into only two colors and invert colors
		Imgproc.threshold(dstImg, dstImg, 70,255, Imgproc.THRESH_BINARY_INV);
		//dilate the pixels to make them smooth to some extent
		Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(13,13));
		Mat dilated = new Mat();
		Imgproc.dilate(dstImg, dilated, kernel);
		//		Imgproc.GaussianBlur(dstImg, dstImg, new Size(5,5),Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C);
		//		Imgproc.resize(dstImg,dstImg, new Size(0,0), 0.4, 0.4, Imgproc.INTER_CUBIC);
		Imgcodecs.imwrite(cImage,dstImg);
		//		Imgcodecs.imwrite(cImage,cImg);
	}

	private String filterText(String text) {
		if(text.isEmpty()) 
			text = "0";
		if(text.trim().equals("o")) 
			text = text.replaceAll("o", "0");
		if(text.trim().equals("w")) 
			text = text.replaceAll("w", "0");

		text.replaceAll("l", "1").replaceAll("Z", "2").replaceAll("O", "0")
		.replaceAll("B", "8").replaceAll("G", "6").replaceAll("S", "5").replaceAll("'", "")
		.replaceAll("‘", "").replaceAll("E", "8").replaceAll("o", "0").replaceAll("i", "1")
		.replaceAll("ﬂ", "0").replaceAll("ﬁ", "6").replaceAll("§", "5").replaceAll("I", "1")
		.replaceAll("T", "7").replaceAll("’", "").replaceAll("U", "0").replaceAll("D", "0")
		.replaceAll("=", "e").replaceAll("s", "5").replaceAll("m", "").replaceAll("M", "");

		//		if(text.contains("o")) 
		//			text = text.replaceAll("o", "0");
		//		if(text.contains("="))
		//			text = text.replaceAll("=", "e");
		//		if(text.contains("s"))
		//			text = text.replaceAll("s", "5");
		//		if(text.contains("S"))
		//			text = text.replaceAll("S", "5");
		//		if(text.contains("m"))
		//			text = text.replaceAll("m", "");
		//		if(text.contains("M"))
		//			text = text.replaceAll("M", "");
		//		if(text.contains(":"))
		//			text = text.replaceAll(":", "");
		//		if(text.contains("i"))
		//			text = text.replaceAll("i", "1");
		//		if(text.contains("l"))
		//			text = text.replaceAll("l", "1");
		return text;
	}
}