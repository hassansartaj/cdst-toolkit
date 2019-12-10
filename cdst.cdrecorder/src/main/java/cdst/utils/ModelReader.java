package cdst.utils;

import java.util.ArrayList;

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.Behavior;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Region;
import org.eclipse.uml2.uml.StateMachine;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.Vertex;

public class ModelReader {
	
//	public static void main(String[] args) {
//		getStates("model/Plane.uml");
//	}
	
	public static ArrayList<String> getStates(String umlFilePath) {		
		ArrayList<String> states = new ArrayList<String>();
		
		ModelLoader umlModel = new ModelLoader();
		String uri = null;
		try {
			uri = umlModel.getFileURI(umlFilePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Object objModel = umlModel.loadModel(uri);
		Model sourceModel;
		EList<PackageableElement> sourcePackagedElements = null;
		if (objModel instanceof Model) {
			sourceModel = (Model) objModel;
			sourcePackagedElements = sourceModel.getPackagedElements();
		} else if (objModel instanceof Package) {
			Package sourcePackage = (Package) objModel;
			sourcePackagedElements = sourcePackage.getPackagedElements();
		}
		
		for (PackageableElement element : sourcePackagedElements){
			//for nested package
			if(element.eClass() == UMLPackage.Literals.PACKAGE){
				org.eclipse.uml2.uml.Package nestedPackage = (org.eclipse.uml2.uml.Package) element;
				EList<PackageableElement> nestedPackagedElements = nestedPackage.getPackagedElements();
				for (PackageableElement nestedElement : nestedPackagedElements){
					populateStates(nestedElement, states);
				}
			}
			else
				populateStates(element, states);
		}
		
		
		return states;
	}

	private static void populateStates(PackageableElement element, ArrayList<String> states){
		
		if (element.eClass() == UMLPackage.Literals.CLASS)
		{
			Class clas = (Class)element;
			//get class name
			EList<Behavior> behaviors = clas.getOwnedBehaviors();
			//System.out.println("behaviors: ");
			for(Behavior beh: behaviors) {
				if (beh instanceof StateMachine) {
					StateMachine sm = (StateMachine) beh;
					//System.out.println("SM: "+ sm.getName());
					EList<Region> regions = sm.getRegions();
					for (Region reg : regions) {
						extractNestedStates(reg, states);
					}
				}
			}
		}
	}

	private static void extractNestedStates(Region reg, ArrayList<String> states){
		if(reg != null) {
			EList<Vertex> vertics = reg.getSubvertices();
			for(Vertex ver: vertics) {
				if(ver.eClass() == UMLPackage.Literals.STATE) {
					states.add(ver.getName());
					EList<Element> elems = ver.getOwnedElements();
					for(Element elem:elems) {
						if (elem instanceof Region) {
							extractNestedStates((Region) elem,states);
						}
					}
				}
			}
		}
	}
}
