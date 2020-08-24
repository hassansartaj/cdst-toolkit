/**
 * 
 */
package cdst.image.utils;

import java.util.Arrays;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
/**
 * A class that finds the similarity between two images using various computer vision techniques.
 * 
 * @author Hassan Sartaj
 * @version 1.0
 */
public class ImageComparator {

	public enum Method {HIST_CORREL, HIST_CHISQR, HIST_INTERSECT, HIST_BHATTACHARYYA, TEMPLATE_MATCH, PIXEL_MATCH}

	public static double compare_image(String img1, String img2, Method method)
	{
		Mat img_1 = Imgcodecs.imread(img1,Imgcodecs.IMREAD_COLOR);
		Mat img_2 = Imgcodecs.imread(img2,Imgcodecs.IMREAD_COLOR);
		Imgproc.cvtColor(img_1, img_1, Imgproc.COLOR_BGR2HSV);
		Imgproc.cvtColor(img_2, img_2, Imgproc.COLOR_BGR2HSV);
		double result = 0.0;
		if(method == Method.HIST_CORREL) {
			result = compare_image_hist_correl(img_1, img_2);
		}
		else if(method == Method.HIST_CHISQR) {
			result = compare_image_hist_chisqr(img_1, img_2);
		}
		else if(method == Method.HIST_INTERSECT) {
			result = compare_image_hist_intersect(img_1, img_2);
		}
		else if(method == Method.HIST_BHATTACHARYYA) {
			result = compare_image_hist_bhattacharyya(img_1, img_2);
		}
		else if(method == Method.TEMPLATE_MATCH) {
			result = compare_image_template(img_1, img_2);
		}
		else if(method == Method.PIXEL_MATCH) {
			result = compare_image_pixels(img_1, img_2);
		}
		return result;
	}

	public static double compare_image_hist_correl(Mat mat_1, Mat mat_2)
	{
		Mat hist_1 = new Mat();
		Mat hist_2 = new Mat();

		MatOfFloat ranges = new MatOfFloat(0f,256f);
		MatOfInt histSize = new MatOfInt(25);

		Imgproc.calcHist(Arrays.asList(mat_1), new MatOfInt(0), new Mat(), hist_1, histSize, ranges);
		Imgproc.calcHist(Arrays.asList(mat_2), new MatOfInt(0), new Mat(), hist_2, histSize, ranges);

		double res = Imgproc.compareHist(hist_1, hist_2, Imgproc.CV_COMP_CORREL);
		return res*100;
	}

	public static double compare_image_hist_chisqr(Mat mat_1, Mat mat_2)
	{
		Mat hist_1 = new Mat();
		Mat hist_2 = new Mat();

		MatOfFloat ranges = new MatOfFloat(0f,256f);
		MatOfInt histSize = new MatOfInt(25);

		Imgproc.calcHist(Arrays.asList(mat_1), new MatOfInt(0), new Mat(), hist_1, histSize, ranges);
		Imgproc.calcHist(Arrays.asList(mat_2), new MatOfInt(0), new Mat(), hist_2, histSize, ranges);

		double res = Imgproc.compareHist(hist_1, hist_2, Imgproc.CV_COMP_CHISQR);
		return res*100;
	}

	public static double compare_image_hist_intersect(Mat mat_1, Mat mat_2)
	{
		Mat hist_1 = new Mat();
		Mat hist_2 = new Mat();

		MatOfFloat ranges = new MatOfFloat(0f,256f);
		MatOfInt histSize = new MatOfInt(25);

		Imgproc.calcHist(Arrays.asList(mat_1), new MatOfInt(0), new Mat(), hist_1, histSize, ranges);
		Imgproc.calcHist(Arrays.asList(mat_2), new MatOfInt(0), new Mat(), hist_2, histSize, ranges);

		double res = Imgproc.compareHist(hist_1, hist_2, Imgproc.CV_COMP_INTERSECT);
		return res*100;
	}

	public static double compare_image_hist_bhattacharyya(Mat mat_1, Mat mat_2)
	{
		Mat hist_1 = new Mat();
		Mat hist_2 = new Mat();

		MatOfFloat ranges = new MatOfFloat(0f,256f);
		MatOfInt histSize = new MatOfInt(25);

		Imgproc.calcHist(Arrays.asList(mat_1), new MatOfInt(0), new Mat(), hist_1, histSize, ranges);
		Imgproc.calcHist(Arrays.asList(mat_2), new MatOfInt(0), new Mat(), hist_2, histSize, ranges);

		double res = Imgproc.compareHist(hist_1, hist_2, Imgproc.CV_COMP_BHATTACHARYYA);
		return res*100;
	}

	public static double compare_image_template(Mat mat_1, Mat mat_2)
	{
		Mat scoreImg = new Mat();
		Imgproc.matchTemplate(mat_1, mat_2, scoreImg, Imgproc.TM_CCOEFF_NORMED);
		double res = Core.minMaxLoc(scoreImg).maxVal;
		return res*100;
	}

	public static double compare_image_pixels(Mat mat_1, Mat mat_2)
	{
		if(mat_1.rows() != mat_2.rows() && mat_1.cols() != mat_2.cols())
			return 0.0;
		long diff=0;
		for(int i=0;i<mat_1.rows();i++) {
			for(int j=0;j<mat_1.cols();j++) {
				if(mat_1.get(i, j) != null && mat_2.get(i, j) != null)
					diff+=pixelDifference((int) mat_1.get(i, j)[0], (int) mat_2.get(i, j)[0]);
			}
		}
		long maxDiff = 3L * 255 * mat_1.rows() * mat_1.cols();
		double res = 100.0*diff/maxDiff;
		res = 100.0 - res*100.0;
		return res;
	}

	private static long pixelDifference(int rgb1, int rgb2) {
		int r1 = (rgb1>>16) & 0xff;
		int g1 = (rgb1>>8) & 0xff;
		int b1 = rgb1 & 0xff;
		int r2 = (rgb2>>16) & 0xff;
		int g2 = (rgb2>>8) & 0xff;
		int b2 = rgb2 & 0xff;
		return Math.abs(r1-r2)+Math.abs(g1-g2)+Math.abs(b1-b2);
	}
}

