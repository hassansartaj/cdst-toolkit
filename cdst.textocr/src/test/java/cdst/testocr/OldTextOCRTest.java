package cdst.testocr;
import org.junit.Test;

import cdst.text.ocr.TextOCR;

import static org.junit.Assert.*;

import org.junit.BeforeClass;

public class OldTextOCRTest {
	@BeforeClass
	public static void setTD() {
		TextOCR.tessdataDir = "tessdata/tessdata_standard";
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
		String string = TextOCR.extractTextFromImage("imgs/alt2.jpg");
		System.out.println("["+TextOCR.tessdataDir+"]OCR output:\n" + string);
		assertEquals(string, "2183.7");
	}
	
	@Test 
	public void testAlt1Value() {
		String string = TextOCR.extractTextFromImage("imgs/alt4.jpg");
		System.out.println("["+TextOCR.tessdataDir+"]OCR output:\n" + string);
		assertEquals(string, "10000");
	}
	
	@Test 
	public void testRollValue() {
		String string = TextOCR.extractTextFromImage("imgs/roll2.jpg");
		System.out.println("["+TextOCR.tessdataDir+"]OCR output:\n" + string);
		assertEquals(string, "-0.132444");
	}
	@Test 
	public void testSpeedValue() {
		String string = TextOCR.extractTextFromImage("imgs/as2.jpg");
		System.out.println("["+TextOCR.tessdataDir+"]OCR output:\n" + string);
		assertEquals(string, "160.924");
	}
}
