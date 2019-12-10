/**
 * 
 */
package cdst.xml2model;

/**
 * An interface for the transformation of CDS XML to CDS model (instance of profile) in UML
 * 
 * @author Hassan Sartaj
 * @version 1.0
 */
public interface XML2CDSModel {
	
	/**
	 * A method that transforms CDS XML to CDS model
	 * 
	 * @param aircraftName
	 * @param xmlPath
	 * @param profilePath
	 * @param mappingFile
	 * @param genModelDir
	 */
	public void transformXML2CDSModel(String aircraftName, String xmlPath, String profilePath, String mappingFile, String genModelDir);
}
