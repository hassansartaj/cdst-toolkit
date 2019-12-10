/**
 * 
 */
package cdst.model;

import java.util.ArrayList;

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.LiteralInteger;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.ValueSpecification;

import cdst.model.utils.ModelLoader;

/**
 * A class that loads CDS model (profile instance) and store in the form of model elements list
 * 
 * @author Hassan Sartaj
 * @version 1.0
 */
public class CDSModelLoader {
//	public static void main(String[] args) {
//		String modelPath = "model/CDSModel.uml";
//		ArrayList<CDSModelElement> cdsElements = loadCDSModel(modelPath);
//		System.out.println(cdsElements);
//	}

	/**
	 * A method that loads CDS model and returns the list of model elements
	 * 
	 * @param modelPath
	 * @return a list of CDS model elements
	 */
	public static ArrayList<CDSModelElement> loadCDSModel(String modelPath) {
		ArrayList<CDSModelElement> cdsModelElements = new ArrayList<CDSModelElement>();
		ModelLoader modelLoader = new ModelLoader();
		String uri = null;
		try {
			uri = modelLoader.getFileURI(modelPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Object cdsModel = modelLoader.loadModel(uri);
		EList<PackageableElement> sourcePackagedElements = null;
		if (cdsModel instanceof Model) {
			Model sourceModel = (Model) cdsModel;
			sourcePackagedElements = sourceModel.getPackagedElements();
		} else if (cdsModel instanceof Package) {
			Package sourcePackage = (Package) cdsModel;
			sourcePackagedElements = sourcePackage.getPackagedElements();
		}
		for(PackageableElement element:sourcePackagedElements) {
			if(element.eClass() == UMLPackage.Literals.CLASS) {
				Class _class = (Class) element;
				CDSModelElement cdsElem = new CDSModelElement(_class.getName());
				//System.out.println(_class.getName());
				for(Property prop:_class.getOwnedAttributes()) {
					if(prop.getType() instanceof PrimitiveType) {
						//System.out.println(prop.getName());
						if(prop.getDefaultValue() == null) {
							cdsElem.setAircraftProperty(prop.getName());
						}else {
							ValueSpecification defVal = prop.getDefaultValue();
							if(defVal instanceof LiteralInteger) {
								LiteralInteger li = (LiteralInteger) defVal;
								ElementVisualProperty cdsVisualProperty = new ElementVisualProperty(prop.getName(), li.integerValue());
								cdsElem.addCdsVisualProperty(cdsVisualProperty);
							}
						}
					}
				}
				cdsModelElements.add(cdsElem);
			}
			else if(element.eClass() == UMLPackage.Literals.ENUMERATION) {
				
			}
		}
		return cdsModelElements;
	}
	
	/**
	 * A method that returns aircraft properties modeled in CDS elements
	 * 
	 * @param cdsElements
	 * @return aircraft properties
	 */
	public static ArrayList<String> getAircraftProperties(ArrayList<CDSModelElement> cdsElements) {
		ArrayList<String> aProps = new ArrayList<>();
		for(CDSModelElement cdsElem : cdsElements) {
			if(cdsElem.getAircraftProperty()!=null)
				aProps.add(cdsElem.getAircraftProperty());
		}
		return aProps;
	}
}
