package cdst.text.ocr;
import static org.bytedeco.javacpp.lept.pixDestroy;
import static org.bytedeco.javacpp.lept.pixRead;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.lept.PIX;
import org.bytedeco.javacpp.tesseract.TessBaseAPI;
/**
 * A class to perform text OCR of the given image
 * 
 * @author Hassan Sartaj
 * @version 1.0
 */
public class TextOCR {
	
	public static String tessdataDir="tessdata/tessdata_standard";
	/**
	 * A method that extracts text from input image using TessBaseAPI
	 * 
	 * @param image file path
	 * @return text string
	 */
	public static String extractTextFromImage(String imgFile) {
		BytePointer outText;
		@SuppressWarnings("resource")
		TessBaseAPI api = new TessBaseAPI();
		// Initialize tesseract-ocr with English, without specifying tessdata path
		if (api.Init(tessdataDir, "ENG") != 0) {
			System.err.println("Could not initialize tesseract.");
			System.exit(1);
		}

		PIX image = pixRead(imgFile);
		
		api.SetImage(image);
		// Get OCR result
		outText = api.GetUTF8Text();
		String string = outText.getString();

		// Destroy used object and release memory
		api.End();
		outText.deallocate();
		pixDestroy(image);
		return filterText(string);
	}
	
	/**
	 * A method that filter text by removing useless characters (e.g. space/end line)
	 * 
	 * @param rawTxt
	 * @return updated text
	 */
	public static String filterText(String rawTxt) {
		rawTxt = rawTxt.replaceAll(" ", "");
		rawTxt = rawTxt.replaceAll("\n", "");
		if(rawTxt.contains("*")) {
			rawTxt = rawTxt.replace("*", "\\-");
			rawTxt = rawTxt.replace("\\", "");
		}
		return rawTxt;
	}
}
