/**
 * 
 */
package cdst.xml2model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cdst.xml2model.vapsxt.VapsXTArrayProperty;
import cdst.xml2model.vapsxt.VapsXTModel;
import cdst.xml2model.vapsxt.VapsXTObject;
import cdst.xml2model.vapsxt.VapsXTProperty;
import cdst.xml2model.vapsxt.VapsXTStructProperty;
import cdst.xml2model.vapsxt.VapsXTXYProperty;

/**
 * A class that loads the CDS elements from the VapsXT XML
 * 
 * @author Hassan Sartaj
 * @version 1.0
 */
public class VapsXMLLoader {

	private static String [] propertyTypes = {"prop", "xyprop", "structprop", "arrayprop"};
	/**
	 * @param args
	 */
	/*public static void main(String[] args) {
		String xmlPath = "cds-xml/pfd.xml";
		ArrayList<VapsXTObject> cdsObjects = loadCDSObjectsFromVapsXML(xmlPath);
		System.out.println(cdsObjects);
		System.out.println("CDS objects are loaded");
	}*/

	/**
	 * A method that loads objects from VapsXT XML of CDS and return them as a list
	 * 
	 * @param xml file path
	 * @return a list of VapsXT objects
	 */
	public static ArrayList<VapsXTObject> loadCDSObjectsFromVapsXML(String xmlPath) {
		ArrayList<VapsXTObject> cdsObjects = new ArrayList<VapsXTObject>(1);

		File xmlFile = new File(xmlPath);
		DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;
		try {
			documentBuilder = documentFactory.newDocumentBuilder();
			Document doc = documentBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			//get list of all CDS objects
			NodeList nodeList = doc.getElementsByTagName("object");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element obj_elem = (Element) node;
					//get properties of each CDS object
					//System.out.println("---- Object -----");
					VapsXTObject vapsObj = new VapsXTObject(obj_elem.getAttribute("name"), obj_elem.getAttribute("class"));
					//					//System.out.println("name:: "+obj_elem.getAttribute("name"));
					//					//System.out.println("class:: "+obj_elem.getAttribute("class"));
					//					if(obj_elem.getAttribute("name").equals("HeadingIndicator"))
					//						System.err.println();
					//get model inside the object
					NodeList modelList = obj_elem.getElementsByTagName("model");
					VapsXTModel vapsModel = new VapsXTModel();
					//there is one model element inside an object
					Node modelNode = modelList.item(0);
					if (modelNode.getNodeType() == Node.ELEMENT_NODE) {
						Element model_elem = (Element) modelNode;
						//get simple properties list in a model
						for(String propType:propertyTypes)
							populateModelProperties(model_elem, propType, vapsModel);
					}
					//TODO: check for nested vaps object
					vapsObj.setModel(vapsModel);
					cdsObjects.add(vapsObj);
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return cdsObjects;
	}

	/**
	 * A method that get model properties from VapsXML element and populates VapsXTModel
	 * 
	 * @param model_elem
	 * @param propType
	 * @param vapsModel
	 */
	private static void populateModelProperties(Element model_elem, String propType, VapsXTModel vapsModel) {
		NodeList propList = model_elem.getElementsByTagName(propType);
		//System.out.println("---- "+propType+" Properties -----");
		for(int i=0; i<propList.getLength(); i++) {
			Node propNode = propList.item(i);
			if (propNode.getNodeType() == Node.ELEMENT_NODE) {
				//get properties of each CDS object
				Element prop_elem = (Element) propNode;
				if(propType.equals("prop")) {
					VapsXTProperty vapsProp = new VapsXTProperty(prop_elem.getAttribute("name"),prop_elem.getTextContent());
					vapsModel.addProperty(vapsProp);
				}
				else if(propType.equals("xyprop")) {
					double posX = Double.parseDouble(prop_elem.getAttribute("x"));
					double posY = Double.parseDouble(prop_elem.getAttribute("y"));
					VapsXTXYProperty vapsXProp = new VapsXTXYProperty(posX);
					VapsXTXYProperty vapsYProp = new VapsXTXYProperty(posY);
					boolean found = false;
					if (prop_elem.getAttribute("name").equals("Position")) {
						vapsXProp.setName("PosX");
						vapsYProp.setName("PosY");
						found = true;
					}
					else if (prop_elem.getAttribute("name").equals("Size")) {
						vapsXProp.setName("SizeX");
						vapsYProp.setName("SizeY");
						found = true;
					}
					if(found) {
						vapsModel.addProperty(vapsXProp);
						vapsModel.addProperty(vapsYProp);
					}
				}
				else if(propType.equals("structprop")) {
					VapsXTStructProperty vapsProp = new VapsXTStructProperty();
					vapsProp.setName(prop_elem.getAttribute("name"));
					vapsModel.addProperty(vapsProp);
					NodeList fieldList = prop_elem.getElementsByTagName("field");
					for(int j=0; j<fieldList.getLength(); j++) {
						Node fieldNode = fieldList.item(j);
						if (fieldNode.getNodeType() == Node.ELEMENT_NODE) {
							Element field_elem = (Element) fieldNode;
							vapsProp.addField(field_elem.getAttribute("name"), field_elem.getTextContent());
						}
					}
				}
				else if(propType.equals("arrayprop")) {
					int capacity = Integer.parseInt(prop_elem.getAttribute("capacity"));
					int size = Integer.parseInt(prop_elem.getAttribute("size"));
					VapsXTArrayProperty vapsProp = new VapsXTArrayProperty(capacity, size);
					vapsProp.setName(prop_elem.getAttribute("name"));
					vapsModel.addProperty(vapsProp);
					NodeList entryList = prop_elem.getElementsByTagName("xyentry");
					for(int j=0; j<entryList.getLength(); j++) {
						Node entryNode = entryList.item(j);
						if (entryNode.getNodeType() == Node.ELEMENT_NODE) {
							Element entry_elem = (Element) entryNode;
							double x = Double.parseDouble(entry_elem.getAttribute("x"));
							double y = Double.parseDouble(entry_elem.getAttribute("y"));
							vapsProp.createXYEntry(x, y);
						}
					}
				}
				//System.out.println("name:: "+prop_elem.getAttribute("name")+" -- "+"value:: "+prop_elem.getTextContent());
			}
		}
	}
}
