package cdst.validation;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.InstanceSpecification;
import org.eclipse.uml2.uml.InstanceValue;
import org.eclipse.uml2.uml.LiteralBoolean;
import org.eclipse.uml2.uml.LiteralInteger;
import org.eclipse.uml2.uml.LiteralNull;
import org.eclipse.uml2.uml.LiteralString;
import org.eclipse.uml2.uml.LiteralUnlimitedNatural;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Slot;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.ValueSpecification;

public  class InstanceGeneratorUtil <E extends EObject, PK extends E, T extends E, C extends T, 
CLS extends C, DT extends C, PT extends C, ET extends DT, O extends E, PM extends E, P extends E, 
PA extends P, PR extends P, EL, S, COA, SSA, CT>
extends OCLEvaluatorUtil<E, PK, T, C, CLS, DT, PT, ET, O, PM, P, PA, PR, EL, S, COA, SSA, CT> {
	
	
	public InstanceSpecification instantiate(Package pkg, Classifier classifier, String instanceName) {
		InstanceSpecification result = (InstanceSpecification) pkg.createPackagedElement(
				null, UMLPackage.eINSTANCE.getInstanceSpecification());
		
		if (classifier != null) {
			result.getClassifiers().add(classifier);
		}
		result.setName(instanceName);
		return result;
	}
	
	
	public InstanceSpecification instantiate(Package pkg, Classifier classifier) {
		InstanceSpecification result = (InstanceSpecification) pkg.createPackagedElement(
				null, UMLPackage.eINSTANCE.getInstanceSpecification());
		
		if (classifier != null) {
			result.getClassifiers().add(classifier);
		}
		
		return result;
	}
	
	public Slot setValue(
			InstanceSpecification instance,
			Property property,
			Object value) {
		
		Slot result = null;
		for (Slot slot : instance.getSlots()) {
			if (slot.getDefiningFeature() == property) {
				result = slot;
				slot.getValues().clear();
				break;
			}
		}
		
		if (result == null) {
			result = instance.createSlot();
			result.setDefiningFeature(property);
		}
		
		if (value instanceof Collection<?>) {
			for (Object e : (Collection<?>) value) {
				addValue(result, e);
			}
		} else {
			addValue(result, value);
		}
		
		return result;
	}
	
	protected void clearValue(
			InstanceSpecification instance,
			Property property) {
		
		for (Slot slot : instance.getSlots()) {
			if (slot.getDefiningFeature() == property) {
				instance.getSlots().remove(slot);
				break;
			}
		}
	}
	
	protected Slot addValue(
			InstanceSpecification instance,
			Property property,
			Object value) {
		
		Slot result = null;
		for (Slot slot : instance.getSlots()) {
			if (slot.getDefiningFeature() == property) {
				result = slot;
				break;
			}
		}
		
		if (result == null) {
			result = setValue(instance, property, value);
		} else {
			addValue(result, value);
		}
		
		return result;
	}
	
	protected ValueSpecification addValue(Slot slot, Object value) {
		ValueSpecification result;
		
		if (value instanceof InstanceSpecification) {
			InstanceValue valueSpec = (InstanceValue) slot.createValue(
					null, null, UMLPackage.eINSTANCE.getInstanceValue());
			valueSpec.setInstance((InstanceSpecification) value);
			result = valueSpec;
		} else if (value instanceof String) {
			LiteralString valueSpec = (LiteralString) slot.createValue(
					null, null, UMLPackage.eINSTANCE.getLiteralString());
			valueSpec.setValue((String) value);
			result = valueSpec;
		} else if (value instanceof Integer) {
			if (slot.getDefiningFeature().getType() == getUMLUnlimitedNatural()) {
				LiteralUnlimitedNatural valueSpec =
					(LiteralUnlimitedNatural) slot.createValue(
						null, null, UMLPackage.eINSTANCE.getLiteralUnlimitedNatural());
				valueSpec.setValue(((Integer) value).intValue());
				result = valueSpec;
			} else {
				LiteralInteger valueSpec = (LiteralInteger) slot.createValue(
						null, null, UMLPackage.eINSTANCE.getLiteralInteger());
				valueSpec.setValue(((Integer) value).intValue());
				result = valueSpec;
			}
		} else if (value instanceof Boolean) {
			LiteralBoolean valueSpec = (LiteralBoolean) slot.createValue(
					null, null, UMLPackage.eINSTANCE.getLiteralBoolean());
			valueSpec.setValue(((Boolean) value).booleanValue());
			result = valueSpec;
		} else if (value == null) {
			LiteralNull valueSpec = (LiteralNull) slot.createValue(
					null, null, UMLPackage.eINSTANCE.getLiteralNull());
			result = valueSpec;
		} else {
			throw new IllegalArgumentException("Unrecognized slot value: " + value);
		}
		
		return result;
	}
	
	protected Object getValue(InstanceSpecification owner, Property property) {
		for (Slot slot : owner.getSlots()) {
			if (slot.getDefiningFeature() == property) {
				EList<ValueSpecification> values = slot.getValues();
				
				if (!property.isMultivalued()) {
					return values.isEmpty()? null : convert(values.get(0));
				} else {
					EList<Object> result = new BasicEList.FastCompare<Object>(values.size());
					
					for (ValueSpecification value : values) {
						result.add(convert(value));
					}
					
					return result;
				}
			}
		}
		
		
		return null;
	}
	
	protected Object convert(ValueSpecification value) {
		Object result;
		
		if (value instanceof InstanceValue) {
			result = ((InstanceValue) value).getInstance();
		} else if (value instanceof LiteralString) {
			result = ((LiteralString) value).stringValue();
		} else if (value instanceof LiteralInteger) {
			result = ((LiteralInteger) value).integerValue();
		} else if (value instanceof LiteralUnlimitedNatural) {
			result = ((LiteralUnlimitedNatural) value).integerValue();
		} else if (value instanceof LiteralBoolean) {
			result = ((LiteralBoolean) value).booleanValue();
		} else if (value instanceof LiteralNull) {
			result = null;
		} else {
			throw new IllegalArgumentException("Unrecognized slot value: " + value);
		}
		
		return result;
	}
	
	public InstanceSpecification link(
			Package pkg,
			InstanceSpecification instance1, Property end1,
			InstanceSpecification instance2, Property end2,
			Association assoc) {
		
		InstanceSpecification result = instantiate(pkg, assoc);
		
		setValue(result, end1, instance2);
		if ( end1.getOwningAssociation() == null) {
			addValue(instance1, end1, instance2);
		}
		
		setValue(result, end2, instance1);
		if ( end2.getOwningAssociation() == null) {
			addValue(instance2, end2, instance1);
		}
		
		return result;
	}
	
	protected InstanceSpecification link(
			Package pkg,
			InstanceSpecification instance1, Property end1,
			Object[] qualifierValues,
			InstanceSpecification instance2, Property end2,
			Association assoc) {
		
		InstanceSpecification result = link(pkg, instance1, end1, instance2, end2, assoc);
		
		List<Property> qualifiers = end1.getQualifiers();
		int count = qualifiers.size();
		
		for (int i = 0; i < count; i++) {
			setValue(result, qualifiers.get(i), qualifierValues[i]);
		}
		
		return result;
	}
}
