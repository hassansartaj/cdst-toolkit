/**
 * 
 */
package cdst.xml2model;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.uml.AggregationKind;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;

import cdst.model.utils.ProfileModelLoader;
import cdst.model.utils.UMLModelFactory;
import cdst.xml2model.profile.ProfileConcept;
import cdst.xml2model.profile.ProfileEnumeration;
import cdst.xml2model.profile.ProfileModel;
import cdst.xml2model.profile.ProfileStereotype;
import cdst.xml2model.profile.StereotypeAttribute;
import cdst.xml2model.profile.StereotypeAttribute.Type;
import cdst.xml2model.vapsxt.VapsXTObject;
import cdst.xml2model.vapsxt.VapsXTProperty;
import cdst.xml2model.vapsxt.VapsXTXYProperty;
import snt.oclsolver.util.Logger;

/**
 * A class that transforms the VapsXML to CDS model (instance of profile) in UML
 * 
 * @author Hassan Sartaj
 * @version 1.0
 */
public class VapsXML2CDSModel implements XML2CDSModel{

	private static HashMap<String, String> xml2profileMap = new HashMap<String, String>();

	//	public static void main(String[] args) {
//		String genModelDir = "model/";
//		String profilePath = "model/CDSProfile.profile.uml";
//		String xmlPath = "cds-xml/pfd.xml";
//		String mappingFile = "mappings/mapping.mp";
//
//		VapsXML2CDSModel obj = new VapsXML2CDSModel();
//		obj.transformXML2CDSModel("Aircraft",xmlPath, profilePath, mappingFile, genModelDir);
//	}


	/* (non-Javadoc)
	 * @see cdst.xml2model.XML2CDSModel#transformXML2CDSModel(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void transformXML2CDSModel(String aircraftName, String xmlPath, String profilePath,String mappingFile, String genModelDir) {
		loadMapping(mappingFile);
		ArrayList<VapsXTObject> cdsObjects = VapsXMLLoader.loadCDSObjectsFromVapsXML(xmlPath);
		ProfileModel cdsProfile = ProfileModelLoader.loadCDSProfile(profilePath);
		Package _package = createCDSModel(aircraftName, cdsProfile, cdsObjects);
		saveModel(_package, URI.createURI(genModelDir).appendSegment("CDSModel").appendFileExtension(UMLResource.FILE_EXTENSION));
	}
	
	/**
	 * A method that loads XML to Profile mapping from the mapping file
	 * 
	 * @param mappingFile
	 */
	private void loadMapping(String mappingFile) {
		BufferedReader file=null;
		try {
			file=new BufferedReader(new FileReader(mappingFile));
			String line = null;
			while((line = file.readLine()) != null) {
				if(line.contains("->") && !line.isEmpty() && (!line.contains("Profile") || !line.contains("XML"))) {
					String []splitValues = line.split("->");
					xml2profileMap.put(splitValues[0].trim(), splitValues[1].trim());
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				file.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * A method that creates UML model from CDS profile and CDS XML elements
	 * 
	 * @param cdsProfile
	 * @param cdsObjects
	 * @return cds model as UML Package
	 */
	private Package createCDSModel(String aircraftName, ProfileModel cdsProfile, ArrayList<VapsXTObject> cdsObjects) {
		Package _package = UMLModelFactory.createPackage("CDSPackage");
		ArrayList<String> createdEnums = new ArrayList<>(1);
		Class aircraftClass = UMLModelFactory.createClass(_package, aircraftName, false);
		for(VapsXTObject cdsObj:cdsObjects) {
			ProfileConcept concept = getCDSProfileConcept(cdsProfile,cdsObj.getName());
			if(concept instanceof ProfileStereotype) {
				ProfileStereotype pstereo = (ProfileStereotype) concept;
				Class _class = UMLModelFactory.createClass(_package, cdsObj.getName(), false);
				//create association between class1 and class2
				UMLModelFactory.createAssociation(aircraftClass, true, AggregationKind.COMPOSITE_LITERAL, _class.getName().toLowerCase(), 
						0, 1, _class, false, AggregationKind.NONE_LITERAL, aircraftClass.getName().toLowerCase(), 0, 1);
				ArrayList<VapsXTProperty> vapsModelProps = cdsObj.getModel().getProperties();
				for(StereotypeAttribute pattr: pstereo.getAttributes()) {
					if(pattr.getAbstractType() == Type.Primitive) {
						Property attribute = UMLModelFactory.createProperty(_class, pattr.getName(), UMLModelFactory.createPrimitiveType(_package, pattr.getType()), 0, 1, 0, 0);
						for(VapsXTProperty vapsProp : vapsModelProps) {
							if(vapsProp instanceof VapsXTProperty && !(vapsProp instanceof VapsXTXYProperty)) {
								if(vapsProp.getName().equals(pattr.getName())) {
									if(pattr.getType().equals("Boolean"))
										UMLModelFactory.createBooleanDefaultValue(attribute, _package, vapsProp.getValue());
									else if(pattr.getType().equals("Integer"))
										UMLModelFactory.createIntegerDefaultValue(attribute, _package, vapsProp.getValue());
									else if(pattr.getType().equals("String"))
										UMLModelFactory.createStringDefaultValue(attribute, _package, vapsProp.getValue());
								}
							}
							else if(vapsProp instanceof VapsXTXYProperty) {
								VapsXTXYProperty vapsXyProp = (VapsXTXYProperty) vapsProp;
								if(vapsXyProp.getName().equals(pattr.getName())) {
									if(pattr.getType().equals("Integer")) {
										Double dval = Math.ceil(vapsXyProp.getPosition());
										int pos = dval.intValue();
										UMLModelFactory.createIntegerDefaultValue(attribute, _package, pos);
									}
								}
							}
						}
					}
					else if(pattr.getAbstractType() == Type.Enum && !createdEnums.contains(pattr.getType())) {
						UMLModelFactory.createProperty(_class, pattr.getName(), UMLModelFactory.createEnumeration(_package, pattr.getType()), 0, 1, 0, 0);
						createdEnums.add(pattr.getType());
						Enumeration enumeration = null;
						ProfileEnumeration penum = (ProfileEnumeration) getCDSProfileConcept(cdsProfile, pattr.getType());
						for (PackageableElement element : _package.getPackagedElements()){
							if(element.eClass() == UMLPackage.Literals.ENUMERATION){
								Enumeration enm = (Enumeration) element;
								if(enm.getName().equals(penum.getName()))
								{
									enumeration = enm;
									break;
								}
							}
						}
						for(String el:penum.getLiterals()) 
							UMLModelFactory.createEnumerationLiteral(enumeration, el);
					}
				}
			}
			else if(concept instanceof ProfileEnumeration) {
				ProfileEnumeration penum = (ProfileEnumeration) concept;
				Enumeration enumeration = null;
				if(!createdEnums.contains(penum.getName()))
					enumeration = UMLModelFactory.createEnumeration(_package, penum.getName());
				else {
					for (PackageableElement element : _package.getPackagedElements()){
						if(element.eClass() == UMLPackage.Literals.ENUMERATION){
							Enumeration enm = (Enumeration) element;
							if(enm.getName().equals(penum.getName()))
							{
								enumeration = enm;
								break;
							}
						}
					}
				}
				for(String el:penum.getLiterals()) 
					UMLModelFactory.createEnumerationLiteral(enumeration, el);
			}
		}

		return _package;
	}

	/**
	 * A method that retrieves profile concept with the specified name
	 * 
	 * @param cdsProfile
	 * @param name
	 * @return ProfileConcept
	 */
	private ProfileConcept getCDSProfileConcept(ProfileModel cdsProfile,String name) {
		//search in stereotypes
		String mapName = xml2profileMap.get(name);
		for(ProfileStereotype pst: cdsProfile.getStereotypes()) {
			if(pst.getName().equals(mapName))
				return pst;
		}
		//search in enumerations
		for(ProfileEnumeration enm: cdsProfile.getEnumerations()) {
			if(enm.getName().equals(name))
				return enm;
		}
		return null;
	}

	/**
	 * A method that exports UML model to file
	 * 
	 * @param _package
	 * @param uri
	 */
	@SuppressWarnings("unchecked")
	private void saveModel(org.eclipse.uml2.uml.Package _package, URI uri) {
		Resource resource = new ResourceSetImpl().createResource(uri);
		resource.getContents().add(_package);
		try {
			resource.save(null);
			Logger.getLogger().println("Successfully Saved.");
		} catch (IOException ioe) {
			Logger.getLogger().println(ioe.getMessage());
		}
	}
}
