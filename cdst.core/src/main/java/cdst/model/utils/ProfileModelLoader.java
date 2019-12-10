/**
 * 
 */
package cdst.model.utils;

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Extension;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.internal.impl.EnumerationImpl;
import org.eclipse.uml2.uml.internal.impl.PrimitiveTypeImpl;

import cdst.xml2model.profile.ProfileEnumeration;
import cdst.xml2model.profile.ProfileModel;
import cdst.xml2model.profile.ProfileStereotype;
import cdst.xml2model.profile.StereotypeAttribute.Type;

/**
 * A class that loads the CDS profile
 * 
 * @author Hassan Sartaj
 * @version 1.0
 */
public class ProfileModelLoader {
	/**
	 * @param args
	 */
	/*public static void main(String[] args) {
		String profilePath = "model/CDSProfile.profile.uml";
		ProfileModel cdsProfile = loadCDSProfile(profilePath);
		System.out.println("CDS profile is loaded"+cdsProfile);
	}*/

	/**
	 * A method that load CDS profile from file and retutn as ProfileModel 
	 * 
	 * @param profile path
	 * @return profile stored as ProfileModel
	 */
	public static ProfileModel loadCDSProfile(String profilePath) {
		ProfileModel cdsProfile = new ProfileModel();
		ModelLoader modelLoader = new ModelLoader();
		String uri = null;
		try {
			uri = modelLoader.getFileURI(profilePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Profile profile = modelLoader.loadProfile(uri);
		EList<PackageableElement> profileElements = profile.getPackagedElements();
		for (PackageableElement element : profileElements){
			if(element.eClass() == UMLPackage.Literals.STEREOTYPE){
				Stereotype st = (Stereotype) element;
				ProfileStereotype pstereo = new ProfileStereotype(st.getName());
				//System.out.println("[st] "+st.getName());
				for(Property prop : st.getAllAttributes()) {
					if(!(prop.getType() instanceof Class)) {
						String type = null;
						if(prop.getType() instanceof PrimitiveTypeImpl) {
							PrimitiveTypeImpl pt = (PrimitiveTypeImpl) prop.getType();
							type = pt.eProxyURI().fragment();
							pstereo.addAttribute(prop.getName(), type, Type.Primitive);
						}
						else if(prop.getType() instanceof EnumerationImpl) {
							EnumerationImpl pt = (EnumerationImpl) prop.getType();
							type = pt.getName();
							pstereo.addAttribute(prop.getName(), type, Type.Enum);
						}
					}
				}
				cdsProfile.addStereotype(pstereo);
			}
			else if(element.eClass() == UMLPackage.Literals.ENUMERATION){
				Enumeration enm = (Enumeration) element;
				//System.out.println("[em] "+enm.getName());
				ProfileEnumeration penum = new ProfileEnumeration(enm.getName());
				for(EnumerationLiteral el:enm.getOwnedLiterals()) {
					penum.addLiteral(el.getName());
					//System.out.println(el.getName());
				}
				cdsProfile.addEnumeration(penum);
			}
			else if(element.eClass() == UMLPackage.Literals.EXTENSION){
				Extension ext = (Extension) element;
				//System.out.println("[ext] "+ext.getName());
				cdsProfile.addMetaClass(ext.getName());
			}
		}
		return cdsProfile;
	}

}
