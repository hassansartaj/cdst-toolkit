package cdst.testocr;
import org.junit.Test;

import cdst.text.ocr.TextOCR;

import static org.junit.Assert.*;

import org.junit.BeforeClass;

public class FastTextOCRTest {
	@BeforeClass
	public static void setTD() {
		TextOCR.tessdataDir = "tessdata/tessdata_fast";
	}
	@Test 
	public void testAltNotEmpty() {
		String string = TextOCR.extractTextFromImage("imgs/alt.jpg");
		assertTrue(!string.isEmpty());
	}
	@Test 
	public void testRollNotEmpty() {
		String string = TextOCR.extractTextFromImage("imgs/roll.jpg");
		assertTrue(!string.isEmpty());
	}
	@Test 
	public void testSpeedNotEmpty() {
		String string = TextOCR.extractTextFromImage("imgs/speed.jpg");
		assertTrue(!string.isEmpty());
	}
	
	@Test 
	public void testAltValue() {
		String string = TextOCR.extractTextFromImage("imgs/alt.jpg");
		System.out.println("["+TextOCR.tessdataDir+"]OCR output:\n" + string);
		assertEquals(string, "2270.5");
	}
	@Test 
	public void testRollValue() {
		String string = TextOCR.extractTextFromImage("imgs/roll.jpg");
		System.out.println("["+TextOCR.tessdataDir+"]OCR output:\n" + string);
		assertEquals(string, "-0.0408932");
	}
	@Test 
	public void testSpeedValue() {
		String string = TextOCR.extractTextFromImage("imgs/speed.jpg");
		System.out.println("["+TextOCR.tessdataDir+"]OCR output:\n" + string);
		assertEquals(string, "184.134");
	}
}
