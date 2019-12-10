package cdst.modelutils;

import java.util.ArrayList;

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.Behavior;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Region;
import org.eclipse.uml2.uml.StateMachine;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.Vertex;
import org.eclipse.uml2.uml.internal.impl.EnumerationImpl;
import org.eclipse.uml2.uml.internal.impl.PrimitiveTypeImpl;
public class ModelReader {
	
//	private static InstanceGeneratorUtil instanceGenerator = new InstanceGeneratorUtil();;
	
	public static Class clas_1 = null;
	
//	public static void main(String[] args) {
//		ArrayList<ClassInfo> classesInfo = getClassDetails("model/Plane.uml");
//		//System.out.println(classesInfo);
//	}
	public static ArrayList<ClassInfo> getClassDetails(String umlFilePath) {
//		instanceGenerator.setUp();
		
		ArrayList<ClassInfo> classesInfo = new ArrayList<>(1);
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
					populateClassInfo(nestedElement, classesInfo);
				}
			}
			else
				populateClassInfo(element, classesInfo);
		}
		
		
		return classesInfo;
	}

	private static void populateClassInfo(PackageableElement element, ArrayList<ClassInfo> classesInfo){
		
		if (element.eClass() == UMLPackage.Literals.CLASS)
		{
			ClassInfo cinfo = new ClassInfo();
			Class clas = (Class)element;
			clas_1 = clas;
			//get class name
//			String clasName = clas.getName();
			cinfo.setClassName(clas.getName());
			//System.out.println("Class Name: "+clas.getName());
			//get class attributes
			EList<Property> attributes = clas.getOwnedAttributes();				
			//System.out.println("Attributes: ");
			if(!attributes.isEmpty()){
				for (Property attr: attributes)
				{
					if (attr.getType() instanceof PrimitiveTypeImpl) {
						//System.out.println(attr.getName());
						PrimitiveTypeImpl pt = (PrimitiveTypeImpl) attr.getType();
						//					pt.eProxyURI().fragment();
						//System.out.println(pt.eProxyURI().fragment());
						cinfo.addAttributeDT(attr.getName(), pt.eProxyURI().fragment());
					}
					else if (attr.getType() instanceof EnumerationImpl) {
						//System.out.println(attr.getName());
						EnumerationImpl enm = (EnumerationImpl) attr.getType();
						ArrayList<String> literals = new ArrayList<>(1);
						for(EnumerationLiteral el:enm.getOwnedLiterals()) {
							literals.add(el.getName());
						}
						cinfo.addEnumLiterals(enm.getName(), literals);
						cinfo.addAttributeDT(attr.getName(), enm.getName());
					}
				}
			}
			EList<Behavior> behaviors = clas.getOwnedBehaviors();
			//System.out.println("behaviors: ");
			for(Behavior beh: behaviors) {
				if (beh instanceof StateMachine) {
					StateMachine sm = (StateMachine) beh;
					//System.out.println("SM: "+ sm.getName());
					EList<Region> regions = sm.getRegions();
					for (Region reg : regions) {
						extractNestedStates(reg, cinfo);
					}
				}
			}
			classesInfo.add(cinfo);
		}
	}

	private static void extractNestedStates(Region reg, ClassInfo cinfo){
		if(reg != null) {
//			//System.out.println("Reg: "+reg.getName());
			EList<Vertex> vertics = reg.getSubvertices();
			for(Vertex ver: vertics) {
				if(ver.eClass() == UMLPackage.Literals.STATE) {
					//System.out.println("state: "+ver.getName());
					cinfo.addState(ver.getName());
					EList<Element> elems = ver.getOwnedElements();
					for(Element elem:elems) {
						if (elem instanceof Region) {
							extractNestedStates((Region) elem,cinfo);
						}
					}
				}
			}
		}
	}
	

	public static void main(String[] args) {
		ModelLoader umlModel = new ModelLoader();
		String umlFilePath = "model/Plane2.uml";
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
		ArrayList<ClassInfo> classesInfo = new ArrayList<>();
		for (PackageableElement element : sourcePackagedElements){
			//for nested package
			if(element.eClass() == UMLPackage.Literals.PACKAGE){
				org.eclipse.uml2.uml.Package nestedPackage = (org.eclipse.uml2.uml.Package) element;
				EList<PackageableElement> nestedPackagedElements = nestedPackage.getPackagedElements();
				for (PackageableElement nestedElement : nestedPackagedElements){
					populateClassInfo(nestedElement,classesInfo);
				}
			}
			else
				populateClassInfo(element, classesInfo);
		}
	}
}
